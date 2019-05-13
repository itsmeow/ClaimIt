package its_meow.claimit.command.claimit.claimblocks;

import java.util.UUID;

import its_meow.claimit.command.CommandCIBaseAdminOnly;
import its_meow.claimit.util.UserClaimBlocks;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubClaimBlocksSetAllowed extends CommandCIBaseAdminOnly {

    @Override
    public String getName() {
        return "setallowed";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claimblocks setallowed <player> <amount>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 2) {
            throw new CommandException("Invalid argument count! Usage: " + this.getUsage(sender));
        }
        UUID uuid = CommandUtils.getUUIDForName(args[0], server);
        int amount = CommandBase.parseInt(args[1], 4, Integer.MAX_VALUE);
        UserClaimBlocks.setAllowedClaimBlocks(uuid, amount);
    }

    @Override
    public String getPermissionString() {
        return "claimit.claimblocks.setallowed";
    }

    @Override
    public String getAdminHelp(ICommandSender sender) {
        return "Sets allowed claim blocks for a user. First argument name, second amount of blocks.";
    }

}
