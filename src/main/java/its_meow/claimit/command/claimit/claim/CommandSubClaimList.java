package its_meow.claimit.command.claimit.claim;

import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.claim.ClaimArea;
import its_meow.claimit.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubClaimList extends CommandBase {

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim list - claimit claim list <username>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		/* 
		 * Filter by name (admin only) 
		 */
		UUID filter = null;
		if(args.length == 1) {
			String name = args[0];
			GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(name);
			if(profile != null && profile.getName().equals(name)) { // Found the profile!
				filter = profile.getId();
			} else {
				throw new PlayerNotFoundException("Invalid player: " + args[0]);
			}
		}

		if(sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			if(!ClaimManager.getManager().isAdmin(player)) {
				Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
				int i = 0;
				for(ClaimArea claim : claims) {
					if(claim.isTrueOwner(player)) {
						i++;
						if(i == 1) {
							sendMessage(sender, "§1Claim List for §a" + player.getName() + "§1:");
						}
						sendMessage(sender, "§9Name: §2" + claim.getDisplayedViewName());
					}
				}
				if(i == 0) {
					sendMessage(sender, "§cYou don't own any claims!");
				}
			} else {
				if(filter == null) {
					sendMessage(sender, "§cYou are admin. Getting all claims. Specify a name to get only their claims.");
				}
				Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
				int i = 0;
				for(ClaimArea claim : claims) {
					if(filter == null || claim.isTrueOwner(filter)) {
						i++;
						sendMessage(sender, "##### Claim " + i + " #####");
						sendMessage(sender, "Owner: " + ClaimManager.getPlayerName(claim.getOwner().toString(), sender.getEntityWorld()));
						sendMessage(sender, "Claim True Name: " + claim.getTrueViewName());
						sendMessage(sender, "Dimension: " + claim.getDimensionID());
						sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
					}
				}
				if(i == 0) {
					sendMessage(sender, "No claims found.");
				}
			}
		} else { // Sender is console!
			sendMessage(sender, "Detected server console. Getting all claims. Specify a name to get only their claims.");
			Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
			int i = 0;
			for(ClaimArea claim : claims) {
				if(filter == null || claim.isTrueOwner(filter)) {
					i++;
					sendMessage(sender, "####CLAIM INFO####");
					sendMessage(sender, "Claim #" + i + ", owned by: " + ClaimManager.getPlayerName(claim.getOwner().toString(), sender.getEntityWorld()));
					sendMessage(sender, "Claim True Name: " + claim.getTrueViewName());
					sendMessage(sender, "Dimension: " + claim.getDimensionID());
					sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
				}
			}
			if(i == 0) {
				sendMessage(sender, "No claims found.");
			}
		}

		if(args.length > 1) {
			sendMessage(sender, "Invalid amount of arguments. Usage: " + this.getUsage(sender));
		}
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}