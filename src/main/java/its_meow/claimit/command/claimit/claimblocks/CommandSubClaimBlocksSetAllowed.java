package its_meow.claimit.command.claimit.claimblocks;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import its_meow.claimit.command.CommandCIBaseAdminOnly;
import its_meow.claimit.util.UserClaimBlocks;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
        int amount = CommandBase.parseInt(args[1], 0, Integer.MAX_VALUE);
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

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) { 
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getPossiblePlayers(null, server, sender, args));
        } else {
            return new ArrayList<String>();
        }
    }
}
