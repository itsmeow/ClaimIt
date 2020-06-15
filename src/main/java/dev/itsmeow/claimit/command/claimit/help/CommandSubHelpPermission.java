package dev.itsmeow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import dev.itsmeow.claimit.api.permission.ClaimPermissionMember;
import dev.itsmeow.claimit.api.permission.ClaimPermissionRegistry;
import dev.itsmeow.claimit.api.permission.ClaimPermissionToggle;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.text.CommandChatStyle;
import dev.itsmeow.claimit.util.text.FTC;
import dev.itsmeow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelpPermission extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return "Base help command for permissions (member and toggle). Really, stop asking for help on help. Just follow the instructions.";
    }

    @Override
    public String getName() {
        return "permission";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit help permission <member/toggle> [permission name]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sendMessage(sender, new FTC("Available choices: ", GOLD, Form.UNDERLINE), new FTC("/claimit help permission <choice>", YELLOW));
            sendSMessage(sender, GREEN + "member", new CommandChatStyle("/claimit help permission member", true, "Click for option"));
            sendSMessage(sender, GREEN + "toggle", new CommandChatStyle("/claimit help permission toggle", true, "Click for option"));
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("member") || args[0].equalsIgnoreCase("toggle")) {
            if(args[0].equalsIgnoreCase("member")) {
                if(args.length == 1) {
                    sendMessage(sender, new FTC("Member Permissions:", GOLD, Form.UNDERLINE));
                    for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
                        sendMessage(sender, new FTC(perm.parsedName + ": ", GREEN), new FTC(perm.helpInfo, YELLOW));
                    }
                } else if(args.length == 2) {
                    ClaimPermissionMember perm = ClaimPermissionRegistry.getPermissionMember(args[1]);
                    if(perm != null) {
                        sendMessage(sender, new FTC(perm.parsedName + ": ", GREEN), new FTC(perm.helpInfo, YELLOW));
                    } else {
                        throw new CommandException("No such permission \"" + args[1] + "\"");
                    }
                } else {
                    throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
                }
            }
            if(args[0].equalsIgnoreCase("toggle")) {
                if(args.length == 1) {
                    sendMessage(sender, new FTC("Toggle Permissions:", GOLD, Form.UNDERLINE));
                    for(ClaimPermissionToggle perm : ClaimPermissionRegistry.getTogglePermissions()) {
                        sendMessage(sender, new FTC(perm.parsedName + ": ", GREEN), new FTC(perm.helpInfo, YELLOW), new FTC(" (Default: " + perm.getDefault() + ")", BLUE));
                    }
                } else if(args.length == 2) {
                    ClaimPermissionToggle perm = ClaimPermissionRegistry.getPermissionToggle(args[1]);
                    if(perm != null) {
                        sendMessage(sender, new FTC(perm.parsedName + ": ", GREEN), new FTC(perm.helpInfo, YELLOW));
                    } else {
                        throw new CommandException("No such permission \"" + args[1] + "\"");
                    }
                } else {
                    throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
                }
            }
        } else {
            throw new CommandException("Invalid type! Specify member or toggle. Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.help.permission";
    }

}
