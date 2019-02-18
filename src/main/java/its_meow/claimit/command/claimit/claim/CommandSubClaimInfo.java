package its_meow.claimit.command.claimit.claim;

import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import static net.minecraft.util.text.TextFormatting.*;

public class CommandSubClaimInfo extends CommandBase {

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit claim info [name]";
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
					sendMessage(sender, RED + "There is no claim here!");
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
					sendMessage(sender, RED + "No claim with this name that you own!");
				}
			}
		}
		
		if(args.length > 1) {
			sendMessage(sender, RED + "Invalid argument count. Maximum 1 argument after \"info\". Usage: " + this.getUsage(sender));
		}
	}
	
	private static void outputClaimInfo(ClaimArea claim, EntityPlayer player) {
		World worldIn = player.getEntityWorld();
		BlockPos[] corners = claim.getTwoMainClaimCorners();
		UUID owner = claim.getOwner();
		String ownerName = ClaimManager.getPlayerName(owner, worldIn);
		if(ownerName == null) {
			ownerName = worldIn.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(owner).getName();
		}
		int dim = claim.getDimensionID();

		sendMessage(player, BLUE + "" + BOLD + "Information for claim owned by " + GREEN + "" + BOLD + ownerName + BLUE + "" + BOLD + ":");
		sendMessage(player, BLUE + "Claim Name: " + DARK_GREEN + claim.getDisplayedViewName());
		if(ClaimManager.getManager().isAdmin(player)) {
			sendMessage(player, BLUE + "Claim True Name: " + DARK_GREEN + claim.getTrueViewName());
		}
		sendMessage(player, BLUE + "Dimension: " + DARK_PURPLE + dim);
		sendMessage(player, BLUE + "Area: " + AQUA + (claim.getSideLengthX() + 1) + BLUE + "x" + AQUA + (claim.getSideLengthZ() + 1) + BLUE + " (" + AQUA + claim.getArea() + BLUE + ")");
		sendMessage(player, BLUE + "Corner 1: " + DARK_GREEN + (corners[0].getX()) + ", " + (corners[0].getZ()));
		sendMessage(player, BLUE + "Corner 2: " + DARK_GREEN + (corners[1].getX()) + ", " + (corners[1].getZ()));
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}