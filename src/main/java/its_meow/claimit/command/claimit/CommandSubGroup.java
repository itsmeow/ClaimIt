package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.group.CommandSubGroupClaim;
import its_meow.claimit.command.claimit.group.CommandSubGroupCreate;
import its_meow.claimit.command.claimit.group.CommandSubGroupDelete;
import its_meow.claimit.command.claimit.group.CommandSubGroupInfo;
import its_meow.claimit.command.claimit.group.CommandSubGroupPermission;
import its_meow.claimit.command.claimit.group.CommandSubGroupSetName;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubGroup extends CommandCITreeBase {

    public CommandSubGroup() {
        super(
                new CommandSubGroupCreate(),
                new CommandSubGroupSetName(),
                new CommandSubGroupDelete(),
                new CommandSubGroupPermission(),
                new CommandSubGroupClaim(),
                new CommandSubGroupInfo()
                );
    }

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group <subcommand>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

}
