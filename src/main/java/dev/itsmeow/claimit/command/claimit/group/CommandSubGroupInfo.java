package dev.itsmeow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.group.Group;
import dev.itsmeow.claimit.api.group.GroupManager;
import dev.itsmeow.claimit.api.permission.ClaimPermissionMember;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.ColorUtil;
import dev.itsmeow.claimit.util.text.FTC;
import dev.itsmeow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSubGroupInfo extends CommandCIBase {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group info <groupname>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Provides information on a group. First argument is group name.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 1) {
            sendMessage(sender, RED, "Invalid argument count. Specify a group name. Usage: " + this.getUsage(sender));
        }

        if(args.length == 1) {
            if(sender instanceof EntityPlayer) { 
                EntityPlayer player = ((EntityPlayer) sender);
                Group group = GroupManager.getGroup(args[0]);
                if(group != null) {
                    outputGroupInfo(group, player);
                } else {
                    sendMessage(sender, RED, "No group with this name!");
                }
            }
        }


    }

    private static void outputGroupInfo(Group group, EntityPlayer player) throws CommandException {
        World world = player.getEntityWorld();
        String ownerName = CommandUtils.getNameForUUID(group.getOwner(), world.getMinecraftServer());

        sendMessage(player, new FTC(BLUE, Form.BOLD, "Information for group owned by "), new FTC(GREEN, Form.BOLD, ownerName), new FTC(BLUE, Form.BOLD, ":"));
        sendMessage(player, new FTC(BLUE, "Group Name: "), new FTC(DARK_GREEN, group.getName()));
        sendMessage(player, new FTC(BLUE, "Group Tag: "), ColorUtil.getGroupTagComponent(group));
        ImmutableSetMultimap<UUID, ClaimPermissionMember> permMap = group.getMembers();
        if(permMap == null || permMap.isEmpty()) {
            sendMessage(player, YELLOW, "No members.");
        } else {
            sendMessage(player, YELLOW, Form.BOLD, "Members:");
            for(UUID member : permMap.keySet()) {
                String permString = "";
                ImmutableSet<ClaimPermissionMember> permSet = permMap.get(member);
                for(ClaimPermissionMember p : permSet) {
                    permString += p.parsedName + ", ";
                }
                int end = permString.lastIndexOf(',');
                permString = permString.substring(0, end);
                sendMessage(player, new FTC(YELLOW, CommandUtils.getNameForUUID(member, player.getEntityWorld().getMinecraftServer())), new FTC(BLUE, ": "), new FTC(GREEN, permString));
            }
        }
        if(group.getClaims().size() == 0 || group.getClaims().isEmpty()) {
            sendMessage(player, YELLOW, "No claims.");
        } else {
            sendMessage(player, YELLOW, Form.BOLD, "Claims:");
            for(ClaimArea claim : group.getClaims()) {
                sendMessage(player, new FTC(BLUE, " + "), new FTC(YELLOW, claim.getDisplayedViewName()), new FTC(BLUE, " of ") , new FTC(GREEN, CommandUtils.getNameForUUID(claim.getOwner(), world.getMinecraftServer())));
            }
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
        return "claimit.group.info";
    }

}