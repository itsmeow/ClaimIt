package dev.itsmeow.claimit.util.text;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;

import static net.minecraft.util.text.TextFormatting.RED;

import net.minecraft.command.ICommandSender;

public class AutoFillHelpChatStyle extends AutoFillChatStyle {

    public AutoFillHelpChatStyle(String command, CommandCIBase cmd, ICommandSender sender) {
        super(command, true, GREEN + "Click to autofill. \n" 
                + GREEN + "Usage: " + AQUA + cmd.getUsage(sender) + "\n" 
                + GREEN + "Help: " + YELLOW + cmd.getHelp(sender) + (!CommandUtils.isAdmin(sender) ? "" : "\n"
                + RED + "Permission String: " + YELLOW + cmd.getPermissionString()));
    }

}
