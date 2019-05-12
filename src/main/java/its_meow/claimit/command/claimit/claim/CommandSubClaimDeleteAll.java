package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.RED;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.command.ConfirmationManager;
import its_meow.claimit.util.command.IConfirmable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimDeleteAll extends CommandCIBase implements IConfirmable {

	@Override
	public String getName() {
		return "deleteall";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return CommandUtils.isAdminNoded(sender, "claimit.claim.deleteall.others") ? "/claimit claim deleteall [username]" : "/claimit claim deleteall";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.claim.deleteall.others") ? "Deletes all claims YOU personally (yes, you, admin user) own, or if a username is specified, all of their claims." : "Deletes all claims you own. Must be confirmed via '/claimit confirm'. Can be canceled via '/claimit cancel'";
    }

    @Override
    protected String getPermissionString() {
        return "claimit.claim.deleteall";
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0 && sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            Set<ClaimArea> ownedClaims = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
            if(ownedClaims != null) {
                if(!ConfirmationManager.getManager().needsConfirm(sender)) {
                    ConfirmationManager.getManager().addConfirm(sender, this, new String[] {player.getName()});
                    sendMessage(sender, DARK_RED + "This will delete ALL of your claims. Are you sure you want to do this? Run " + AQUA + "'/claimit confirm'" + DARK_RED + " to confirm. If you do not want to do this, run " + AQUA + "'/claimit cancel'" + DARK_RED + ".");
                } else {
                    sendMessage(sender, RED + "Canceling preexisting action. Run this command again to delete all claims.");
                    ConfirmationManager.getManager().removeConfirm(sender);
                }
            } else {
                sendMessage(sender, RED + "You don't own any claims!");
            }
        } else if(args.length == 1 && CommandUtils.isAdminNoded(sender, "claimit.claim.deleteall.others")) {
            String name = args[0];
            UUID uuid = CommandUtils.getUUIDForName(name, server);
            if(uuid != null) {
                Set<ClaimArea> owned = ClaimManager.getManager().getClaimsOwnedByPlayer(uuid);
                if(owned == null) {
                    throw new CommandException("No claims were found.");
                }
                if(!ConfirmationManager.getManager().needsConfirm(sender)) {
                    ConfirmationManager.getManager().addConfirm(sender, this, new String[] {name});
                    sendMessage(sender, DARK_RED + "This will delete ALL of " + name + "'s claims. Are you sure you want to do this? Run " + AQUA + "'/claimit confirm'" + DARK_RED + " to confirm. If you do not want to do this, run " + AQUA + "'/claimit cancel'" + DARK_RED + ".");
                } else {
                    sendMessage(sender, RED + "Canceling preexisting action. Run this command again to delete all claims.");
                    ConfirmationManager.getManager().removeConfirm(sender);
                }
            } else {
                throw new PlayerNotFoundException("Player " + name + " does not exist!");
            }
        } else {
            throw new CommandException("Invalid argument count! Usage: " + this.getUsage(sender));
        }
	}

    @Override
    public String getConfirmName() {
        return "deleteall";
    }

    @Override
    public void doAction(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            String player = args[0];
            UUID uuid = CommandUtils.getUUIDForName(player, server);
            if(uuid != null) {
                if(sender instanceof EntityPlayer && !uuid.equals(((EntityPlayer) sender).getGameProfile().getId()) || !CommandUtils.isAdminNoded(sender, "claimit.claim.deleteall.others")) {
                    throw new CommandException("You do not have permission to remove " + player + "'s claims.");
                }
                ImmutableSet<ClaimArea> owned = ClaimManager.getManager().getClaimsOwnedByPlayer(uuid);
                if(owned == null) {
                    throw new CommandException("No claims were found.");
                }
                boolean failed = false;
                for(ClaimArea claim : owned) {
                    boolean s = ClaimManager.getManager().deleteClaim(claim);
                    if(!s) failed = true;
                }
                sendMessage(sender, "Removed " + player + "'s claims." + (failed ? " Something prevented some claims from being removed." : ""));
            } else {
                throw new RuntimeException("A deletion was confirmed, but the player supplied does not exist! What did you do !?!?");
            }
        } else {
            throw new RuntimeException("A deletion was confirmed, but no player was supplied! What did you do!?!?");
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(CommandUtils.isAdmin(sender)) {
            return CommandUtils.getPossiblePlayers(null, server, sender, args);
        }
        return new ArrayList<String>();
    }

}