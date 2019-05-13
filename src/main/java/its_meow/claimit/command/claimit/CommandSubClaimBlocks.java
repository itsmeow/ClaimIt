package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.claimblocks.CommandSubClaimBlocksSetAllowed;
import its_meow.claimit.command.claimit.claimblocks.CommandSubClaimBlocksView;
import net.minecraft.command.ICommandSender;

public class CommandSubClaimBlocks extends CommandCITreeBase {
    
    public CommandSubClaimBlocks() {
        super(
            new CommandSubClaimBlocksView(),
            new CommandSubClaimBlocksSetAllowed()
        );
    }
    
    @Override
    public String getHelp(ICommandSender sender) {
        return "Base command for claim blocks.";
    }

    @Override
    public String getName() {
        return "claimblocks";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claimblocks <subcommand>";
    }

    @Override
    public String getPermissionString() {
        return "claimit.claimblocks";
    }
}
