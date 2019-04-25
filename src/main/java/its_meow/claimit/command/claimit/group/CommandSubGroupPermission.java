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
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubGroupPermission extends CommandBase {

    public CommandSubGroupPermission() {

    }

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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length == 3;
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

        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            Group group = GroupManager.getGroup(groupname);
            if(group != null) {
                if(group.isOwner(player)) {
                    if(action.equals("add"))  {
                        // Add user
                        if(!group.inPermissionList(permission, id) || group.isOwner(id)) {
                            if(!(sender instanceof EntityPlayer) && sender.canUseCommand(2, "") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer) sender))) {
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
                            if(!(sender instanceof EntityPlayer) && sender.canUseCommand(2, "") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer) sender))) {
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
        } else {
            sendMessage(sender, "You must be a player to use this command!");
        }
    }

    private static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

}