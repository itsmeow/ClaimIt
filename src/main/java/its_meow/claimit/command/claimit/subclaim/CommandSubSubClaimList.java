package its_meow.claimit.command.claimit.subclaim;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import java.util.ArrayList;
import java.util.List;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.CommandChatStyle;
import its_meow.claimit.util.text.FTC;
import its_meow.claimit.util.text.TextComponentStyled;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubSubClaimList extends CommandCIBase {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim list [claimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Lists all subclaims in specified claim. Click names to view info on them.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String claimname = null;
        if(args.length == 1) {
            claimname = args[0];
        } else if(args.length > 1) {
            throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
        }
        ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimname, sender);
        if(claim == null) {
            throw new CommandException("No claim found!");
        }
        if(claim.getSubClaims().size() > 0) {
            int i = 0;
            for(SubClaimArea subclaim : claim.getSubClaims()) {
                i++;
                if(i == 1) {
                    sendMessage(sender, new FTC(DARK_BLUE, "Subclaim list for claim "), new FTC(GREEN, claim.getDisplayedViewName()), new FTC(DARK_BLUE, ":"));
                }
                sender.sendMessage(new TextComponentStyled(BLUE + "Name: " + DARK_GREEN + subclaim.getDisplayedViewName(), new CommandChatStyle("/ci subclaim info " + claim.getDisplayedViewName(), true, "Click for info.")));
            }
        } else {
            sendMessage(sender, RED, "There are no subclaims in this claim!");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
        }
        return new ArrayList<String>();
    }

    @Override
    public String getPermissionString() {
        return "claimit.claim.list";
    }

}