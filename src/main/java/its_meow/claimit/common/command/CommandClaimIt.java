package its_meow.claimit.common.command;

import java.util.LinkedList;
import java.util.List;

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
			sendMessage(sender, "§bClaimIt version §e" + Ref.VERSION + " §b by its_meow");
			sendMessage(sender, "§bFor possible commands, run §e/claimit help");
			sendMessage(sender, "§bAlias(es): §e" + aliasList);
		} else if(args[0].equals("help") && args.length == 1) {
			sendMessage(sender, "§l§bSubcommands: ");
			sendMessage(sender, "§e/claimit");
			sendMessage(sender, "§e/claimit help");
			sendMessage(sender, "§e/claimit claim");
		} else if(args[0].equals("claim") && sender.getCommandSenderEntity() instanceof EntityPlayer) {
			if(args.length == 1) {
				sendMessage(sender, "§l§bSubcommands: ");
				sendMessage(sender, "§eclaim delete");
				sendMessage(sender, "§eclaim info");
				sendMessage(sender, "§eclaim list");
			} else if(args.length == 2) {
				if(args[1].equals("delete")) {
					if(sender instanceof EntityPlayer) {
						ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
						if(claim != null) {
							if(claim.isOwner((EntityPlayer) sender.getCommandSenderEntity())) {
								boolean removed = ClaimManager.getManager().deleteClaim(claim, (EntityPlayer) sender.getCommandSenderEntity());
								sendMessage(sender, removed ? "§eClaim deleted." : "§cCould not remove claim!");
							} else {
								sendMessage(sender, "§cYou do not own this claim!");
							}
						} else {
							sendMessage(sender, "§cThere is no claim here!");
						}
					} else {
						sendMessage(sender, "You must be a player to use this command!");
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
