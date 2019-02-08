package its_meow.claimit.command.claimit.claim;

import its_meow.claimit.claim.ClaimArea;
import its_meow.claimit.command.CommandUtils;
import its_meow.claimit.permission.ClaimPermissionRegistry;
import its_meow.claimit.permission.ClaimPermissionToggle;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubClaimToggle extends CommandBase {

	@Override
	public String getName() {
		return "toggle";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit claim toggle [toggle name] [claim name]";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String name = null;
		if(args.length > 1) {
			name = args[1];
		}
		ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(name, sender);
		if(claim == null) {
			throw new CommandException("No claim found.");
		}
		if(args.length == 0 || (args.length == 1 && args[0].equals("list"))) {
			if(CommandUtils.canManagePerms(sender, claim)) {
				String out = "";
				for(ClaimPermissionToggle toggle : ClaimPermissionRegistry.getTogglePermissions()) {
					boolean toggled = claim.isPermissionToggled(toggle);
					String toggledStr = toggled ? "ON" : "OFF";
					toggledStr = toggle.defaultValue == toggled ? "§a" + toggledStr : "§c" + toggledStr;
					out += "§e" + toggle.parsedName + "§9: " + toggledStr + "\n";
				}
				throw new CommandException(out.trim());
			}
		}
		if(args.length > 2 || args.length < 1) {
			throw new WrongUsageException(this.getUsage(sender) + "\n Invalid argument count!");
		}
		
		ClaimPermissionToggle perm = CommandUtils.getPermissionToggle(args[0], this.getUsage(sender));

		if(CommandUtils.canManagePerms(sender, claim)) {
			claim.flipPermissionToggle(perm);
			boolean toggled = claim.isPermissionToggled(perm);
			String toggledStr = toggled ? "ON" : "OFF";
			sendMessage(sender, "§aSet §e" + perm.parsedName + "§a to §9" + toggledStr);
		}

	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
