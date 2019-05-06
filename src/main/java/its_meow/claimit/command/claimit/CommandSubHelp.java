package its_meow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.command.CommandCIBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelp extends CommandCIBase {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit help <command/permission/topic>";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "The help command. Are you okay? Asking for help on getting help while also getting help? What are you doing? Put another command to view help on it.";
    }

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
			sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:");
			sendMessage(sender, GREEN + "/claimit help command");
			sendMessage(sender, GREEN + "/claimit help permissions");
			sendMessage(sender, GREEN + "/claimit help topics");
		}

		if(args.length == 1) {
			String choice = args[0];
			// Basic tree
			if(choice.equalsIgnoreCase("command")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:"  + YELLOW + " /claimit help <choice>");
				
			}
			if(choice.equalsIgnoreCase("permission")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "member");
				sendMessage(sender, GREEN + "toggle");
			}
			if(choice.equalsIgnoreCase("topic")) {
				sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
				sendMessage(sender, GREEN + "creating");
				sendMessage(sender, GREEN + "members");
				sendMessage(sender, GREEN + "toggles");
				sendMessage(sender, GREEN + "management");
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

}
