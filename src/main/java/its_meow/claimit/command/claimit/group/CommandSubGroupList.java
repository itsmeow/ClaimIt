package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.CommandChatStyle;
import its_meow.claimit.util.text.TextComponentStyled;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubGroupList extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.command.group.list.others") ? "Lists all groups on the server. Optional username argument." : "Lists all groups you are in. Takes no arguments.";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group list" + (CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.list.others") ? " [username]" : "");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean admin = CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.list.others");
        if(args.length > 0 && !admin) {
            throw new CommandException("Invalid argument count! Usage: " + this.getUsage(sender));
        }
        String name = null;
        if(args.length == 1 && admin) {
            name = args[0];
        }

        if(!admin && name == null) {
            name = sender.getName();
        }

        UUID uuid = CommandUtils.getUUIDForName(name, server);
        Group[] groups = (Group[]) GroupManager.getGroups().stream().filter(g -> (admin && uuid == null) || (uuid != null && (g.getMembers().keySet().contains(uuid) || g.isOwner(uuid)))).toArray(Group[]::new);
        if(groups.length > 0) {
            sendMessage(sender, DARK_BLUE + "" + BOLD + "Group List:");
            for(Group group : groups) {
                sender.sendMessage(new TextComponentStyled(BLUE + "Group: " + DARK_GREEN + group.getName(), new CommandChatStyle("/ci group info " + group.getName(), true, "Click for info")));
            }
        } else {
            sendMessage(sender, RED + "No groups found!");
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(CommandUtils.isAdmin(sender)) {
            return CommandUtils.getPossiblePlayers(null, server, sender, args);
        }
        return new ArrayList<String>();
    }

    @Override
    public String getPermissionString() {
        return "claimit.group.list";
    }

}
