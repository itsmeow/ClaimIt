package dev.itsmeow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dev.itsmeow.claimit.api.AdminManager;
import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.claim.ClaimManager;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.ClaimPage;
import dev.itsmeow.claimit.util.ClaimPageTracker;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.ClaimInfoChatStyle;
import dev.itsmeow.claimit.util.text.FTC;
import dev.itsmeow.claimit.util.text.PageChatStyle;
import dev.itsmeow.claimit.util.text.TeleportXYChatStyle;
import dev.itsmeow.claimit.util.text.TextComponentStyled;
import dev.itsmeow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimList extends CommandCIBase {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.command.claimit.claim.list.others") ? "/claimit claim list [username] [page]" : "/claimit claim list";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.command.claimit.claim.list.others") ? "Lists all claims on the server, takes a page number (or no page for 1) as an argument, can filter to a player (first argument). Next Page is clickable." : "Lists all claims you own. Click names to view info on them.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        /* 
         * Filter by name (admin only) 
         */
        String pName = null;
        UUID filter = null;
        String page = null;
        int pg = 0;
        boolean error = false;
        boolean admin = CommandUtils.isAdminNoded(sender, "claimit.command.claimit.claim.list.others");
        if(args.length >= 1 && admin) {
            try {
                Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                error = true;
            }
            if(!error) {
                page = args[0];
            }
        }

        if(error && args.length >= 1 && admin) {
            pName = args[0];
            GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(pName);
            if(profile != null && profile.getName().equals(pName)) { // Found the profile!
                filter = profile.getId();
            } else {
                throw new PlayerNotFoundException("Invalid player: " + args[0]);
            }
            if(args.length == 2) {
                page = args[1];
            } else {
                page = "1";
            }
        }
        if(args.length == 0 && admin) {
            page = "1";
        }
        if(admin) {
            try {
                pg = Integer.parseInt(page);
            } catch(NumberFormatException e) {
                throw new CommandException("Invalid page number \"" + page + "\"");
            }

            int maxPg = ClaimPageTracker.getMaxPage(null);
            if(maxPg < 1) {
                throw new CommandException("No claims exist.");
            }
        }

        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            if(!admin) {
                final Set<ClaimArea> claims = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
                if(claims != null && claims.size() > 0) {
                    int i = 0;
                    for(ClaimArea claim : claims) {
                        i++;
                        if(i == 1) {
                            sendMessage(sender, new FTC(DARK_BLUE, "Claim List for "), new FTC(GREEN, player.getName()), new FTC(DARK_BLUE, ":"));
                        }
                        sender.sendMessage(new TextComponentStyled(BLUE + "Name: " + DARK_GREEN + claim.getDisplayedViewName(), new ClaimInfoChatStyle(claim.getDisplayedViewName())));
                    }
                } else {
                    sendMessage(sender, RED, "You don't own any claims!");
                }
            } else {
                if(filter == null) {
                    //sendMessage(sender, GREEN + "You are admin. Getting all claims. Specify a name to get only their claims.");
                }
                if(ClaimManager.getManager().getClaimsList().size() == 0) {
                    throw new CommandException("No claims found."); 
                }

                ClaimPage cPage = ClaimPageTracker.getPage(filter, pg - 1);
                if(cPage == null || cPage.getPageSize() == 0) {
                    throw new CommandException("Empty page: " + pg);
                }
                sendMessage(sender, new FTC(DARK_PURPLE, Form.BOLD, "-----Page " + pg + " of " + ClaimPageTracker.getMaxPage(filter) + "-----"));
                int i = (pg - 1) * 3;
                for(ClaimArea claim : cPage.getClaimsInPage()) {
                    sendMessage(sender, DARK_RED, Form.UNDERLINE, "Claim " + (i + 1));
                    sendMessage(sender, new FTC(BLUE, "Owner: "), new FTC(GREEN, CommandUtils.getNameForUUID(claim.getOwner(), server)));
                    sender.sendMessage(new TextComponentStyled(BLUE + "Claim True Name: " + YELLOW + claim.getTrueViewName(), new ClaimInfoChatStyle(claim.getTrueViewName())));
                    sendMessage(sender, new FTC(BLUE, "Dimension: "), new FTC(DARK_PURPLE, claim.getDimensionID() + ""));
                    sender.sendMessage(new TextComponentStyled(BLUE + "Location: " + DARK_PURPLE + (claim.getMainPosition().getX()) + BLUE + ", " + DARK_PURPLE + (claim.getMainPosition().getZ()), new TeleportXYChatStyle(claim.getDimensionID(), claim.getMainPosition().getX(), claim.getMainPosition().getZ())));
                    i++;
                }
                if(ClaimPageTracker.getPage(filter, pg) != null) {
                    sender.sendMessage(new TextComponentStyled(GREEN + "" + ITALIC + "" + UNDERLINE + "Next Page", new PageChatStyle("claimit claim list", AdminManager.isAdmin(player), String.valueOf(pg + 1), pName)));
                }
            }
        } else if(sender.canUseCommand(4, "claimit.claim.list.others")) {
            sendBMessage(sender, "Detected server console. Getting all claims. Specify a name to get only their claims.");
            int i = 0;
            for(ClaimArea claim : ClaimManager.getManager().getClaimsList()) {
                if(filter == null || claim.isOwner(filter)) {
                    i++;
                    sendBMessage(sender, "####CLAIM INFO####");
                    sendBMessage(sender, "Claim #" + i + ", owned by: " + CommandUtils.getNameForUUID(claim.getOwner(), server));
                    sendBMessage(sender, "Claim True Name: " + claim.getTrueViewName());
                    sendBMessage(sender, "Dimension: " + claim.getDimensionID());
                    sendBMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
                }
            }
            if(i == 0) {
                sendBMessage(sender, "No claims found.");
            }
        }

        if(args.length > 2) {
            sendMessage(sender, RED, "Invalid amount of arguments. Usage: " + this.getUsage(sender));
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(CommandUtils.isAdmin(sender)) {
            return CommandUtils.getPossiblePlayers(null, server, sender, args);
        }
        return new ArrayList<String>();
    }

    @Override
    public String getPermissionString() {
        return "claimit.claim.list";
    }

}