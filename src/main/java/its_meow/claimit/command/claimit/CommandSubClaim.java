package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.claim.CommandSubClaimDelete;
import its_meow.claimit.command.claimit.claim.CommandSubClaimDeleteAll;
import its_meow.claimit.command.claimit.claim.CommandSubClaimInfo;
import its_meow.claimit.command.claimit.claim.CommandSubClaimList;
import its_meow.claimit.command.claimit.claim.CommandSubClaimManage;
import its_meow.claimit.command.claimit.claim.CommandSubClaimPermission;
import its_meow.claimit.command.claimit.claim.CommandSubClaimSetName;
import its_meow.claimit.command.claimit.claim.CommandSubClaimToggle;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

    @Override
    public String getPermissionString() {
        return "claimit.claim";
    }
	
}
