package its_meow.claimit.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RESET;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.command.claimit.CommandSubAdmin;
import its_meow.claimit.command.claimit.CommandSubCancel;
import its_meow.claimit.command.claimit.CommandSubClaim;
import its_meow.claimit.command.claimit.CommandSubClaimBlocks;
import its_meow.claimit.command.claimit.CommandSubConfig;
import its_meow.claimit.command.claimit.CommandSubConfirm;
import its_meow.claimit.command.claimit.CommandSubGroup;
import its_meow.claimit.command.claimit.CommandSubHelp;
import its_meow.claimit.command.claimit.CommandSubShowBorders;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.util.command.CommandHelpRegistry;
import its_meow.claimit.util.text.AutoFillHelpChatStyle;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Loader;

public class CommandClaimIt extends CommandCITreeBase {

	public CommandClaimIt() {
	    super(
	        new CommandSubClaim(),
		    new CommandSubGroup(),
		    new CommandSubAdmin(),
		    new CommandSubConfirm(),
		    new CommandSubCancel(),
		    new CommandSubConfig(),
		    new CommandSubClaimBlocks(),
		    new CommandSubShowBorders(),
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
    public void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        sendMessage(sender, GRAY + "" + BOLD + ClaimIt.NAME + RESET + "" + DARK_PURPLE + " Version " + YELLOW + ClaimIt.VERSION + DARK_PURPLE + " by " + DARK_RED + "" + BOLD + "its_meow");
        sendMessage(sender, GRAY + "" + BOLD + ClaimItAPI.NAME + RESET + "" + DARK_PURPLE + " Version " + YELLOW + ClaimItAPI.VERSION);
        sendMessage(sender, BLUE + "Claiming item is " + RESET + GREEN + ITALIC + ClaimItConfig.claim_create_item_display);
        sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
        List<String> subCommands = Arrays.asList(new String[] {"claim", "group", "config", "claimblocks", "showborders", "admin", "help"});
        for(String cmdT : subCommands) {
            String cmd = "/claimit " + cmdT;
            CommandCIBase subCmd = this.getSubCommand(cmdT);
            sendSMessage(sender, YELLOW + cmd, new AutoFillHelpChatStyle(cmd, subCmd, sender));
        }
        sendMessage(sender, AQUA + "Alias(es): " + YELLOW + aliasList);
    }

    @Override
    public String getPermissionString() {
        return "claimit";
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(0, "claimit") || !Loader.isModLoaded("sponge");
    }

}
