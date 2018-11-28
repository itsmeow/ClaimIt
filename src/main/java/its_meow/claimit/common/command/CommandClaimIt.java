package its_meow.claimit.common.command;

import java.util.LinkedList;
import java.util.List;

import its_meow.claimit.Ref;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
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
		if(args.length == 0) {
			sendMessage(sender, "§bClaimIt version §e" + Ref.VERSION + " §b by its_meow");
			sendMessage(sender, "§bFor possible commands, run §e/claimit help");
			sendMessage(sender, "§bAlias(es): §e" + aliasList);
		} else if(args[0] == "help" && args.length == 1) {
			sendMessage(sender, "§l§bSubcommands: ");
			sendMessage(sender, "§e/claimit");
			sendMessage(sender, "§e/claimit help");
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
