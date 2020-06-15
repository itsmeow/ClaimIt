package dev.itsmeow.claimit.command.claimit;

import dev.itsmeow.claimit.command.CommandCITreeBase;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupClaim;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupCreate;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupDelete;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupInfo;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupList;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupPermission;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupSetName;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupSetPrimary;
import dev.itsmeow.claimit.command.claimit.group.CommandSubGroupSetTag;
import net.minecraft.command.ICommandSender;

public class CommandSubGroup extends CommandCITreeBase {

    public CommandSubGroup() {
        super(
            new CommandSubGroupCreate(),
            new CommandSubGroupSetName(),
            new CommandSubGroupDelete(),
            new CommandSubGroupPermission(),
            new CommandSubGroupClaim(),
            new CommandSubGroupInfo(),
            new CommandSubGroupList(),
            new CommandSubGroupSetPrimary(),
            new CommandSubGroupSetTag()
       );
    }
    
    @Override
    public String getHelp(ICommandSender sender) {
        return "Base group command for ClaimIt. Allows management of groups. Click on or run a subcommand to do stuff.";
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
    public String getPermissionString() {
        return "claimit.group";
    }

}
