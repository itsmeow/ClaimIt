package its_meow.claimit.command.claimit;

import its_meow.claimit.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubAdmin extends CommandBase {

	@Override
	public String getName() {
		return "admin";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit admin";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(2, "claimit admin");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			if(this.checkPermission(server, sender)) {
				if(ClaimManager.getManager().isAdmin((EntityPlayer) sender)) {
					ClaimManager.getManager().removeAdmin((EntityPlayer) sender);
					sendMessage(sender, "§aAdmin bypass disabled.");
				} else {
					ClaimManager.getManager().addAdmin((EntityPlayer) sender);
					sendMessage(sender, "§aAdmin bypass enabled. You may now manage all claims.");
				}
			} else {
				sendMessage(sender, "§cYou do not have permission to use this command!");
			}
		} else {
			sendMessage(sender, "You must be a player to use this command!");
		}
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
