package its_meow.claimit.command.claimit;

import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.util.UserClaimBlocks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import static net.minecraft.util.text.TextFormatting.*;

public class CommandSubClaimBlocks extends CommandCITreeBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return "Base command for claim blocks. Gives the amount of claim blocks you have.";
    }

    @Override
    public String getName() {
        return "claimblocks";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claimblocks";
    }

    @Override
    protected String getPermissionString() {
        return "claimit.claimblocks";
    }

    @Override
    protected void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            sendMessage(player, GREEN + "You have " + UserClaimBlocks.getClaimBlocksRemaining(player.getGameProfile().getId()) + " claim blocks remaining.");
        } else {
            throw new CommandException("You must be a player to use this command!");
        }
    }
}
