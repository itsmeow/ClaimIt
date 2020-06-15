package dev.itsmeow.claimit.command.claimit;

import dev.itsmeow.claimit.command.CommandCITreeBase;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimDelete;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimDeleteAll;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimInfo;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimList;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimManage;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimPermission;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimSetName;
import dev.itsmeow.claimit.command.claimit.claim.CommandSubClaimToggle;
import net.minecraft.command.ICommandSender;

public class CommandSubClaim extends CommandCITreeBase {
	
	public CommandSubClaim() {
	    super(
	        new CommandSubClaimInfo(),
		    new CommandSubClaimDelete(),
		    new CommandSubClaimList(),
		    new CommandSubClaimSetName(),
		    new CommandSubClaimPermission(),
		    new CommandSubClaimDeleteAll(),
		    new CommandSubClaimToggle(),
		    new CommandSubClaimManage()
		);
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "Base claim management command for ClaimIt. Use subcommands to do stuff.";
    }
	
	@Override
	public String getName() {
		return "claim";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit claim <subcommand>";
	}

    @Override
    public String getPermissionString() {
        return "claimit.claim";
    }
	
}
