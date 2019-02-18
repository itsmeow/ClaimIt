package its_meow.claimit.command.claimit.claim;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import static net.minecraft.util.text.TextFormatting.*;

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
					toggledStr = toggle.defaultValue == toggled ? GREEN + toggledStr : RED + toggledStr;
					out += YELLOW + toggle.parsedName + BLUE + ": " + toggledStr + "\n";
				}
				throw new CommandException(out.trim());
			}
		}
		if(args.length > 2 || args.length < 1) {
			throw new WrongUsageException(this.getUsage(sender) + "\n Invalid argument count!");
		}
		
		ClaimPermissionToggle perm = CommandUtils.getPermissionToggle(args[0], this.getUsage(sender));

		if(CommandUtils.canManagePerms(sender, claim)) {
			if(perm.force) {
				claim.setPermissionToggle(perm, perm.toForce);
				throw new CommandException("This toggle cannot be modified. It has been forced by the server.");
			}
			claim.flipPermissionToggle(perm);
			boolean toggled = claim.isPermissionToggled(perm);
			String toggledStr = toggled ? "ON" : "OFF";
			sendMessage(sender, GREEN + "Set " + YELLOW + perm.parsedName + GREEN + " to " + BLUE + toggledStr);
		} else {
			sendMessage(sender, RED + "You cannot modify toggles of this claim!");
		}

	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
