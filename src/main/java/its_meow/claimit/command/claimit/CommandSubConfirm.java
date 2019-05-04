package its_meow.claimit.command.claimit;

import java.util.Set;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.util.ConfirmationManager;
import its_meow.claimit.util.ConfirmationManager.EnumConfirmableAction;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubConfirm extends CommandBase {

	@Override
	public String getName() {
		return "confirm";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit confirm";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ConfirmationManager mgr = ConfirmationManager.getManager();
		if(mgr.needsConfirm(sender)) {
			EnumConfirmableAction action = mgr.getAction(sender);
			sendMessage(sender, "Confirmed action: " + action.toString().toLowerCase());
			mgr.removeConfirm(sender);
			if(action == EnumConfirmableAction.DELETEALL) {
				if(sender instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) sender;
					Set<ClaimArea> owned = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
					if(owned != null) {
						for(ClaimArea claim : owned) {
							ClaimManager.getManager().deleteClaim(claim);
						}
						sendMessage(sender, "Successfully deleted all claims.");
					} else {
						sendMessage(sender, "You do not own any claims!");
					}
				} else {
					sendMessage(sender, "You were confirming deletion but were not a player. Actually, this is technically impossible because the way the delete command works. I'm mildly concerned. But here we are.");
				}
			}
		} else {
			sendMessage(sender, "You have no actions to confirm!");
		}
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
