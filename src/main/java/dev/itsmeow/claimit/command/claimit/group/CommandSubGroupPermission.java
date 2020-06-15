package dev.itsmeow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.itsmeow.claimit.api.group.Group;
import dev.itsmeow.claimit.api.group.GroupManager;
import dev.itsmeow.claimit.api.permission.ClaimPermissionMember;
import dev.itsmeow.claimit.api.permission.ClaimPermissionRegistry;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubGroupPermission extends CommandCIBase {

    @Override
    public String getName() {
        return "permission";
    }

    @Override
    public List<String> getAliases() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("perm");
        list.add("player");
        return list;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group permission <add/remove> <permission> <username> <groupname>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Adds or removes a permission from a member within the group, and by extension their permissions in the group's claims. First argument is add or remove. Second argument is a member permission. Third argument is username. Fourth argument is group name.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if(args.length != 4) {
            throw new WrongUsageException("Improper argument count! Usage: " + this.getUsage(sender));
        }
        String action = args[0].toLowerCase();
        String permissionStr = args[1];
        String username = args[2];
        String groupname = args[3];
        if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
        }
        Set<ClaimPermissionMember> permissions = CommandUtils.getMemberPermissionsForArgument(ClaimPermissionRegistry.getMemberPermissions(), this.getUsage(sender), permissionStr, sender, server);

        Group group = GroupManager.getGroup(groupname);
        if(group != null) {
            if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.permission.others") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer) sender))) {
                Set<UUID> wildcard = group.getMembers().keySet();
                wildcard = new HashSet<UUID>(wildcard);
                wildcard.remove(group.getOwner());
                Set<UUID> ids = CommandUtils.getUUIDsForArgument(wildcard, username, sender, server);
                if(ids.size() == 0) {
                    sendMessage(sender, RED, "No members to " + action.toLowerCase() + "!");
                }
                for(UUID id : ids) {
                    username = CommandUtils.getNameForUUID(id, server);
                    if(action.equals("add"))  {
                        // Add user
                        for(ClaimPermissionMember permission : permissions) {
                            if(!group.isOwner(id)) {
                                if(!group.inPermissionList(permission, id)) {
                                    group.addMember(id, permission);
                                    sendMessage(sender, new FTC(GREEN, "Successfully added "), new FTC(YELLOW, username), new FTC(GREEN, " to group "), new FTC(DARK_GREEN, groupname), new FTC(GREEN, " with permission "), new FTC(AQUA, permission.parsedName));
                                } else if(permissions.size() == 1) {
                                    sendMessage(sender, YELLOW, "This player already has that permission!");
                                }
                            } else {
                                sendMessage(sender, YELLOW, "Cannot add owner as member!");
                            }
                        }
                    } else if(action.equals("remove")) {
                        // Remove user
                        for(ClaimPermissionMember permission : permissions) {
                            if(!group.isOwner(id)) {
                                if(group.inPermissionList(permission, id)) {
                                    group.removeMember(id, permission);
                                    sendMessage(sender, new FTC(GREEN, "Successfully removed permission "), new FTC(AQUA, permission.parsedName), new FTC(GREEN, " from user "), new FTC(YELLOW, username), new FTC(GREEN, " in group "), new FTC(DARK_GREEN, groupname));
                                } else if(permissions.size() == 1) {
                                    sendMessage(sender, YELLOW, "This player does not have that permission!");
                                }
                            } else {
                                sendMessage(sender, YELLOW, "Cannot add owner as member!");
                            }
                        }
                    } else {
                        throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
                    }
                }
            } else {
                sendMessage(sender, RED, "You do not own this group!");
            }
        } else {
            sendMessage(sender, RED, "No such group: " + groupname);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<String>();
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove");
        } else if(args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getMemberPermissionsAndWildcard(list));
        } else if(args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getPossiblePlayers(list, server, sender, args));
        } else if(args.length == 4) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getRelevantGroupNames(sender));
        }
        return list;
    }

    @Override
    public String getPermissionString() {
        return "claimit.group.permission";
    }

}
