package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubGroupSetName extends CommandBase {

    @Override
    public String getName() {
        return "setname";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group setname <groupname> <name>";
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

    private static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

}
