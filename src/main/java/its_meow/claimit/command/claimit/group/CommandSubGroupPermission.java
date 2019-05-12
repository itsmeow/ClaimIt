package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
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
        list.add("permission");
        list.add("perm");
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
        ClaimPermissionMember permission = CommandUtils.getPermissionMember(permissionStr, this.getUsage(sender));
        UUID id = CommandUtils.getUUIDForName(username, server);

        Group group = GroupManager.getGroup(groupname);
        if(group != null) {
            if(CommandUtils.isAdmin(sender) || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer)sender))) {
                if(action.equals("add"))  {
                    // Add user
                    if(!group.inPermissionList(permission, id) || group.isOwner(id)) {
                        if(CommandUtils.isAdminNoded(sender, "claimit.group.permission.others") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer) sender))) {
                            group.addMemberPermission(id, permission);
                            sendMessage(sender, GREEN + "Successfully added " + YELLOW + username + GREEN + " to group " + DARK_GREEN + groupname + GREEN + " with permission " + AQUA + permission.parsedName);
                        } else {
                            sendMessage(sender, RED + "You do not own this group!");
                        }
                    } else {
                        sendMessage(sender, YELLOW + "This player already has that permission!");
                    }
                } else if(action.equals("remove")) {
                    // Remove user
                    if(group.inPermissionList(permission, id)) {
                        if(CommandUtils.isAdminNoded(sender, "claimit.group.permission.others") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer) sender))) {
                            group.removeMemberPermission(id, permission);
                            sendMessage(sender, GREEN + "Successfully removed permission " + AQUA + permission.parsedName + GREEN + " from user " + YELLOW + username + GREEN + " in group " + DARK_GREEN + groupname);
                        } else {
                            sendMessage(sender, RED + "You do not own this group!");
                        }
                    } else {
                        sendMessage(sender, YELLOW + "This player does not have that permission!");
                    }
                } else {
                    throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
                }
            } else {
                sendMessage(sender, RED + "You do not own this group!");
            }
        } else {
            sendMessage(sender, RED + "No such group: " + groupname);
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<String>();
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove");
        } else if(args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getMemberPermissions(list));
        } else if(args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getPossiblePlayers(list, server, sender, args));
        } else if(args.length == 4) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getRelevantGroupNames(sender));
        }
        return list;
    }

    @Override
    protected String getPermissionString() {
        return "claimit.group.permission";
    }

}
