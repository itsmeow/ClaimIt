package its_meow.claimit.common.command.claimit.claim;

import its_meow.claimit.common.claim.ClaimArea;
import its_meow.claimit.common.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;

public class CommandSubClaimDelete extends CommandBase {

	@Override
	public String getName() {
		return "delete";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim delete - claimit claim delete <name> - claimit claim delete <posX> <posY> <dimID>";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		/* 
		 * Delete by current location
		 */
		
		if(args.length == 0) {
			if(sender instanceof EntityPlayer) {
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
				if(claim != null) {
					if(claim.isOwner((EntityPlayer) sender.getCommandSenderEntity())) {
						ClaimManager.getManager().deleteClaim(claim);
						sendMessage(sender, "§eClaim deleted.");
					} else {
						sendMessage(sender, "§cYou do not own this claim!");
					}
				} else {
					sendMessage(sender, "§cThere is no claim here! You can use §e/claimit claim delete (Corner 1 X) (Corner 1 Z) (Dimension ID) §cto delete a claim remotely.");
					sendMessage(sender, "§cYou can also use §e/claimit claim delete (claim name) §cto delete a claim remotely.");
				}
			} else {
				sendMessage(sender, "You must be a player to use this command!");
			}
		}
		
		/* 
		 * Delete by name
		 */
		
		if(args.length == 1) {
			if(sender instanceof EntityPlayer) { 
				EntityPlayer player = ((EntityPlayer) sender);
				ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(args[0], EntityPlayer.getUUID(player.getGameProfile()));
				if(claim == null && ClaimManager.getManager().isAdmin(player)) {
					claim = ClaimManager.getManager().getClaimByTrueName(args[0]);
					sendMessage(sender, "§aYou are admin and no claim was found. Trying again with true name.");
					if(claim == null) {
						sendMessage(sender, "§cNo such claim with this true name exists. A true name starts with the player UUID, an underscore, and ends with the name that was set by the player.");
					}
				}
				if(claim != null && claim.isOwner(player)) {
					ClaimManager.getManager().deleteClaim(claim);
					sendMessage(sender, "§b§lDeleted claim.");
				} else if(!ClaimManager.getManager().isAdmin(player)) {
					sendMessage(sender, "§cNo claim with this name that you own!");
				}
			} else {
				// Sender is console
				ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(args[0]);
				if(claim != null && sender.canUseCommand(4, "")) {
					ClaimManager.getManager().deleteClaim(claim);
					sendMessage(sender, "Deleted claim.");
				} else {
					sendMessage(sender, "Could not find claim with this true name");
				}
			}
		}
		
		/*
		 * Delete by position and dimension
		 */
		
		if(args.length == 3) {
			int posX = 0;
			int posY = 0;
			int dim = 0;
			try {
				posX = Integer.parseInt(args[0]);
				posY = Integer.parseInt(args[1]);
				dim = Integer.parseInt(args[2]);
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(DimensionManager.getWorld(dim), new BlockPos(posX,0,posY));
				if(claim != null) {
					if(sender instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) sender;
						if(claim.isOwner(player)) {
							ClaimManager.getManager().deleteClaim(claim);
							sendMessage(sender, "§b§lDeleted claim:");
							sendMessage(sender, "§9Claim Name: §2" + claim.getDisplayedViewName());
							sendMessage(sender, "§5Dimension: " + claim.getDimensionID());
							sendMessage(sender, "§9Location: §2" + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
						} else {
							sendMessage(sender, "§cNo claim there or you don't own the claim!");
						}
					} else {
						// Sender is console
						if(sender.canUseCommand(4, "")) {
							sendMessage(sender, "Deleted claim:");
							sendMessage(sender, "§9Claim Name: §2" + claim.getDisplayedViewName());
							sendMessage(sender, "Dimension: " + claim.getDimensionID());
							sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
						} else {
							sendMessage(sender, "No claim there or you don't have permission!");
						}
					}
				} else {
					sendMessage(sender, "§cNo claim there or you don't own the claim!");
				}
			} catch(NumberFormatException e) {
				sendMessage(sender, "§cInvalid location! Use §e/claimit claim delete (Corner 1 X) (Corner 1 Z) (Dimension ID)");
			}
		}
		
		
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}