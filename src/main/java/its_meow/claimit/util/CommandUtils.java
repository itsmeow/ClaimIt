package its_meow.claimit.util;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import static net.minecraft.util.text.TextFormatting.*;

public class CommandUtils {
	
	@Nullable
	public static ClaimArea getClaimWithName(String claimName, ICommandSender sender) {
		ClaimArea claim = null;
		ClaimManager mgr = ClaimManager.getManager();
		if(sender instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) sender);
			claim = mgr.getClaimByNameAndOwner(claimName, player.getUniqueID());
			if(claim == null && mgr.isAdmin(player)) {
				sendMessage(sender, AQUA + "Using true name.");
				claim = mgr.getClaimByTrueName(claimName);
			}
		} else { // sender is console/commandblock
			if(sender.canUseCommand(2, "")) {
				sendMessage(sender, "You are console, using true name");
				claim = mgr.getClaimByTrueName(claimName);
			}
		}
		return claim;
	}
	
	@Nullable
	public static ClaimArea getClaimWithNameOrLocation(String claimName, ICommandSender sender) throws CommandException {
		ClaimArea claim = null;
		if(claimName != null && !claimName.equals("")) {
			claim = CommandUtils.getClaimWithName(claimName, sender);
		} else {
			// Get current location for claim
			if(sender instanceof EntityPlayer) {
				claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
			} else {
				throw new CommandException("Console must specify a true claim name!");
			}
		}
		return claim;
	}
	
	public static ClaimPermissionMember getPermissionMember(String permName, String usage) throws CommandException {
		String validPerms = ClaimPermissionRegistry.getValidPermissionListMember();
		ClaimPermissionMember permission = null;
		try {
			permission = ClaimPermissionRegistry.getPermissionMember(permName);
		} catch (IllegalArgumentException e) {
			throw new CommandException("Invalid permission." + GREEN +" Valid Permissions: " + YELLOW + validPerms + RED + "\nUsage: " + YELLOW + usage);
		}
		if(permission == null) {
		    throw new CommandException("Invalid permission." + GREEN +" Valid Permissions: " + YELLOW + validPerms + RED + "\nUsage: " + YELLOW + usage);
		}
		return permission;
	}
	
	public static ClaimPermissionToggle getPermissionToggle(String permName, String usage) throws CommandException {
		String validPerms = ClaimPermissionRegistry.getValidPermissionListToggle();
		ClaimPermissionToggle permission = null;
		try {
			permission = ClaimPermissionRegistry.getPermissionToggle(permName);
		} catch (IllegalArgumentException e) {
			throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + usage);
		}
		if(permission == null) {
			throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + usage);
		}
		return permission;
	}
	
	public static boolean canManagePerms(ICommandSender sender, ClaimArea claim) {
		if(sender instanceof EntityPlayer) {
			if(claim.canManage((EntityPlayer) sender)) {
				return true;
			}
		} else if(sender.canUseCommand(2, "")) { // Console
			return true;
		}
		return false;
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

	public static UUID getUUIDForName(String username, MinecraftServer server) throws PlayerNotFoundException {
		GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
		if(profile != null && profile.getName().equals(username)) { // Found the profile!
			return profile.getId();
		} else {
			throw new PlayerNotFoundException("Invalid player: " + username);
		}
	}
	
}
