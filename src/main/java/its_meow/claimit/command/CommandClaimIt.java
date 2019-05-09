package its_meow.claimit.command;

import static net.minecraft.util.text.TextFormatting.*;

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
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.util.command.CommandHelpRegistry;
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
	    CommandHelpRegistry.registerHelp(this.getName(), this::getHelp);
	}

	@Override
	public String getName() {
		return "claimit";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit [subcommand]";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "Base command for all of ClaimIt. Click on subcommands/run them to do stuff. Provides version of mod as well.";
    }

	private String aliasList = "";

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("ci");
		aliases.iterator().forEachRemaining(s -> aliasList += "/" + s + " ");
		return aliases;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

    @Override
    public void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        sendMessage(sender, GRAY + "" + BOLD + ClaimIt.NAME + RESET + "" + DARK_PURPLE + " Version " + YELLOW + ClaimIt.VERSION + DARK_PURPLE + " by " + DARK_RED + "" + BOLD + "its_meow");
        sendMessage(sender, GRAY + "" + BOLD + ClaimItAPI.NAME + RESET + "" + DARK_PURPLE + " Version " + YELLOW + ClaimItAPI.VERSION);
        sendMessage(sender, GRAY + "" + BOLD + "" + ITALIC + "Claiming item is " + RESET + DARK_RED + ClaimItConfig.claim_create_item_display);
        sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
        sendCMessage(sender, YELLOW, "/claimit claim");
        sendCMessage(sender, YELLOW, "/claimit group");
        sendCMessage(sender, YELLOW, "/claimit config");
        sendCMessage(sender, YELLOW, "/claimit admin");
        sendCMessage(sender, YELLOW, "/claimit help");
        sendMessage(sender, AQUA + "Alias(es): " + YELLOW + aliasList);
    }

}
