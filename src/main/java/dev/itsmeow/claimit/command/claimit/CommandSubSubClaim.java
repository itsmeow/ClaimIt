package dev.itsmeow.claimit.command.claimit;

import dev.itsmeow.claimit.command.CommandCITreeBase;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimDelete;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimDeleteAll;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimInfo;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimList;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimPermission;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimSetName;
import dev.itsmeow.claimit.command.claimit.subclaim.CommandSubSubClaimToggle;
import net.minecraft.command.ICommandSender;

public class CommandSubSubClaim extends CommandCITreeBase {
    
    public CommandSubSubClaim() {
        super(
            new CommandSubSubClaimDelete(),
            new CommandSubSubClaimSetName(),
            new CommandSubSubClaimPermission(),
            new CommandSubSubClaimToggle(),
            new CommandSubSubClaimDeleteAll(),
            new CommandSubSubClaimList(),
            new CommandSubSubClaimInfo()
        );
    }
    
    @Override
    public String getHelp(ICommandSender sender) {
        return "Base subclaim management command for ClaimIt. Use subcommands to do stuff.";
    }
    
    @Override
    public String getName() {
        return "subclaim";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim <subcommand>";
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim";
    }
    
}
