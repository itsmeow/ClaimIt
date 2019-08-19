package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.config.ClaimItAPIConfig;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.CommandChatStyle;
import its_meow.claimit.util.text.FTC;
import its_meow.claimit.util.text.FTC.Form;
import its_meow.claimit.util.text.TeleportXYChatStyle;
import its_meow.claimit.util.text.TextComponentStyled;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandSubClaimInfo extends CommandCIBase {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claim info [name]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Displays information about a claim. No arguments attempts getting claim for your location. Supplying a name as an argument will give info on that claim. " + (CommandUtils.isAdmin(sender) ? "Admins must use true names." : "");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        /*
         * Info by current location
         */
        if(args.length == 0) {
            if(sender instanceof EntityPlayer) {
                ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
                if(claim != null) {
                    outputClaimInfo(claim, (EntityPlayer) sender);
                } else {
                    sendMessage(sender, RED, "There is no claim here!");
                }
            }
        }


        /*
         * Info by claim name
         */
        if(args.length == 1) {
            if(sender instanceof EntityPlayer) { 
                EntityPlayer player = ((EntityPlayer) sender);
                ClaimArea claim = CommandUtils.getClaimWithName(args[0], player);
                if(claim != null) {
                    outputClaimInfo(claim, player);
                } else {
                    if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.claim.info.others") && (claim = ClaimManager.getManager().getClaimByTrueName(args[0])) != null) {
                        outputClaimInfo(claim, player);
                    } else {
                        sendMessage(sender, RED, "No claim with this name" + (CommandUtils.isAdmin(sender) ? "!" : " that you own!"));
                    }
                }
            } else if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.claim.info.others")) {
                ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(args[0]);
                if(claim != null) {
                    outputClaimInfo(claim, sender);
                } else {
                    sendBMessage(sender, "No claim with this true name found!");
                }
            }
        }

        if(args.length > 1) {
            sendMessage(sender, RED, "Invalid argument count. Maximum 1 argument after \"info\". Usage: " + this.getUsage(sender));
        }
    }

    private static void outputClaimInfo(ClaimArea claim, ICommandSender sender) {
        World worldIn = sender.getEntityWorld();
        BlockPos[] corners = claim.getTwoMainClaimCorners();
        UUID owner = claim.getOwner();
        String ownerName = CommandUtils.getNameForUUID(owner, worldIn.getMinecraftServer());
        if(ownerName == null) {
            ownerName = worldIn.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(owner).getName();
        }
        int dim = claim.getDimensionID();

        sendMessage(sender, new FTC(BLUE, Form.BOLD, "Information for claim owned by "), new FTC(GREEN, Form.BOLD, ownerName), new FTC(BLUE, Form.BOLD, ":"));
        sendMessage(sender, new FTC(BLUE, "Claim Name: "), new FTC(YELLOW, claim.getDisplayedViewName()));
        runIfAdmin(sender, ()->sendMessage(sender, new FTC(BLUE, "Claim True Name: "), new FTC(YELLOW, claim.getTrueViewName())));
        sendMessage(sender, new FTC(BLUE, "Dimension: "), new FTC(DARK_PURPLE, dim + ""));
        sendMessage(sender, new FTC(BLUE, "Area: "), new FTC(AQUA, (claim.getSideLengthX() + 1) + ""), new FTC(BLUE, "x"), new FTC(AQUA, (claim.getSideLengthZ() + 1) + ""), new FTC(BLUE, " ("), new FTC(AQUA, claim.getArea() + ""), new FTC(BLUE, ")"));
        sendAdminStyleMessage(sender, BLUE + "Corner 1: " + DARK_PURPLE + (corners[0].getX()) + BLUE + ", " + DARK_PURPLE + (corners[0].getZ()), new TeleportXYChatStyle(claim.getDimensionID(), corners[0].getX(), corners[0].getZ()));
        sendAdminStyleMessage(sender, BLUE + "Corner 2: " + DARK_PURPLE + (corners[1].getX()) + BLUE + ", " + DARK_PURPLE + (corners[1].getZ()), new TeleportXYChatStyle(claim.getDimensionID(), corners[1].getX(), corners[1].getZ()));
        Style viewSubclaimsStyle = new CommandChatStyle("/ci subclaim list " + (CommandUtils.isAdmin(sender) ? claim.getTrueViewName() : claim.getDisplayedViewName()), true, "Click to view subclaim list").setColor(YELLOW).setUnderlined(true).setItalic(true);
        if(CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.command.claimit.claim.permission.list.others")) {
            ITextComponent viewMembers = new TextComponentStyled("View Members", new CommandChatStyle("/ci claim permission list " + (CommandUtils.isAdmin(sender) ? claim.getTrueViewName() : claim.getDisplayedViewName()), true, "Click to view claim members").setColor(GREEN).setUnderlined(true).setItalic(true));
            if(ClaimItAPIConfig.enable_subclaims) {
                viewMembers.appendSibling(new TextComponentString(" ").setStyle(new Style().setUnderlined(false))).appendSibling(new TextComponentStyled("View Subclaims", viewSubclaimsStyle));
            }
            sendMessage(sender, viewMembers);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.claim.info";
    }

}