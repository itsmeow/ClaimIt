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
import its_meow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimBlocksAddAllowed extends CommandCIBaseAdminOnly {

    @Override
    public String getName() {
        return "addallowed";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claimblocks addallowed <player> <amount>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 2) {
            throw new CommandException("Invalid argument count! Usage: " + this.getUsage(sender));
        }
        UUID uuid = CommandUtils.getUUIDForName(args[0], server);
        int oldAllowed = UserClaimBlocks.getClaimBlocksAllowed(uuid);
        int amount = CommandBase.parseInt(args[1]);
        if(UserClaimBlocks.getClaimBlocksRemaining(uuid) + amount < 0) {
            throw new CommandException("Adding this amount would make the remaining amount less than 0!");
        }
        if(uuid != null) { 
            UserClaimBlocks.setAllowedClaimBlocks(uuid, oldAllowed + amount);
            sendMessage(sender, new FTC(GREEN, "Added " + amount + " to "), new FTC(YELLOW, args[0]), new FTC(GREEN, "'s allowed claim blocks. They are now allowed " + (oldAllowed + amount) + " claim blocks. They now have "), new FTC(AQUA, UserClaimBlocks.getClaimBlocksRemaining(uuid) + ""), new FTC(GREEN, " blocks remaining."));
        } else {
            throw new PlayerNotFoundException("Invalid player: " + args[0]);
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.claimblocks.addallowed";
    }

    @Override
    public String getAdminHelp(ICommandSender sender) {
        return "Adds to allowed claim blocks for a user. First argument name, second amount of blocks to add.";
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
