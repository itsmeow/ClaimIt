package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.command.CommandCIBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            String groupname = args[0];
            if(sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                Group group = GroupManager.getGroup(groupname);
                if(group != null) {
                    if(group.isOwner(player)) {
                        GroupManager.removeGroup(group);
                        sendMessage(sender, AQUA + "Deleted group: " + GREEN + groupname);
                    } else {
                        sendMessage(sender, RED + "You do not own this group!");
                    }
                } else {
                    sendMessage(sender, RED + "No such group: " + groupname);
                }
            } else {
                sendMessage(sender, "You must be a player to use this command!");
            }
        } else {
            throw new SyntaxErrorException("Invalid syntax. Usage: " + this.getUsage(sender));
        }
    }

}