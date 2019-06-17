package its_meow.claimit.command;

import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.FTC;
import its_meow.claimit.util.text.FTC.Form;
import its_meow.claimit.util.text.TextComponentCommand;
import its_meow.claimit.util.text.TextComponentStyled;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public abstract class CommandCIBase extends CommandBase implements ICommandHelp {

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(getRequiredPermissionLevel(), "claimit.command." + this.getPermissionString()) || !Loader.isModLoaded("sponge");
    }

    public abstract String getPermissionString();

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    /**
     * Sends message to sender.
     */
    // protected static void sendMessage(ICommandSender sender, String message) {
    //   sender.sendMessage(new TextComponentString(message));
    //}

    protected static void sendBMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

    public static void sendMessage(ICommandSender sender, TextFormatting color, String message) {
        sender.sendMessage(new FTC(message, color));
    }

    public static void sendMessage(ICommandSender sender, TextFormatting color, Form form, String message) {
        sender.sendMessage(new FTC(message, color, form));
    }

    public static void sendMessage(ICommandSender sender, Form form, String message) {
        sender.sendMessage(new FTC(message, form));
    }

    public static void sendMessage(ICommandSender sender, ITextComponent... styled) {
        if(styled.length > 0) {
            ITextComponent comp = styled[0];
            if(styled.length > 1) {
                for(int i = 1; i < styled.length; i++) {
                    comp.appendSibling(styled[i]);
                }
            }
            sender.sendMessage(comp);
        }
    }

    /**
     * Sends message to a sender. If clicked, will run message as a command.
     */
    public static void sendCMessage(ICommandSender sender, TextFormatting formatting, String message) {
        sendCMessage(sender, formatting.toString(), message);
    }

    /**
     * Sends message to sender. If clicked, will run message as a command.
     */
    public static void sendCMessage(ICommandSender sender, String formatting, String message) {
        sender.sendMessage(new TextComponentCommand(formatting, message));
    }

    /**
     * Sends message to sender, with a style.
     */
    public static void sendSMessage(ICommandSender sender, String message, Style style) {
        sender.sendMessage(new TextComponentStyled(message, style));
    }

    /**
     * Sends message to sender. If the sender is admin, attaches style.
     */
    public static void sendAdminStyleMessage(ICommandSender sender, String message, Style style) {
        if(CommandUtils.isAdmin(sender)) sendSMessage(sender, message, style); else sendBMessage(sender, message);
    }

    /**
     * Sends message to sender only if they are admin
     */
    public static void sendMessageIfAdmin(ICommandSender sender, String message) {
        if(CommandUtils.isAdmin(sender)) sendBMessage(sender, message);
    }
    
    public static void runIfAdmin(ICommandSender sender, Runnable run) {
        if(CommandUtils.isAdmin(sender)) run.run();
    }

    /**
     * Sends message to sender only if they are admin, with a style.
     */
    public static void sendSMessageIfAdmin(ICommandSender sender, String message, Style style) {
        if(CommandUtils.isAdmin(sender)) sendSMessage(sender, message, style);
    }

}
