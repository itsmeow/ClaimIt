package its_meow.claimit.command;

import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.TextComponentCommand;
import its_meow.claimit.util.text.TextComponentStyled;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public abstract class CommandCIBase extends CommandBase implements ICommandHelp {

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    /**
     * Sends message to sender.
     */
    protected static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

    /**
     * Sends message to a sender. If clicked, will run message as a command.
     */
    protected static void sendCMessage(ICommandSender sender, TextFormatting formatting, String message) {
        sendCMessage(sender, formatting.toString(), message);
    }

    /**
     * Sends message to sender. If clicked, will run message as a command.
     */
    protected static void sendCMessage(ICommandSender sender, String formatting, String message) {
        sender.sendMessage(new TextComponentCommand(formatting, message));
    }

    /**
     * Sends message to sender, with a style.
     */
    protected static void sendSMessage(ICommandSender sender, String message, Style style) {
        sender.sendMessage(new TextComponentStyled(message, style));
    }

    /**
     * Sends message to sender. If the sender is admin, attaches style.
     */
    protected static void sendAdminStyleMessage(ICommandSender sender, String message, Style style) {
        if(CommandUtils.isAdmin(sender)) sendSMessage(sender, message, style); else sendMessage(sender, message);
    }

    /**
     * Sends message to sender only if they are admin
     */
    protected static void sendMessageIfAdmin(ICommandSender sender, String message) {
        if(CommandUtils.isAdmin(sender)) sendMessage(sender, message);
    }

    /**
     * Sends message to sender only if they are admin, with a style.
     */
    protected static void sendSMessageIfAdmin(ICommandSender sender, String message, Style style) {
        if(CommandUtils.isAdmin(sender)) sendSMessage(sender, message, style);
    }

}
