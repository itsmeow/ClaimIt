package dev.itsmeow.claimit.util.command;

import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ConfirmationManager {
	
	private static ConfirmationManager instance = null;
	
	private ConfirmationManager() {
		 confirmActions = new HashMap<ICommandSender, Pair<IConfirmable, String[]>>();
	}

	public static ConfirmationManager getManager() {
		if(instance == null) {
			instance = new ConfirmationManager();
		}

		return instance;
	}
	
	private HashMap<ICommandSender, Pair<IConfirmable, String[]>> confirmActions;
	
	public boolean needsConfirm(ICommandSender sender) {
		return confirmActions.containsKey(sender);
	}
	
	public IConfirmable getAction(ICommandSender sender) {
		return confirmActions.get(sender).getLeft();
	}
	
	public boolean addConfirm(ICommandSender sender, IConfirmable action, String[] args) {
		if(needsConfirm(sender)) {
			return false;
		} else {
			confirmActions.put(sender, Pair.of(action, args));
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

    public void doAction(MinecraftServer server, ICommandSender sender, IConfirmable action) throws CommandException {
        action.doAction(server, sender, confirmActions.get(sender).getRight());
    }
	
}
