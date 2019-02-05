package its_meow.claimit.command.claimit.claim;

import its_meow.claimit.permission.ClaimPermissionRegistry;
import its_meow.claimit.permission.ClaimPermissionToggle;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandSubClaimToggle extends CommandBase {

	@Override
	public String getName() {
		return "toggle";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim toggle <toggle name>";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length > 1) {
			throw new WrongUsageException(this.getUsage(sender) + "\n Too many arguments.");
		}
		ClaimPermissionToggle perm = ClaimPermissionRegistry.getPermissionToggle(args[0]);
		if(perm == null) {
			throw new CommandException("Invalid permission. Valid Permissions: §a" + ClaimPermissionRegistry.getValidPermissionListToggle());
		}
		
	}

}
