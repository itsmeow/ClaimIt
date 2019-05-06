package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.DARK_BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.ClaimPage;
import its_meow.claimit.util.ClaimPageTracker;
import its_meow.claimit.util.CommandUtils;
import its_meow.claimit.util.text.ClaimInfoChatStyle;
import its_meow.claimit.util.text.PageChatStyle;
import its_meow.claimit.util.text.TeleportXYChatStyle;
import its_meow.claimit.util.text.TextComponentStyled;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubClaimList extends CommandCIBase {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        if(sender instanceof EntityPlayer) {
            if(ClaimManager.getManager().isAdmin((EntityPlayer) sender)) {
                return "/claimit claim list [username] [page]";
            }
        }
        return "/claimit claim list";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return CommandUtils.isAdmin(sender) ? "Lists all claims on the server, takes a page number (or no page for 1) as an argument, can filter to a player (first argument). Next Page is clickable." : "Lists all claims you own. Click names to view info on them.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
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
        boolean admin = CommandUtils.isAdmin(sender);
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
            if(!ClaimManager.getManager().isAdmin(player)) {
                final Set<ClaimArea> claims = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
                if(claims != null) {
                    int i = 0;
                    for(ClaimArea claim : claims) {
                        i++;
                        if(i == 1) {
                            sendMessage(sender, DARK_BLUE + "Claim List for " + GREEN + player.getName() + DARK_BLUE + ":");
                        }
                        sender.sendMessage(new TextComponentStyled(BLUE + "Name: " + DARK_GREEN + claim.getDisplayedViewName(), new ClaimInfoChatStyle(claim.getDisplayedViewName())));
                    }
                } else {
                    sendMessage(sender, RED + "You don't own any claims!");
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
                sendMessage(sender, DARK_PURPLE + "" + BOLD + "-----Page " + pg + " of " + ClaimPageTracker.getMaxPage(filter) +"-----");
                int i = (pg - 1) * 3;
                for(ClaimArea claim : cPage.getClaimsInPage()) {
                    sendMessage(sender, DARK_RED  + "" + UNDERLINE + "Claim " + (i + 1));
                    sendMessage(sender, BLUE + "Owner: " + GREEN + CommandUtils.getNameForUUID(claim.getOwner(), server));
                    sender.sendMessage(new TextComponentStyled(BLUE + "Claim True Name: " + YELLOW + claim.getTrueViewName(), new ClaimInfoChatStyle(claim.getTrueViewName())));
                    sendMessage(sender, BLUE + "Dimension: " + DARK_PURPLE + claim.getDimensionID());
                    sender.sendMessage(new TextComponentStyled(BLUE + "Location: " + DARK_PURPLE + (claim.getMainPosition().getX()) + BLUE + ", " + DARK_PURPLE + (claim.getMainPosition().getZ()), new TeleportXYChatStyle(claim.getDimensionID(), claim.getMainPosition().getX(), claim.getMainPosition().getZ())));
                    i++;
                }
                if(ClaimPageTracker.getPage(filter, pg) != null) {
                    sender.sendMessage(new TextComponentStyled(GREEN + "" + ITALIC + "" + UNDERLINE + "Next Page", new PageChatStyle("claimit claim list", ClaimManager.getManager().isAdmin(player), String.valueOf(pg + 1), pName)));
                }
            }
        } else { // Sender is console!
            sendMessage(sender, "Detected server console. Getting all claims. Specify a name to get only their claims.");
            int i = 0;
            for(ClaimArea claim : ClaimManager.getManager().getClaimsList()) {
                if(filter == null || claim.isTrueOwner(filter)) {
                    i++;
                    sendMessage(sender, "####CLAIM INFO####");
                    sendMessage(sender, "Claim #" + i + ", owned by: " + CommandUtils.getNameForUUID(claim.getOwner(), server));
                    sendMessage(sender, "Claim True Name: " + claim.getTrueViewName());
                    sendMessage(sender, "Dimension: " + claim.getDimensionID());
                    sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
                }
            }
            if(i == 0) {
                sendMessage(sender, "No claims found.");
            }
        }

        if(args.length > 2) {
            sendMessage(sender, "Invalid amount of arguments. Usage: " + this.getUsage(sender));
        }
    }

}