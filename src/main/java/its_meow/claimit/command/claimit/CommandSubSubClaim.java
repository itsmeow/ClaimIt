package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.subclaim.CommandSubSubClaimDelete;
import net.minecraft.command.ICommandSender;

public class CommandSubSubClaim extends CommandCITreeBase {
    
    public CommandSubSubClaim() {
        super(
            new CommandSubSubClaimDelete()
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
