package its_meow.claimit.util;

import java.util.HashMap;

import net.minecraft.command.ICommandSender;

public class ConfirmationManager {
	
	private static ConfirmationManager instance = null;
	
	private ConfirmationManager() {
		 confirmActions = new HashMap<ICommandSender, EnumConfirmableAction>();
	}

	public static ConfirmationManager getManager() {
		if(instance == null) {
			instance = new ConfirmationManager();
		}

		return instance;
	}
	
	private HashMap<ICommandSender, EnumConfirmableAction> confirmActions;
	public boolean needsConfirm(ICommandSender sender) {
		return confirmActions.containsKey(sender);
	}
	
	public EnumConfirmableAction getAction(ICommandSender sender) {
		return confirmActions.get(sender);
	}
	
	public boolean addConfirm(ICommandSender sender, EnumConfirmableAction action) {
		if(needsConfirm(sender)) {
			return false;
		} else {
			confirmActions.put(sender, action);
			return true;
		}
	}
	
	public boolean removeConfirm(ICommandSender sender) {
		if(!needsConfirm(sender)) {
			return false;
		} else {
			confirmActions.remove(sender);
			return true;
		}
	}
	
	public void removeAllConfirms() {
		this.confirmActions.clear();
	}
	
	public static enum EnumConfirmableAction {
	    DELETEALL,
	    CLEARMEMBERS;
	}
	
}
