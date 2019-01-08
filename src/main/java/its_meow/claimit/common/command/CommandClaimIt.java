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
import net.minecraftforge.common.DimensionManager;

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
			sendMessage(sender, "§b§lSubcommands: ");
			sendMessage(sender, "§e/claimit claim");
			sendMessage(sender, "§e/claimit admin");
		} else if(args[0].equals("claim")) {
			if(args.length == 1) {
				sendMessage(sender, "§b§lSubcommands: ");
				sendMessage(sender, "§eclaim delete");
				sendMessage(sender, "§eclaim info");
				sendMessage(sender, "§eclaim list");
				sendMessage(sender, "§eclaim setname");
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
							sendMessage(sender, "§cThere is no claim here! You can use §e/claimit claim delete (Corner 1 X) (Corner 1 Z) (Dimension ID) §cto delete a claim remotely.");
							sendMessage(sender, "§cYou can also use §e/claimit claim delete (claim name) §cto delete a claim remotely.");
						}
					} else {
						sendMessage(sender, "You must be a player to use this command!");
					}
				} else if(args[1].equals("info")) {
					if(sender instanceof EntityPlayer) {
						ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
						if(claim != null) {
							outputClaimInfo(claim, (EntityPlayer) sender);
						} else {
							sendMessage(sender, "§cThere is no claim here!");
						}
					}
				} else if(args[1].equals("list")) {
					if(sender instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) sender;
						if(!ClaimManager.getManager().isAdmin(player)) {
							Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
							int i = 0;
							for(ClaimArea claim : claims) {
								if(claim.isOwner(player)) {
									i++;
									sendMessage(sender, "§9§lClaim §a§l#" + i);
									sendMessage(sender, "§9Claim Name: §2" + claim.getDisplayedViewName());
									sendMessage(sender, "§5Dimension: " + claim.getDimensionID());
									sendMessage(sender, "§9Location: §2" + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
								}
							}
							if(i == 0) {
								sendMessage(sender, "§cYou don't own any claims!");
							}
						} else {
							sendMessage(sender, "§cYou are admin. Getting all claims...");
							Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
							int i = 0;
							for(ClaimArea claim : claims) {
								i++;
								sendMessage(sender, "####CLAIM INFO####");
								sendMessage(sender, "Claim #" + i + ", owned by: " + ClaimManager.getPlayerName(claim.getOwner().toString(), sender.getEntityWorld()));
								sendMessage(sender, "Claim Name: " + claim.getTrueViewName());
								sendMessage(sender, "Dimension: " + claim.getDimensionID());
								sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
							}
							if(i == 0) {
								sendMessage(sender, "There's no claims on the server!");
							}
						}
					} else { // Sender is console!
						sendMessage(sender, "Detected server console. Getting all claims...");
						Set<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
						int i = 0;
						for(ClaimArea claim : claims) {
							i++;
							sendMessage(sender, "####CLAIM INFO####");
							sendMessage(sender, "Claim #" + i + ", owned by: " + ClaimManager.getPlayerName(claim.getOwner().toString(), sender.getEntityWorld()));
							sendMessage(sender, "Claim Name: " + claim.getTrueViewName());
							sendMessage(sender, "Dimension: " + claim.getDimensionID());
							sendMessage(sender, "Location: " + (claim.getMainPosition().getX()) + ", " + (claim.getMainPosition().getZ()));
						}
						if(i == 0) {
							sendMessage(sender, "There's no claims on the server!");
						}
					}
				}
			} else if(args.length == 5 && args[1].equals("delete")) {
				int posX = 0;
				int posY = 0;
				int dim = 0;
				try {
					posX = Integer.parseInt(args[2]);
					posY = Integer.parseInt(args[3]);
					dim = Integer.parseInt(args[4]);
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
							if(sender.canUseCommand(2, "")) {
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
			} else if(args.length == 3) {
				if(args[1].equals("setname")) { // ci claim setname (name)
					if(sender instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) sender;
						ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(player.world, player.getPosition());
						if(claim != null) {
							if(claim.isTrueOwner(player)) {
								claim.setViewName(args[2], player);
								sendMessage(sender, "§bSet this claim's name to: §a" + claim.getDisplayedViewName());
							} else {
								if(ClaimManager.getManager().isAdmin(player)) {
									sendMessage(sender, "§cAdmins cannot change claim names.");
								} else {
									sendMessage(sender, "§cYou don't own this claim!");
								}
							}
						} else {
							sendMessage(sender, "§cNo claim there or you don't own the claim!");
						}
					} else {
						sendMessage(sender, "You must be a player to use this command!");
					}
				}
				if(args[1].equals("info")) { // ci claim info (name)
					if(sender instanceof EntityPlayer) { 
						EntityPlayer player = ((EntityPlayer) sender);
						ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(args[2], EntityPlayer.getUUID(player.getGameProfile()));
						if(claim != null) {
							outputClaimInfo(claim, player);
						} else {
							sendMessage(sender, "§cNo claim with this name that you own!");
						}
					}
				}
				if(args[1].equals("delete")) { // ci claim delete (name)
					if(sender instanceof EntityPlayer) { 
						EntityPlayer player = ((EntityPlayer) sender);
						ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(args[2], EntityPlayer.getUUID(player.getGameProfile()));
						if(claim == null && ClaimManager.getManager().isAdmin(player)) {
							claim = ClaimManager.getManager().getClaimByTrueName(args[2]);
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
					}
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
					sendMessage(sender, "§cYou are not operator level 2/cheats are disabled!");
				}
			} else {
				sendMessage(sender, "You must be a player to use this command!");
			}
		} else {
			throw new SyntaxErrorException("Unknown subcommand.");
		}
	}

	private void outputClaimInfo(ClaimArea claim, EntityPlayer player) {
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
		sendMessage(player, "§9Dimension: §5" + dim);
		sendMessage(player, "§9Area: §b" + (claim.getSideLengthX() + 1) + "§9x§b" + (claim.getSideLengthZ() + 1) + " §9(§b" + claim.getArea() + "§9) ");
		sendMessage(player, "§9Corner 1: §2" + (corners[0].getX()) + ", " + (corners[0].getZ()));
		sendMessage(player, "§9Corner 2: §2" + (corners[1].getX()) + ", " + (corners[1].getZ()));
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
