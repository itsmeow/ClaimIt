package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimDelete extends CommandCIBase {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claim delete [claimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Deletes a claim. With no arguments, attempts claim at your location. Specify a claim name as an argument to delete that claim. " + (CommandUtils.isAdmin(sender) ? "Admins must specify a true name if using arguments." : "");
    }

    @Override
    protected String getPermissionString() {
        return "claimit.claim.delete";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        String claimName = null;
        if(args.length == 1) {
            claimName = args[0];
        }
        ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);
        if(claimName == null && claim == null) {
            throw new CommandException("There is no claim here!");
        } else if(claim == null) {
            throw new CommandException("There is no claim with this name you own!");
        }
        if(!CommandUtils.equivalentOwnerWithNode(sender, claim, "claimit.command.claimit.claim.delete.others")) {
            throw new CommandException("You do not have permission to delete this claim!");
        }

        boolean success = ClaimManager.getManager().deleteClaim(claim);
        sendMessage(sender, YELLOW + (success ? "Claim deleted." : "Claim was not deleted, something canceled it."));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
        } else {
            return new ArrayList<String>();
        }
    }

}