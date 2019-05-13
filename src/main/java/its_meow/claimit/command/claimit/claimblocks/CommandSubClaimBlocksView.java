package its_meow.claimit.command.claimit.claimblocks;

import static net.minecraft.util.text.TextFormatting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.UserClaimBlocks;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimBlocksView extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return "View your claim block count." + (CommandUtils.isAdminNoded(sender, "claimit.claimblocks.view.others") ? " Admins can specify playernames." : "");
    }

    @Override
    public String getName() {
        return "view";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.claimblocks.view.others") ? "/claimit claimblocks view [playername]" : "/claimit claimblocks view";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean canUseName = CommandUtils.isAdminNoded(sender, "claimit.claimblocks.view.others");
        UUID uuid = null;
        String name = "";
        if(args.length == 0) {
            EntityPlayer player = ((EntityPlayer) sender);
            name = player.getName();
            uuid = player.getGameProfile().getId();
        } else if(args.length == 1 && canUseName) {
            uuid = CommandUtils.getUUIDForName(args[0], server);
            name = args[0];
        } else {
            throw new CommandException("Invalid argument count! Usage: " + this.getUsage(sender));
        }
        sendMessage(sender, AQUA + "Claim Blocks for " + YELLOW + name + AQUA + ":");
        sendMessage(sender, GREEN + "Allowed: " + BLUE + UserClaimBlocks.getClaimBlocksAllowed(uuid));
        sendMessage(sender, GREEN + "Used: " + BLUE + UserClaimBlocks.getClaimBlocksUsed(uuid));
        sendMessage(sender, GREEN + "Remaining: " + BLUE + UserClaimBlocks.getClaimBlocksRemaining(uuid));
    }

    @Override
    public String getPermissionString() {
        return "claimit.claimblocks.view";
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1 && CommandUtils.isAdminNoded(sender, "claimit.claimblocks.view.others")) { 
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getPossiblePlayers(null, server, sender, args));
        } else {
            return new ArrayList<String>();
        }
    }

}
