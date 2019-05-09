package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.ConfirmationManager;
import its_meow.claimit.util.command.IConfirmable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ConfirmationManager mgr = ConfirmationManager.getManager();
		if(mgr.needsConfirm(sender)) {
		    IConfirmable action = mgr.getAction(sender);
			sendMessage(sender, "Confirmed action: " + action.getConfirmName());
			ConfirmationManager.getManager().doAction(server, sender, action);
			mgr.removeConfirm(sender);
		} else {
			sendMessage(sender, "You have no actions to confirm!");
		}
	}

}
