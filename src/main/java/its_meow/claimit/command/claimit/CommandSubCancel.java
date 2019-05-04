package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.ConfirmationManager;
import its_meow.claimit.util.ConfirmationManager.EnumConfirmableAction;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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

}
