package its_meow.claimit.command.claimit.claim.permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import static net.minecraft.util.text.TextFormatting.*;

public class CommandSubClaimPermissionList extends CommandBase {

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim permission list [claimname] [permission]";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ClaimManager m = ClaimManager.getManager();
		if(args.length == 0) {
			if(sender instanceof EntityPlayer) {
				ClaimArea claim = m.getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
				outputNonFiltered(claim, sender);
			} else {
				sendMessage(sender, "Must specify name as non-player.");
				throw new CommandException("Must specify name as non-player.");
			}
		} else if(args.length == 1) {

			// Attempt perm filter with no claim name

			try {
				ClaimPermissionMember filter = ClaimPermissionRegistry.getPermissionMember(args[0]);
				if(sender instanceof EntityPlayer) {
					ClaimArea claim = m.getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
					outputFiltered(claim, sender, filter);
				} else {
					sendMessage(sender, "Must specify name as non-player.");
					throw new CommandException("Must specify name as non-player.");
				}
			} catch(IllegalArgumentException e) {

				ClaimArea claim = CommandUtils.getClaimWithName(args[0], sender);
				if(claim != null) {
					outputNonFiltered(claim, sender);
				}
			}

		} else if(args.length == 2) {
			String claimName = args[0];
			String permStr = args[1];
			try {
				ClaimPermissionMember filter = ClaimPermissionRegistry.getPermissionMember(permStr);
				
				ClaimArea claim = CommandUtils.getClaimWithName(claimName, sender);
				if(claim != null) {
					outputFiltered(claim, sender, filter);
				}
				
				
			} catch(IllegalArgumentException e) {
				throw new WrongUsageException(RED + "No such permission: " + GREEN + permStr + "\n" + RED + "Valid Permissions: " + GREEN + ClaimPermissionRegistry.getValidPermissionListMember());
			}
		} else {
			throw new WrongUsageException(RED + "Too many arguments! Usage: " + this.getUsage(sender));
		}
	}
	
	private void outputFiltered(ClaimArea claim, ICommandSender sender, ClaimPermissionMember filter) throws WrongUsageException, CommandException {
		if(claim != null) {
			ArrayList<UUID> members = claim.getArrayForPermission(filter);
			if(sender instanceof EntityPlayer) {
				if(!claim.canManage((EntityPlayer) sender)) {
					throw new CommandException("You cannot view the members of this claim!");
				}
			}
			if(members == null || members.isEmpty()) {
				throw new CommandException("This claim has no members with that permission.");
			}
			sendMessage(sender, GREEN + filter.parsedName + BLUE + ":");
			for(UUID member : members) {
				String name = ClaimManager.getPlayerName(member, sender.getEntityWorld());
				sendMessage(sender, YELLOW + name);
			}
		} else {
			throw new WrongUsageException(RED + "There is no claim here! Usage: " + AQUA + this.getUsage(sender));
		}
	}
	
	private void outputNonFiltered(ClaimArea claim, ICommandSender sender) throws WrongUsageException, CommandException {
		if(claim != null) {
			Map<UUID, HashSet<ClaimPermissionMember>> permMap = claim.getMembers();
			if(sender instanceof EntityPlayer) {
				if(!claim.canManage((EntityPlayer) sender)) {
					throw new CommandException("You cannot view the members of this claim!");
				}
			}
			if(permMap == null || permMap.isEmpty()) {
				throw new CommandException("This claim has no members.");
			}
			for(UUID member : permMap.keySet()) {
				String permString = "";
				HashSet<ClaimPermissionMember> permSet = permMap.get(member);
				for(ClaimPermissionMember p : permSet) {
					permString += p.parsedName + ", ";
				}
				int end = permString.lastIndexOf(',');
				permString = permString.substring(0, end);
				sendMessage(sender, YELLOW + ClaimManager.getPlayerName(member, sender.getEntityWorld()) + BLUE + ":" + GREEN + permString);
			}
		} else {
			throw new WrongUsageException(RED+ "There is no claim here! Usage: " + AQUA + this.getUsage(sender));
		}
	}


	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}
}
