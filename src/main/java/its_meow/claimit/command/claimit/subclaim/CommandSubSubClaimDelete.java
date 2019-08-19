package its_meow.claimit.command.claimit.subclaim;

import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubSubClaimDelete extends CommandCIBase {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim delete [claimname) (subclaimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Deletes a subclaim. With no arguments, attempts subclaim at your location. Specify a subclaim name as an argument to delete that subclaim within current claim. Specify both to delete in another claim" + (CommandUtils.isAdmin(sender) ? "Admins must specify a true claim name if using arguments." : "");
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim.delete";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        SubClaimArea subClaim = CommandUtils.getSubClaimWithNamesOrLocation(0, args, sender);
        ClaimArea claim = subClaim.getParent();
        if(!CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.command.claimit.subclaim.delete.others")) {
            throw new CommandException("You do not have permission to delete this subclaim!");
        }

        boolean success = claim.removeSubClaim(subClaim);
        sendMessage(sender, YELLOW, (success ? "Subclaim deleted." : "Subclaim was not deleted, something canceled it."));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            if(!ClaimManager.getManager().isBlockInAnyClaim(sender.getEntityWorld(), sender.getPosition())) {
                return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
            } else {
                return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getSubclaimNames(null, sender, ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition())));
            }
        } else if(args.length == 2) {
            ClaimArea claim = CommandUtils.getClaimWithName(args[0], sender);
            if(claim != null) {
                return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getSubclaimNames(null, sender, claim));
            } else {
                return new ArrayList<String>();
            }
        } else {
            return new ArrayList<String>();
        }
    }

}