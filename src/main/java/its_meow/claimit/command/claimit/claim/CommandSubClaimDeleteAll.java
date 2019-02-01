package its_meow.claimit.command.claimit.claim;

import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.EnumConfirmableAction;
import its_meow.claimit.claim.ClaimArea;
import its_meow.claimit.claim.ClaimManager;
import its_meow.claimit.command.ConfirmationManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubClaimDeleteAll extends CommandBase {

	@Override
	public String getName() {
		return "deleteall";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim deleteall - Admin: claimit claim deleteall <playername>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
			if(sender instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) sender;
				Set<ClaimArea> ownedClaims = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
				if(ownedClaims != null) {
					if(!ConfirmationManager.getManager().needsConfirm(sender)) {
						ConfirmationManager.getManager().addConfirm(sender, EnumConfirmableAction.DELETEALL);
						sendMessage(sender, "§4This will delete ALL of your claims. Are you sure you want to do this? Run §b'/claimit confirm'§4 to confirm. If you do not want to do this, run §b'/claimit cancel'§4.");
					} else {
						sendMessage(sender, "§cCanceling preexisting action. Run this command again to delete all claims.");
						ConfirmationManager.getManager().removeConfirm(sender);
					}
				} else {
					sendMessage(sender, "§cYou don't own any claims!");
				}
			} else {
				sendMessage(sender, "You must be a player to use this command.");
			}
		} else if(args.length == 1) {
			String username = args[0];
			UUID id = null;
			GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
			if(profile != null && profile.getName().equals(username)) { // Found the profile!
				id = profile.getId();
			} else {
				throw new PlayerNotFoundException("Invalid player: " + args[0]);
			}
			Set<ClaimArea> owned = ClaimManager.getManager().getClaimsOwnedByPlayer(id);
			if(owned.size() == 0) {
				throw new CommandException("This player owns no claims.");
			}
			if(!(sender instanceof EntityPlayer) && sender.canUseCommand(2, "")) {
				for(ClaimArea claim : owned) {
					ClaimManager.getManager().deleteClaim(claim);
				}
				sendMessage(sender, "Removed " + username + "'s claims.");
			} else if(sender instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) sender;
				if(ClaimManager.getManager().isAdmin(player)) {
					for(ClaimArea claim : owned) {
						ClaimManager.getManager().deleteClaim(claim);
					}
					sendMessage(sender, "Removed " + username + "'s claims.");
				} else {
					sendMessage(sender, "§cYou must be admin to use the playername argument!");
				}
			}
		} else {
			throw new WrongUsageException("Invalid arugment count! Usage: " + this.getUsage(sender));
		}
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}