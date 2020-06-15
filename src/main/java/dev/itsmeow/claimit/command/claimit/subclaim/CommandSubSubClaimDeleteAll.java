package dev.itsmeow.claimit.command.claimit.subclaim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.RED;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.claim.ClaimManager;
import dev.itsmeow.claimit.api.claim.SubClaimArea;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.command.ConfirmationManager;
import dev.itsmeow.claimit.util.command.IConfirmable;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubSubClaimDeleteAll extends CommandCIBase implements IConfirmable {

    @Override
    public String getName() {
        return "deleteall";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim deleteall [claimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Deletes all subclaims in specified claim. Must be confirmed via '/claimit confirm'. Can be canceled via '/claimit cancel'";
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim.deleteall";
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
        if(claim != null) {
            if(CommandUtils.isAdminWithNodeOrOwner(sender, claim, "claimit.command.claimit.subclaim.deleteall.others")) {
                if(!ConfirmationManager.getManager().needsConfirm(sender)) {
                    ConfirmationManager.getManager().addConfirm(sender, this, new String[] {claim.getTrueViewName()});
                    sendMessage(sender, new FTC(DARK_RED, "This will delete ALL subclaims in this claim! Are you sure you want to do this? Run "), new FTC(AQUA, "'/claimit confirm'"), new FTC(DARK_RED, " to confirm. If you do not want to do this, run "), new FTC(AQUA, "'/claimit cancel'"), new FTC(DARK_RED, "."));
                } else {
                    sendMessage(sender, RED, "Canceling preexisting action. Run this command again to delete all subclaims.");
                    ConfirmationManager.getManager().removeConfirm(sender);
                }
            } else {
                throw new CommandException("You do not have permission to delete this claim's subclaims!");
            }
        } else {
            throw new CommandException("Could not get claim!");
        }
    }

    @Override
    public String getConfirmName() {
        return "subclaim-deleteall";
    }

    @Override
    public void doAction(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            String claimName = args[0];
            ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(claimName);
            if(claim != null) {
                if(!CommandUtils.isAdminWithNodeOrOwner(sender, claim, "claimit.command.claimit.subclaim.deleteall.others")) {
                    ConfirmationManager.getManager().removeConfirm(sender);
                    throw new CommandException("You do not have permission to delete this claim's subclaims!");
                }
                ImmutableSet<SubClaimArea> subclaims = claim.getSubClaims();
                if(subclaims == null || subclaims.size() == 0) {
                    ConfirmationManager.getManager().removeConfirm(sender);
                    throw new CommandException("No subclaims were found.");
                }
                boolean failed = false;
                for(SubClaimArea subclaim : subclaims) { // subclaims is immutable, so there is no concurrent modification
                    boolean s = claim.removeSubClaim(subclaim);
                    if(!s) failed = true;
                }
                sendBMessage(sender, "Removed all subclaims." + (failed ? " Something prevented some subclaims from being removed." : ""));
                ConfirmationManager.getManager().removeConfirm(sender);
            } else {
                ConfirmationManager.getManager().removeConfirm(sender);
                throw new CommandException("A subclaim deletion was confirmed, but the claim supplied was deleted! Action failed.");
            }
        } else {
            throw new RuntimeException("A subclaim deletion was confirmed, but no claim was supplied! What did you do!?!?");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
        }
        return new ArrayList<String>();
    }

}