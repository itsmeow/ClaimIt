package its_meow.claimit.command.claimit.claim;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubClaimDelete extends CommandBase {

	@Override
	public String getName() {
		return "delete";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit claim delete [claimname]";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		String claimName = null;
		if(args.length == 1) {
			claimName = args[0];
		}
		ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);
		if(claimName == null && claim == null) {
			throw new CommandException("There is no claim here!");
		} else if(claim == null) {
			throw new CommandException("There is no claim with this name you own!");
		}

		if((sender instanceof EntityPlayer && (claim.isOwner((EntityPlayer) sender) || ClaimManager.getManager().isAdmin((EntityPlayer) sender))) || (!(sender instanceof EntityPlayer) && sender.canUseCommand(2, ""))) {
			ClaimManager.getManager().deleteClaim(claim);
			sendMessage(sender, "§eClaim deleted.");
		} else {
			sendMessage(sender, "§cYou do not own this claim!");
		}

	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}