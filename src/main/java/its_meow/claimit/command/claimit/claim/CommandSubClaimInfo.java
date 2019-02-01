package its_meow.claimit.command.claimit.claim;

import java.util.UUID;

import its_meow.claimit.claim.ClaimArea;
import its_meow.claimit.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandSubClaimInfo extends CommandBase {

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim info - claimit claim info <name>";
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		/*
		 * Info by current location
		 */
		if(args.length == 0) {
			if(sender instanceof EntityPlayer) {
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
				if(claim != null) {
					outputClaimInfo(claim, (EntityPlayer) sender);
				} else {
					sendMessage(sender, "§cThere is no claim here!");
				}
			}
		}
		
		
		/*
		 * Info by claim name
		 */
		if(args.length == 1) {
			if(sender instanceof EntityPlayer) { 
				EntityPlayer player = ((EntityPlayer) sender);
				ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(args[0], EntityPlayer.getUUID(player.getGameProfile()));
				if(claim != null) {
					outputClaimInfo(claim, player);
				} else {
					sendMessage(sender, "§cNo claim with this name that you own!");
				}
			}
		}
		
		if(args.length > 1) {
			sendMessage(sender, "§cInvalid argument count. Maximum 1 argument after \"info\". Usage: " + this.getUsage(sender));
		}
	}
	
	private static void outputClaimInfo(ClaimArea claim, EntityPlayer player) {
		World worldIn = player.getEntityWorld();
		BlockPos[] corners = claim.getTwoMainClaimCorners();
		UUID owner = claim.getOwner();
		String ownerName = ClaimManager.getPlayerName(owner.toString(), worldIn);
		if(ownerName == null) {
			ownerName = worldIn.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(owner).getName();
		}
		int dim = claim.getDimensionID();

		sendMessage(player, "§9§lInformation for claim owned by §a§l" + ownerName + "§9§l:");
		sendMessage(player, "§9Claim Name: §2" + claim.getDisplayedViewName());
		if(ClaimManager.getManager().isAdmin(player)) {
			sendMessage(player, "§9Claim True Name: §2" + claim.getTrueViewName());
		}
		sendMessage(player, "§9Dimension: §5" + dim);
		sendMessage(player, "§9Area: §b" + (claim.getSideLengthX() + 1) + "§9x§b" + (claim.getSideLengthZ() + 1) + " §9(§b" + claim.getArea() + "§9) ");
		sendMessage(player, "§9Corner 1: §2" + (corners[0].getX()) + ", " + (corners[0].getZ()));
		sendMessage(player, "§9Corner 2: §2" + (corners[1].getX()) + ", " + (corners[1].getZ()));
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}