package its_meow.claimit.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.RESET;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.command.claimit.CommandSubAdmin;
import its_meow.claimit.command.claimit.CommandSubCancel;
import its_meow.claimit.command.claimit.CommandSubClaim;
import its_meow.claimit.command.claimit.CommandSubConfig;
import its_meow.claimit.command.claimit.CommandSubConfirm;
import its_meow.claimit.command.claimit.CommandSubGroup;
import its_meow.claimit.command.claimit.CommandSubHelp;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandClaimIt extends CommandCITreeBase {

	public CommandClaimIt() {
	    super(
	        new CommandSubClaim(),
		    new CommandSubGroup(),
		    new CommandSubAdmin(),
		    new CommandSubConfirm(),
		    new CommandSubCancel(),
		    new CommandSubConfig(),
		    new CommandSubHelp()
		);
	}

	@Override
	public String getName() {
		return "claimit";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit [subcommand]";
	}

	private String aliasList = "";

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("claimit");
		aliases.add("ci");
		aliases.iterator().forEachRemaining(s -> aliasList += "/" + s + " ");
		return aliases;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	/*@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		List<String> completions = new LinkedList<String>();
		\* 
		 * Key:
		 * . = The part to be completed
		 * * = Wildcard. Could be any value here (may be limited to a set)
		 * + = General filter. No spot is completed here, but we are completing every part past and at this.
		 * *\/

		if(args.length == 1) { // claimit .
			completions.add("claim");
			completions.add("config");
			completions.add("admin");
			completions.add("cancel");
			completions.add("confirm");
		} else {
			if(args[0].equals("claim") && args.length == 2) { // claimit claim .
				completions.add("info");
				completions.add("delete");
				completions.add("list");
				completions.add("setname");
				completions.add("member");
				completions.add("toggle");
			}
			if(args.length > 2 && args[0].equals("claim") && args[1].equals("member")) { // claimit claim member +
				if(args.length == 3) { // claimit claim member .
					completions.add("add");
					completions.add("remove");
					completions.add("list");
				}
				if(args.length == 4) { // claimit claim member * .
					for(ClaimPermission perm : ClaimPermissionRegistry.getMemberPermissions()) {
						completions.add(perm.parsedName);
					}
				} else if(args.length == 5 && args[2].equals("list")) { // claimit claim member list * .
					for(ClaimPermission perm : ClaimPermissionRegistry.getMemberPermissions()) {
						completions.add(perm.parsedName);
					}
				}
			} else if(args.length > 2 && args[0].equals("claim") && args[1].equals("toggle")) { // claimit claim toggle +
				if(args.length == 3) { // claimit claim toggle .
					for(ClaimPermission perm : ClaimPermissionRegistry.getTogglePermissions()) {
						completions.add(perm.parsedName);
					}
				}
			}
		}

		return completions;
	}*/

    @Override
    protected void displaySubCommands(MinecraftServer server, ICommandSender sender) {
        sendMessage(sender, GRAY + "" + BOLD + ClaimIt.NAME + RESET + "" + DARK_PURPLE + " Version " + YELLOW + ClaimIt.VERSION + DARK_PURPLE + " by " + DARK_RED + "" + BOLD + "its_meow");
        sendMessage(sender, GRAY + "" + BOLD + ClaimItAPI.NAME + RESET + "" + DARK_PURPLE + " Version " + YELLOW + ClaimItAPI.VERSION);
        sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
        sendCMessage(sender, YELLOW, "/claimit claim");
        sendCMessage(sender, YELLOW, "/claimit group");
        sendCMessage(sender, YELLOW, "/claimit config");
        sendCMessage(sender, YELLOW, "/claimit admin");
        sendCMessage(sender, YELLOW, "/claimit help");
        sendMessage(sender, AQUA + "Alias(es): " + YELLOW + aliasList);
    }

}
