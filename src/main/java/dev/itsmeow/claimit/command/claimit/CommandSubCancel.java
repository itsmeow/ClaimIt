package dev.itsmeow.claimit.command.claimit;

import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.ConfirmationManager;
import dev.itsmeow.claimit.util.command.IConfirmable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class CommandSubCancel extends CommandCIBase {

	@Override
	public String getName() {
		return "cancel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit cancel";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "Cancels a confirmable action. (Deleting all claims, etc)";
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ConfirmationManager mgr = ConfirmationManager.getManager();
		if(mgr.needsConfirm(sender)) {
			IConfirmable action = mgr.getAction(sender);
			sendMessage(sender, TextFormatting.GREEN, "Cancelled action: " + action.getConfirmName());
			mgr.removeConfirm(sender);
		} else {
			sendMessage(sender, TextFormatting.RED, "You have no action to cancel!");
		}
	}

    @Override
    public String getPermissionString() {
        return "claimit.cancel";
    }

}
