package its_meow.claimit.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RESET;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.config.ClaimItAPIConfig;
import its_meow.claimit.command.claimit.CommandSubAdmin;
import its_meow.claimit.command.claimit.CommandSubCancel;
import its_meow.claimit.command.claimit.CommandSubClaim;
import its_meow.claimit.command.claimit.CommandSubClaimBlocks;
import its_meow.claimit.command.claimit.CommandSubConfig;
import its_meow.claimit.command.claimit.CommandSubConfirm;
import its_meow.claimit.command.claimit.CommandSubGroup;
import its_meow.claimit.command.claimit.CommandSubHelp;
import its_meow.claimit.command.claimit.CommandSubShowBorders;
import its_meow.claimit.command.claimit.CommandSubSubClaim;
import its_meow.claimit.command.claimit.CommandSubTrust;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.util.command.CommandHelpRegistry;
import its_meow.claimit.util.text.AutoFillHelpChatStyle;
import its_meow.claimit.util.text.FTC;
import its_meow.claimit.util.text.FTC.Form;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;

public class CommandClaimIt extends CommandCITreeBase {

	public CommandClaimIt() {
	    super(
	        new CommandSubClaim(),
		    new CommandSubGroup(),
		    ClaimItAPIConfig.enable_subclaims ? new CommandSubSubClaim() : null,
		    new CommandSubAdmin(),
		    new CommandSubConfirm(),
		    new CommandSubCancel(),
		    new CommandSubConfig(),
		    new CommandSubClaimBlocks(),
		    new CommandSubShowBorders(),
		    new CommandSubHelp(),
		    new CommandSubTrust()
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

	@Override
	public List<String> getAliases() {
		return Arrays.asList("ci");
	}

    @Override
    public void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        sendMessage(sender, new FTC(GRAY, Form.BOLD, ClaimIt.NAME), new FTC(" Version ", DARK_PURPLE, false), new FTC(ClaimIt.VERSION, YELLOW, false), new FTC(" by ", DARK_PURPLE, false), new FTC(DARK_RED, Form.BOLD, "its_meow"));
        sendMessage(sender, new FTC(GRAY, Form.BOLD, ClaimItAPI.NAME), new FTC(" Version ", DARK_PURPLE, false), new FTC(ClaimItAPI.VERSION, YELLOW, false));
        sendMessage(sender, new FTC(BLUE, "Claiming item is "), new TextComponentString("" + RESET + GREEN + ITALIC + ClaimItConfig.claim_create_item_display));
        sendMessage(sender, AQUA, Form.BOLD, "Subcommands: ");
        List<String> subCommands = Lists.newArrayList("claim", "group", "trust", "config", "claimblocks", "showborders", "admin", "help");
        if(ClaimItAPIConfig.enable_subclaims) {
            subCommands.add("subclaim");
        }
        for(String cmdT : subCommands) {
            String cmd = "/claimit " + cmdT;
            CommandCIBase subCmd = this.getSubCommand(cmdT);
            sendSMessage(sender, YELLOW + cmd, new AutoFillHelpChatStyle(cmd, subCmd, sender));
        }
        sendMessage(sender, new FTC(AQUA, "Alias(es): "), new FTC(YELLOW, "/ci"));
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
