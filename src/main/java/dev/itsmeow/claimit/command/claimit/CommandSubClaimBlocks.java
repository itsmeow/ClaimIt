package dev.itsmeow.claimit.command.claimit;

import dev.itsmeow.claimit.command.CommandCITreeBase;
import dev.itsmeow.claimit.command.claimit.claimblocks.CommandSubClaimBlocksAddAllowed;
import dev.itsmeow.claimit.command.claimit.claimblocks.CommandSubClaimBlocksSetAllowed;
import dev.itsmeow.claimit.command.claimit.claimblocks.CommandSubClaimBlocksView;
import net.minecraft.command.ICommandSender;

public class CommandSubClaimBlocks extends CommandCITreeBase {
    
    public CommandSubClaimBlocks() {
        super(
            new CommandSubClaimBlocksView(),
            new CommandSubClaimBlocksSetAllowed(),
            new CommandSubClaimBlocksAddAllowed()
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
