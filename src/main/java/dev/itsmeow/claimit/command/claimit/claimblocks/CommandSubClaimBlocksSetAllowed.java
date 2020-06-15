package dev.itsmeow.claimit.command.claimit.claimblocks;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.itsmeow.claimit.command.CommandCIBaseAdminOnly;
import dev.itsmeow.claimit.util.UserClaimBlocks;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.FTC;
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
        if(uuid != null) {
            int amount = CommandBase.parseInt(args[1], UserClaimBlocks.getClaimBlocksUsed(uuid), Integer.MAX_VALUE);
            UserClaimBlocks.setAllowedClaimBlocks(uuid, amount);
            sendMessage(sender, new FTC(GREEN, "Set "), new FTC(YELLOW, args[0]), new FTC(GREEN, "'s allowed claim blocks to "), new FTC(AQUA, amount + ""), new FTC(GREEN, ". They now have "), new FTC(AQUA, UserClaimBlocks.getClaimBlocksRemaining(uuid) + ""), new FTC(GREEN, " blocks remaining."));
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
