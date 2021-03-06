package dev.itsmeow.claimit.command.claimit;

import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.ConfirmationManager;
import dev.itsmeow.claimit.util.command.IConfirmable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class CommandSubConfirm extends CommandCIBase {

	@Override
	public String getName() {
		return "confirm";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit confirm";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "Confirms a confirmable action, and runs its result (such as deleting all claims)";
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ConfirmationManager mgr = ConfirmationManager.getManager();
		if(mgr.needsConfirm(sender)) {
		    IConfirmable action = mgr.getAction(sender);
			sendMessage(sender, TextFormatting.GREEN, "Confirmed action: " + action.getConfirmName());
			ConfirmationManager.getManager().doAction(server, sender, action);
			mgr.removeConfirm(sender);
		} else {
			sendMessage(sender, TextFormatting.RED, "You have no actions to confirm!");
		}
	}

    @Override
    public String getPermissionString() {
        return "claimit.confirm";
    }

}
