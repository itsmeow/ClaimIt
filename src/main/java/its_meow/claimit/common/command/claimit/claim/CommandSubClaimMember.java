package its_meow.claimit.common.command.claimit.claim;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.common.claim.ClaimArea;
import its_meow.claimit.common.claim.ClaimManager;
import its_meow.claimit.common.claim.EnumPerm;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubClaimMember extends CommandBase {

	@Override
	public String getName() {
		return "member";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim member <add/remove> <permission> <username> (optional:claimname)";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}


	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 3 || args.length > 4) {
			throw new WrongUsageException("Improper argument count! Usage: " + this.getUsage(sender));
		}
		String action = args[0].toLowerCase();
		String permissionStr = args[1].toUpperCase();
		String username = args[2];
		String claimName = null;
		if(args.length == 4) {
			claimName = args[3];
		}
		if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
			throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
		}
		EnumPerm permission = null;
		try {
			permission = EnumPerm.valueOf(permissionStr);
		} catch (IllegalArgumentException e) {
			throw new WrongUsageException("Invalid permission. Specify: Modify, Use, Entity, or PVP. Usage: " + this.getUsage(sender));
		}
		if(permission == null) {
			throw new WrongUsageException("Invalid permission. Specify: Modify, Use, Entity, or PVP. Usage: " + this.getUsage(sender));
		}
		UUID id = null;
		GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
		if(profile != null && profile.getName().equals(username)) { // Found the profile!
			id = profile.getId();
		} else {
			throw new PlayerNotFoundException("Invalid player: " + args[0]);
		}
		ClaimManager mgr = ClaimManager.getManager();
		ClaimArea claim = null;;

		if(claimName != null && !claimName.equals("")) {
			if(sender instanceof EntityPlayer) {
				EntityPlayer player = ((EntityPlayer) sender);
				claim = mgr.getClaimByNameAndOwner(claimName, player.getUniqueID());
				if(claim == null && mgr.isAdmin(player)) {
					sendMessage(sender, "§bUsing true name.");
					claim = mgr.getClaimByTrueName(claimName);
				}
			} else { // sender is console/commandblock
				if(sender.canUseCommand(2, "")) {
					sendMessage(sender, "You are console, using true name");
					claim = mgr.getClaimByTrueName(claimName);
				}
			}
		} else {
			// Get current location for claim
			if(sender instanceof EntityPlayer) {
				claim = mgr.getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
			} else {
				throw new WrongUsageException("Console must specify a true claim name!");
			}
		}

		if(claim != null) {
			if(action.equals("add"))  {
				// Add user
				if(sender.canUseCommand(2, "")) {
					claim.addMember(permission, id);
					sendMessage(sender, "§aSuccessfully added " + username + "§a to claim " + claim.getDisplayedViewName() + " with permission " + permission.toString());
				} else if(sender instanceof EntityPlayer && claim.isOwner((EntityPlayer) sender)) {
					claim.addMember(permission, id);
					sendMessage(sender, "§aSuccessfully added " + username + "§a to claim " + claim.getDisplayedViewName() + " with permission " + permission.toString());
				}
			} else if(action.equals("remove")) {
				// Remove user
				if(sender.canUseCommand(2, "")) {
					claim.removeMember(permission, id);
					sendMessage(sender, "§aSuccessfully removed permission " + permission.toString() + " from user " + username + " in claim " + claim.getDisplayedViewName());
				} else if(sender instanceof EntityPlayer && claim.isOwner((EntityPlayer) sender)) {
					claim.removeMember(permission, id);
					sendMessage(sender, "§aSuccessfully removed permission " + permission.toString() + " from user " + username + " in claim " + claim.getDisplayedViewName());
				}
			} else {
				throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
			}
		} else {
			if(claimName != null && !claimName.equals("")) {
				sendMessage(sender, "§cNo claim with this name was found.");
			} else {
				sendMessage(sender, "§cThere is no claim here! Specify a name to get a specific claim.");
			}
		}
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}


}
