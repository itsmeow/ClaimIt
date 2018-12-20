package its_meow.claimit.common.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import its_meow.claimit.Ref;
import its_meow.claimit.common.claim.ClaimArea;
import its_meow.claimit.common.claim.ClaimManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandClaimIt extends CommandBase {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "claimit";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "'/claimit help' or '/ci help' for proper usage!";
	}

	private String aliasList = "";

	@Override
	public List<String> getAliases() {
		List<String> aliases = new LinkedList<String>();
		aliases.add("claimit");
		aliases.add("ci");
		aliases.iterator().forEachRemaining(s -> aliasList += "/" + s + " ");
		return aliases;
	}


	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		System.out.println(args.length);
		for(String arg : args) {
			System.out.println(arg);
		}
		if(args.length == 0) {
			sendMessage(sender, "§bClaimIt Version §e" + Ref.VERSION + "§b by its_meow");
			sendMessage(sender, "§bFor possible commands, run §e/claimit help");
			sendMessage(sender, "§bAlias(es): §e" + aliasList);
		} else if(args[0].equals("help") && args.length == 1) {
			sendMessage(sender, "§b§nSubcommands: ");
			sendMessage(sender, "§e/claimit claim");
			sendMessage(sender, "§e/claimit admin");
		} else if(args[0].equals("claim")) {
			if(args.length == 1) {
				sendMessage(sender, "§b§nSubcommands: ");
				sendMessage(sender, "§eclaim delete");
				sendMessage(sender, "§eclaim info");
				sendMessage(sender, "§eclaim list");
			} else if(args.length == 2) {
				if(args[1].equals("delete")) {
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
							sendMessage(sender, "§cThere is no claim here! You can use §e/claimit claim delete (Corner 1 X) (Corner 1 Z) §c to delete a claim remotely.");

						}
					} else {
						sendMessage(sender, "You must be a player to use this command!");
					}
				} else if(args[1].equals("info")) {
					if(sender instanceof EntityPlayer) {
						ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
						if(claim != null) {
							World worldIn = sender.getEntityWorld();
							EntityPlayer player = (EntityPlayer) sender;
							BlockPos[] corners = claim.getTwoMainClaimCorners();
							UUID owner = claim.getOwner();
							String ownerName = ClaimManager.getPlayerName(owner.toString(), worldIn);
							if(ownerName == null) {
								ownerName = worldIn.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(owner).getName();
							}
							int dim = claim.getDimensionID();

							sendMessage(player, "§9§nInformation for claim owned by §a§n" + ownerName + "§9§n:");
							sendMessage(player, "§9Dimension: §5" + dim);
							sendMessage(player, "§9Area: §b" + (claim.getSideLengthX() + 1) + "§9x§b" + (claim.getSideLengthZ() + 1) + " §9(§b" + claim.getArea() + "§9) ");
							sendMessage(player, "§9Corner 1: §2" + (corners[0].getX()) + ", " + (corners[0].getZ()));
							sendMessage(player, "§9Corner 2: §2" + (corners[1].getX()) + ", " + (corners[1].getZ()));
						} else {
							sendMessage(sender, "§cThere is no claim here!");
						}
					}
				} else if(args[1].equals("list")) {
					if(sender instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) sender;
						Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
						int i = 0;
						for(ClaimArea claim : claims) {
							if(claim.isOwner(player)) {
								i++;
								sendMessage(sender, "§9§nClaim §a§n#" + i);
								sendMessage(sender, "§5Dimension: " + claim.getDimensionID());
								sendMessage(sender, "§9Location: §2" + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
							}
						}
						if(i == 0) {
							sendMessage(sender, "§cYou don't own any claims!");
						}
					} else { // Sender is console!
						sendMessage(sender, "Detected server console. Getting all claims...");
						Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
						int i = 0;
						for(ClaimArea claim : claims) {
							i++;
							sendMessage(sender, "####CLAIM INFO####");
							sendMessage(sender, "Claim #" + i + ", owned by: " + ClaimManager.getPlayerName(claim.getOwner().toString(), sender.getEntityWorld()));
							sendMessage(sender, "Dimension: " + claim.getDimensionID());
							sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
						}
						if(i == 0) {
							sendMessage(sender, "There's no claims on the server!");
						}
					}
				}
			} else if(args.length == 4 && args[1].equals("delete")) {
				int posX = 0;
				int posY = 0;
				try {
					posX = Integer.parseInt(args[2]);
					posY = Integer.parseInt(args[3]);
					ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), new BlockPos(posX,0,posY));
					if(claim != null) {
						if(sender instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) sender;
							if(claim.isOwner(player)) {
								ClaimManager.getManager().deleteClaim(claim);
								sendMessage(sender, "§b§nDeleted claim:");
								sendMessage(sender, "§5Dimension: " + claim.getDimensionID());
								sendMessage(sender, "§9Location: §2" + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
							} else {
								sendMessage(sender, "§cNo claim there or you don't own the claim!");
							}
						} else {
							if(sender.canUseCommand(2, "")) {
								sendMessage(sender, "Deleted claim:");
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
					sendMessage(sender, "§cInvalid location! Use §e/claimit claim delete (Corner 1 X) (Corner 1 Z)");
				}
			}
		} else if(args[0].equals("admin")) {
			if(sender instanceof EntityPlayer) {
				if(sender.canUseCommand(2, "")) {
					if(ClaimManager.getManager().isAdmin((EntityPlayer) sender)) {
						ClaimManager.getManager().removeAdmin((EntityPlayer) sender);
						sendMessage(sender, "§aAdmin bypass disabled.");
					} else {
						ClaimManager.getManager().addAdmin((EntityPlayer) sender);
						sendMessage(sender, "§aAdmin bypass enabled. You may now manage all claims.");
					}
				} else {
					sendMessage(sender, "§cYou are not operator level 2!");
				}
			} else {
				sendMessage(sender, "You must be a player to use this command!");
			}
		} else {
			throw new SyntaxErrorException("Unknown subcommand or you are using server console!");
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		List<String> completions = new LinkedList<String>();
		completions.add("claimit help");
		return completions;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}
