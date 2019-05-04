package its_meow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.command.CommandCIBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubAdmin extends CommandCIBase {

	@Override
	public String getName() {
		return "admin";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit admin";
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
					sendMessage(sender, GREEN + "Admin bypass disabled.");
				} else {
					ClaimManager.getManager().addAdmin((EntityPlayer) sender);
					sendMessage(sender, GREEN + "Admin bypass enabled. You may now manage all claims.");
				}
			} else {
				sendMessage(sender, RED + "You do not have permission to use this command!");
			}
		} else {
			sendMessage(sender, "You must be a player to use this command!");
		}
	}

    @Override
    public String getHelp(ICommandSender sender) {
        return "Toggles admin mode on or off (if you have permission to do so), which allows server level management of ClaimIt.";
    }

}
