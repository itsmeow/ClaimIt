package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.UUID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 1) {
            sendMessage(sender, RED + "Invalid argument count. Specify a group name. Usage: " + this.getUsage(sender));
        }

        if(args.length == 1) {
            if(sender instanceof EntityPlayer) { 
                EntityPlayer player = ((EntityPlayer) sender);
                Group group = GroupManager.getGroup(args[0]);
                if(group != null) {
                    outputGroupInfo(group, player);
                } else {
                    sendMessage(sender, RED + "No group with this name!");
                }
            }
        }


    }

    private static void outputGroupInfo(Group group, EntityPlayer player) throws CommandException {
        World world = player.getEntityWorld();
        String ownerName = CommandUtils.getNameForUUID(group.getOwner(), world.getMinecraftServer());

        sendMessage(player, BLUE + "" + BOLD + "Information for group owned by " + GREEN + "" + BOLD + ownerName + BLUE + "" + BOLD + ":");
        sendMessage(player, BLUE + "Group Name: " + DARK_GREEN + group.getName());
  
        ImmutableSetMultimap<UUID, ClaimPermissionMember> permMap = group.getMembers();
        if(permMap == null || permMap.isEmpty()) {
            sendMessage(player, YELLOW + "No members.");
        } else {
            sendMessage(player, YELLOW + "" + BOLD + "Members:");
            for(UUID member : permMap.keySet()) {
                String permString = "";
                ImmutableSet<ClaimPermissionMember> permSet = permMap.get(member);
                for(ClaimPermissionMember p : permSet) {
                    permString += p.parsedName + ", ";
                }
                int end = permString.lastIndexOf(',');
                permString = permString.substring(0, end);
                sendMessage(player, YELLOW + CommandUtils.getNameForUUID(member, player.getEntityWorld().getMinecraftServer()) + BLUE + ": " + GREEN + permString);
            }
        }
        if(group.getClaims().size() == 0 || group.getClaims().isEmpty()) {
            sendMessage(player, YELLOW + "No claims.");
        } else {
            sendMessage(player, YELLOW + "" + BOLD + "Claims:");
            for(ClaimArea claim : group.getClaims()) {
                sendMessage(player, BLUE + " + " + YELLOW + claim.getDisplayedViewName() + BLUE + " of " + GREEN + CommandUtils.getNameForUUID(claim.getOwner(), world.getMinecraftServer()));
            }
        }
    }

}