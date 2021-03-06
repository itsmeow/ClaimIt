package dev.itsmeow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.command.CommandCITreeBase;
import dev.itsmeow.claimit.command.claimit.help.CommandSubHelpCommand;
import dev.itsmeow.claimit.command.claimit.help.CommandSubHelpPermission;
import dev.itsmeow.claimit.command.claimit.help.CommandSubHelpTopic;
import dev.itsmeow.claimit.command.claimit.help.CommandSubHelpUserConfig;
import dev.itsmeow.claimit.util.text.CommandChatStyle;
import dev.itsmeow.claimit.util.text.FTC.Form;
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
    public String getPermissionString() {
        return "claimit.help";
    }

    public void displaySubCommands(MinecraftServer server, ICommandSender sender) throws CommandException {
        sendMessage(sender, AQUA, Form.BOLD, "Subcommands: ");
        for(CommandCIBase subCmd : this.getSubCommands()) {
            String cmd = this.getUsage(sender).substring(0, this.getUsage(sender).indexOf(this.getName() + " ") + this.getName().length()) + " " + subCmd.getName();
            sendSMessage(sender, cmd, new CommandChatStyle(cmd, true, "Click to run").setColor(YELLOW));
        }
    }

}
