package its_meow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.help.CommandSubHelpCommand;
import its_meow.claimit.command.claimit.help.CommandSubHelpPermission;
import its_meow.claimit.command.claimit.help.CommandSubHelpTopic;
import its_meow.claimit.command.claimit.help.CommandSubHelpUserConfig;
import its_meow.claimit.util.text.CommandChatStyle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelp extends CommandCITreeBase {
    
    public CommandSubHelp() {
        super(new CommandSubHelpCommand(), new CommandSubHelpPermission(), new CommandSubHelpUserConfig(), new CommandSubHelpTopic());
    }
    
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit help <topic type>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "The help command. Are you okay? Asking for help on getting help while also getting help? What are you doing? Put another command to view help on it.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
    
    public void displaySubCommands(MinecraftServer server, ICommandSender sender) throws CommandException {
        sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
        for(CommandCIBase subCmd : this.getSubCommands()) {
            String cmd = this.getUsage(sender).substring(0, this.getUsage(sender).indexOf(this.getName() + " ") + this.getName().length()) + " " + subCmd.getName();
            sendSMessage(sender, YELLOW + cmd, new CommandChatStyle(cmd, true, "Click to run"));
        }
    }

}
