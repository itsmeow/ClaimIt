package its_meow.claimit.command.claimit.claimblocks;

import java.util.UUID;

import its_meow.claimit.command.CommandCIBaseAdminOnly;
import its_meow.claimit.util.UserClaimBlocks;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import static net.minecraft.util.text.TextFormatting.*;

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
        if(uuid != null) { 
            UserClaimBlocks.setAllowedClaimBlocks(uuid, amount);
            sendMessage(sender, GREEN + "Set " + YELLOW + args[0] + GREEN + "'s allowed claim blocks to " + AQUA + amount + GREEN + ". They now have " + AQUA + UserClaimBlocks.getClaimBlocksRemaining(uuid) + GREEN + " blocks remaining.");
        } else {
            throw new PlayerNotFoundException("Invalid player: " + args[0]);
        }
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
