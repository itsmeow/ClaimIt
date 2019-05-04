package its_meow.claimit.command.claimit.claim;

import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import static net.minecraft.util.text.TextFormatting.*;

public class CommandSubClaimList extends CommandBase {

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		if(sender instanceof EntityPlayer) {
			if(ClaimManager.getManager().isAdmin((EntityPlayer) sender)) {
				return "/claimit claim list [username] [page]";
			}
		}
		return "/claimit claim list [page]";
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
		if(args.length == 1 && (((!(sender instanceof EntityPlayer) && sender.canUseCommand(2, "")) || ((sender instanceof EntityPlayer) && ClaimManager.getManager().isAdmin((EntityPlayer) sender)))) ) {
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
				final Set<ClaimArea> claims = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
				if(claims != null) {
					int i = 0;
					for(ClaimArea claim : claims) {
						i++;
						if(i == 1) {
							sendMessage(sender, DARK_BLUE + "Claim List for " + GREEN + player.getName() + DARK_BLUE + ":");
						}
						sendMessage(sender, BLUE + "Name: " + DARK_GREEN + claim.getDisplayedViewName());
					}
				} else {
					sendMessage(sender, RED + "You don't own any claims!");
				}
			} else {
				if(filter == null) {
					sendMessage(sender, RED + "You are admin. Getting all claims. Specify a name to get only their claims.");
				}
				Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
				int i = 0;
				for(ClaimArea claim : claims) {
					if(filter == null || claim.isTrueOwner(filter)) {
						i++;
						sender.sendMessage(new TextComponentString("Page").setStyle(new Style() {
						    
						}));
						sendMessage(sender, DARK_RED + "" + BOLD + "##### Claim " + i + " #####");
						sendMessage(sender, BLUE + "Owner: " + GREEN + ClaimManager.getPlayerName(claim.getOwner(), sender.getEntityWorld()));
						sendMessage(sender, BLUE + "Claim True Name: " + YELLOW + claim.getTrueViewName());
						sendMessage(sender, BLUE + "Dimension: " + DARK_PURPLE + claim.getDimensionID());
						sendMessage(sender, BLUE + "Location: " + DARK_PURPLE + (claim.getMainPosition().getX()) + BLUE + ", " + DARK_PURPLE + (claim.getMainPosition().getZ()));
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
					sendMessage(sender, "Claim #" + i + ", owned by: " + ClaimManager.getPlayerName(claim.getOwner(), sender.getEntityWorld()));
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