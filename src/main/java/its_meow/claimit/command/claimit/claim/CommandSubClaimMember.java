package its_meow.claimit.command.claimit.claim;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.claim.ClaimArea;
import its_meow.claimit.claim.ClaimManager;
import its_meow.claimit.command.claimit.claim.member.CommandSubClaimMemberList;
import its_meow.claimit.permission.ClaimPermissionMember;
import its_meow.claimit.permission.ClaimPermissionRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandSubClaimMember extends CommandTreeBase {

	public CommandSubClaimMember() {
		this.addSubcommand(new CommandSubClaimMemberList());
	}

	@Override
	public String getName() {
		return "member";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim member <add/remove> <permission> <username> (optional:claimname) - claimit claim member list (optional:permission) (optional:claimname)";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}



	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return args.length == 3;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 1) {
			throw new WrongUsageException(this.getUsage(sender));
		}
		ICommand cmd = getSubCommand(args[0]);

		if(cmd == null)
		{
			if(args.length < 3 || args.length > 4) {
				throw new WrongUsageException("Improper argument count! Usage: " + this.getUsage(sender));
			}
			String action = args[0].toLowerCase();
			String permissionStr = args[1];
			String username = args[2];
			String claimName = null;
			if(args.length == 4) {
				claimName = args[3];
			}
			if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
				throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
			}
			ClaimPermissionMember permission = null;
			String validPerms = ClaimPermissionRegistry.getValidPermissionListMember();
			try {
				permission = ClaimPermissionRegistry.getPermissionMember(permissionStr);
			} catch (IllegalArgumentException e) {
				throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + this.getUsage(sender));
			}
			if(permission == null) {
				throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + this.getUsage(sender));
			}
			UUID id = null;
			GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
			if(profile != null && profile.getName().equals(username)) { // Found the profile!
				id = profile.getId();
			} else {
				throw new PlayerNotFoundException("Invalid player: " + username);
			}
			ClaimManager mgr = ClaimManager.getManager();
			ClaimArea claim = null;

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
					if(!claim.inPermissionList(permission, id) || claim.isTrueOwner(id)) {
						if(!(sender instanceof EntityPlayer) && sender.canUseCommand(2, "")) {
							claim.addMember(permission, id);
							sendMessage(sender, "§aSuccessfully added " + username + "§a to claim " + claim.getDisplayedViewName() + " with permission " + permission.parsedName);
						} else if(sender instanceof EntityPlayer && claim.isOwner((EntityPlayer) sender)) {
							claim.addMember(permission, id);
							sendMessage(sender, "§aSuccessfully added " + username + "§a to claim " + claim.getDisplayedViewName() + " with permission " + permission.parsedName);
						}
					} else {
						sendMessage(sender, "§eThis player already has that permission!");
					}
				} else if(action.equals("remove")) {
					// Remove user
					if(claim.inPermissionList(permission, id)) {
						if(!(sender instanceof EntityPlayer) && sender.canUseCommand(2, "")) {
							claim.removeMember(permission, id);
							sendMessage(sender, "§aSuccessfully removed permission " + permission.parsedName + " from user " + username + " in claim " + claim.getDisplayedViewName());
						} else if(sender instanceof EntityPlayer && claim.isOwner((EntityPlayer) sender)) {
							claim.removeMember(permission, id);
							sendMessage(sender, "§aSuccessfully removed permission " + permission.parsedName + " from user " + username + " in claim " + claim.getDisplayedViewName());
						}
					} else {
						sendMessage(sender, "§eThis player does not have that permission!");
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
		else if(!cmd.checkPermission(server, sender))
		{
			throw new CommandException("commands.generic.permission");
		}
		else
		{
			cmd.execute(server, sender, shiftArgs(args));
		}
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

	private static String[] shiftArgs(@Nullable String[] s)
	{
		if(s == null || s.length == 0)
		{
			return new String[0];
		}

		String[] s1 = new String[s.length - 1];
		System.arraycopy(s, 1, s1, 0, s1.length);
		return s1;
	}

}
