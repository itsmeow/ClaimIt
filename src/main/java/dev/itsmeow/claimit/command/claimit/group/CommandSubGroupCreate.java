package dev.itsmeow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import dev.itsmeow.claimit.api.group.Group;
import dev.itsmeow.claimit.api.group.GroupManager;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubGroupCreate extends CommandCIBase {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group create <groupname>";
    }
    
    @Override
    public String getHelp(ICommandSender sender) {
        return "Creates a group with specified name. Owner has all permissions within group.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            String groupname = args[0];
            if(sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                boolean pass = GroupManager.addGroup(new Group(groupname, player.getGameProfile().getId()));
                if(pass) {
                    sendMessage(sender, new FTC(AQUA, "Created group: "), new FTC(GREEN, groupname));
                } else {
                    sendMessage(sender, RED, "Failed to create group. There is another group with this name.");
                }
            } else {
                sendMessage(sender, RED, "You must be a player to use this command!");
            }
        } else {
            throw new SyntaxErrorException("Invalid syntax. Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.group.create";
    }

}