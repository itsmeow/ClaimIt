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

public class CommandSubGroupSetName extends CommandCIBase {

    @Override
    public String getName() {
        return "setname";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group setname <groupname> <newname>";
    }
    
    @Override
    public String getHelp(ICommandSender sender) {
        return "Renames or sets the name of a group from its old name. Only group owner may rename. First argument is the group's current name. Second argument is the new name.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 2) {
            String groupname = args[0];
            String name = args[1];
            if(sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                Group group = GroupManager.getGroup(groupname);
                if(group != null) {
                    if(group.isOwner(player)) {
                        boolean pass = GroupManager.renameGroup(groupname, name);
                        if(pass) {
                            sendMessage(sender, AQUA + "Set this group's name to: " + GREEN + group.getName());
                        } else {
                            sendMessage(sender, RED + "Failed to set name. There is another group with this name.");
                        }
                    } else {
                        sendMessage(sender, RED + "You do not own this group!");
                    }
                } else {
                    sendMessage(sender, RED + "There is no group with this name!");
                }
            } else {
                sendMessage(sender, "You must be a player to use this command!");
            }
        } else {
            throw new SyntaxErrorException("Invalid syntax. Usage: " + this.getUsage(sender));
        }
    }

}
