package its_meow.claimit.command;

import its_meow.claimit.util.text.TextComponentCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public abstract class CommandCIBase extends CommandBase implements ICommandHelp {
    
    protected static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }
    
    protected static void sendCMessage(ICommandSender sender, TextFormatting formatting, String message) {
        sendCMessage(sender, formatting.toString(), message);
    }

    protected static void sendCMessage(ICommandSender sender, String formatting, String message) {
        sender.sendMessage(new TextComponentCommand(formatting, message));
    }
    
}
