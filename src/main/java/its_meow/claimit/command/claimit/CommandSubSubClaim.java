package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimDelete;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimDeleteAll;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimInfo;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimList;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimPermission;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimSetName;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimToggle;
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
