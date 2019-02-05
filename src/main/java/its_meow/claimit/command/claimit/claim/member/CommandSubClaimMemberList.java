package its_meow.claimit.command.claimit.claim.member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import its_meow.claimit.claim.ClaimArea;
import its_meow.claimit.claim.ClaimManager;
import its_meow.claimit.permission.ClaimPermissionMember;
import its_meow.claimit.permission.ClaimPermissionRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubClaimMemberList extends CommandBase {

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim member list (optional:claimname) (and/or) (optional:permission)";
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
				throw new WrongUsageException("Must specify name as non-player.");
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
					throw new WrongUsageException("Must specify name as non-player.");
				}
			} catch(IllegalArgumentException e) {

				// Try claim name instead of perm filter
				if(sender instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) sender;
					ClaimArea claim = m.getClaimByNameAndOwner(args[0], EntityPlayer.getUUID(player.getGameProfile()));
					if(claim == null && m.isAdmin(player)) {
						sendMessage(sender, "§aYou are admin and no claim you own with that name was found. Using true names.");
						claim = m.getClaimByTrueName(args[0]);
					}
					outputNonFiltered(claim, sender);
				} else {
					sendMessage(sender, "You are console. Using true name.");
					ClaimArea claim = m.getClaimByTrueName(args[0]);
					outputNonFiltered(claim, sender);
				}
			}

		} else if(args.length == 2) {
			String claimName = args[0];
			String permStr = args[1];
			try {
				ClaimPermissionMember filter = ClaimPermissionRegistry.getPermissionMember(args[0]);
				
				if(sender instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) sender;
					ClaimArea claim = m.getClaimByNameAndOwner(claimName, EntityPlayer.getUUID(player.getGameProfile()));
					if(claim == null && m.isAdmin(player)) {
						sendMessage(sender, "§aYou are admin and no claim you own with that name was found. Using true names.");
						claim = m.getClaimByTrueName(claimName);
					}
					outputFiltered(claim, sender, filter);
				} else {
					ClaimArea claim = m.getClaimByTrueName(claimName);
					outputFiltered(claim, sender, filter);
				}
				
			} catch(IllegalArgumentException e) {
				throw new WrongUsageException("§cNo such permission: §a" + permStr + "\n§cValid Permissions: §a" + ClaimPermissionRegistry.getValidPermissionListMember());
			}
		} else {
			throw new WrongUsageException("§cToo many arguments! Usage: " + this.getUsage(sender));
		}
	}
	
	private void outputFiltered(ClaimArea claim, ICommandSender sender, ClaimPermissionMember filter) throws WrongUsageException, CommandException {
		if(claim != null) {
			ArrayList<UUID> members = claim.getArrayForPermission(filter);
			if(sender instanceof EntityPlayer && !claim.getMembers().keySet().contains(EntityPlayer.getUUID(((EntityPlayer) sender).getGameProfile()))) {
				if(!claim.isOwner((EntityPlayer) sender)) {
					throw new CommandException("You cannot view the members of this claim!");
				}
			}
			if(members.isEmpty()) {
				throw new CommandException("This claim has no members with that permission.");
			}
			sendMessage(sender, "§a" + filter.parsedName + "§9:");
			for(UUID member : members) {
				String name = ClaimManager.getPlayerName(member, sender.getEntityWorld());
				sendMessage(sender, "§e" + name);
			}
		} else {
			throw new WrongUsageException("§cThere is no claim here! Usage: §b" + this.getUsage(sender));
		}
	}
	
	private void outputNonFiltered(ClaimArea claim, ICommandSender sender) throws WrongUsageException, CommandException {
		if(claim != null) {
			Map<UUID, HashSet<ClaimPermissionMember>> permMap = claim.getMembers();
			if(sender instanceof EntityPlayer && !permMap.keySet().contains(EntityPlayer.getUUID(((EntityPlayer) sender).getGameProfile()))) {
				if(!claim.isOwner((EntityPlayer) sender)) {
					throw new CommandException("You cannot view the members of this claim!");
				}
			}
			if(permMap.isEmpty()) {
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
				sendMessage(sender, "§e" + ClaimManager.getPlayerName(member, sender.getEntityWorld()) + "§9:§a " + permString);
			}
		} else {
			throw new WrongUsageException("§cThere is no claim here! Usage: §b" + this.getUsage(sender));
		}
	}


	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}
}
