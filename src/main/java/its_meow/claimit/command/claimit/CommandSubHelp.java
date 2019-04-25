package its_meow.claimit.command.claimit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import static net.minecraft.util.text.TextFormatting.*;

import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;

public class CommandSubHelp extends CommandBase {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit help <commands/permissions/topics>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
			sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:");
			sendMessage(sender, GREEN + "/claimit help commands");
			sendMessage(sender, GREEN + "/claimit help permissions");
			sendMessage(sender, GREEN + "/claimit help topics");
		}

		if(args.length == 1) {
			String choice = args[0];
			// Basic tree
			if(choice.equalsIgnoreCase("commands")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:"  + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "claimcommands");
				sendMessage(sender, GREEN + "misccommands");
			}
			if(choice.equalsIgnoreCase("permissions")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "memberperms");
				sendMessage(sender, GREEN + "toggleperms");
			}
			if(choice.equalsIgnoreCase("topics")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "creating");
				sendMessage(sender, GREEN + "members");
				sendMessage(sender, GREEN + "toggles");
				sendMessage(sender, GREEN + "management");
			}
			
			// Second tree - commands
			if(choice.equalsIgnoreCase("claimcommands")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "claim delete");
				sendMessage(sender, GREEN + "claim info");
				sendMessage(sender, GREEN + "claim permission");
				sendMessage(sender, GREEN + "claim permission list");
				sendMessage(sender, GREEN + "claim list");
				sendMessage(sender, GREEN + "claim deleteall");
				sendMessage(sender, GREEN + "claim setname");
				sendMessage(sender, GREEN + "claim toggle");
			}
			if(choice.equalsIgnoreCase("misccommands")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "admin");
				sendMessage(sender, GREEN + "cancel");
				sendMessage(sender, GREEN + "confirm");
				sendMessage(sender, GREEN + "help");
			}
			
			// Second tree - permissions
			if(choice.equalsIgnoreCase("memberperms")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Member Permissions:");
				for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
					sendMessage(sender, GREEN + perm.parsedName + ": " + YELLOW + perm.helpInfo);
				}
			}
			if(choice.equalsIgnoreCase("toggleperms")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Toggle Permissions:");
				for(ClaimPermissionToggle perm : ClaimPermissionRegistry.getTogglePermissions()) {
					sendMessage(sender, GREEN + perm.parsedName + ": " + YELLOW + perm.helpInfo + BLUE + " (Default: " + perm.defaultValue + ")");
				}
			}
			
			//Second tree - topics
			
			
		}
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
