package its_meow.claimit.command.claimit;

import its_meow.claimit.EnumConfirmableAction;
import its_meow.claimit.command.ConfirmationManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubCancel extends CommandBase {

	@Override
	public String getName() {
		return "cancel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit cancel";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ConfirmationManager mgr = ConfirmationManager.getManager();
		if(mgr.needsConfirm(sender)) {
			EnumConfirmableAction action = mgr.getAction(sender);
			sendMessage(sender, "Cancelled action: " + action.toString().toLowerCase());
			mgr.removeConfirm(sender);
		} else {
			sendMessage(sender, "You have no action to cancel!");
		}
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
