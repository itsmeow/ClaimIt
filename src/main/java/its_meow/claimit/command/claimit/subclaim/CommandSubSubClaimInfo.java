package its_meow.claimit.command.claimit.subclaim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.ClaimInfoText;
import its_meow.claimit.util.text.CommandChatStyle;
import its_meow.claimit.util.text.FTC;
import its_meow.claimit.util.text.FTC.Form;
import its_meow.claimit.util.text.TeleportXYChatStyle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSubSubClaimInfo extends CommandCIBase {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim info [claimname) (subclaimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Displays information about a subclaim. No arguments attempts getting subclaim for your location. Arguments one and two are used for claim/subclaim names" + (CommandUtils.isAdmin(sender) ? "Admins must use true names." : "");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length > 2) {
            sendMessage(sender, RED, "Invalid argument count. Usage: " + this.getUsage(sender));
        }
        SubClaimArea subClaim = CommandUtils.getSubClaimWithNamesOrLocation(0, args, sender);
        outputClaimInfo(subClaim, sender);
    }

    private static void outputClaimInfo(SubClaimArea subClaim, ICommandSender sender) {
        World worldIn = sender.getEntityWorld();
        BlockPos[] corners = subClaim.getTwoMainClaimCorners();

        sendMessage(sender, new FTC(BLUE, Form.BOLD, "Information for subclaim of claim "), new ClaimInfoText(subClaim.getParent(), GREEN, Form.BOLD_UNDERLINE), new FTC(BLUE, Form.BOLD, ":"));
        sendMessage(sender, new FTC(BLUE, "Subclaim Name: "), new FTC(YELLOW, subClaim.getDisplayedViewName()));
        sendMessage(sender, new FTC(BLUE, "Area: "), new FTC(AQUA, (subClaim.getSideLengthX() + 1) + ""), new FTC(BLUE, "x"), new FTC(AQUA, (subClaim.getSideLengthZ() + 1) + ""), new FTC(BLUE, " ("), new FTC(AQUA, subClaim.getArea() + ""), new FTC(BLUE, ")"));
        sendAdminStyleMessage(sender, BLUE + "Corner 1: " + DARK_PURPLE + (corners[0].getX()) + BLUE + ", " + DARK_PURPLE + (corners[0].getZ()), new TeleportXYChatStyle(subClaim.getDimensionID(), corners[0].getX(), corners[0].getZ()));
        sendAdminStyleMessage(sender, BLUE + "Corner 2: " + DARK_PURPLE + (corners[1].getX()) + BLUE + ", " + DARK_PURPLE + (corners[1].getZ()), new TeleportXYChatStyle(subClaim.getDimensionID(), corners[1].getX(), corners[1].getZ()));
        if(CommandUtils.isAdminWithNodeOrManage(sender, subClaim, "claimit.subclaim.permission.list.others"))
            sendSMessage(sender, "View Members", new CommandChatStyle("/ci subclaim permission list " + (CommandUtils.isAdmin(sender) ? subClaim.getParent().getTrueViewName() : subClaim.getParent().getDisplayedViewName()) + " " + subClaim.getDisplayedViewName(), true, "Click to view subclaim members").setColor(GREEN).setUnderlined(true).setItalic(true));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1 || args.length == 2) {
            return CommandUtils.getSubclaimCompletions(null, 0, args, sender);
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim.info";
    }

}