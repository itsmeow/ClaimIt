package dev.itsmeow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import java.util.ArrayList;
import java.util.List;

import dev.itsmeow.claimit.api.group.Group;
import dev.itsmeow.claimit.api.group.GroupManager;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubGroupDelete extends CommandCIBase {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group delete <groupname>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Deletes a group. Removes all member's permissions within claims in the group.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            String groupname = args[0];
            Group group = GroupManager.getGroup(groupname);
            if(group != null) {
                if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.delete.others") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer)sender))) {
                    GroupManager.removeGroup(group);
                    sendMessage(sender, new FTC(AQUA, "Deleted group: "), new FTC(GREEN, groupname));
                } else {
                    sendMessage(sender, RED, "You do not own this group!");
                }
            } else {
                sendMessage(sender, RED, "No such group: " + groupname);
            }
        } else {
            throw new SyntaxErrorException("Invalid syntax. Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
            BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getRelevantGroupNames(sender));
        }
        return new ArrayList<String>();
    }

    @Override
    public String getPermissionString() {
        return "claimit.group.delete";
    }



}