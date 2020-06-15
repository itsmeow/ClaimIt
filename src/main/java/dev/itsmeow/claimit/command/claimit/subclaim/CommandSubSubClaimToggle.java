package dev.itsmeow.claimit.command.claimit.subclaim;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.claim.SubClaimArea;
import dev.itsmeow.claimit.api.permission.ClaimPermissionRegistry;
import dev.itsmeow.claimit.api.permission.ClaimPermissionToggle;
import dev.itsmeow.claimit.api.permission.ClaimPermissions;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.CommandChatStyle;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubSubClaimToggle extends CommandCIBase {

	@Override
	public String getName() {
		return "toggle";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit subclaim toggle [toggle name] [claim name) (subclaim name]";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "Controls toggle permissions. Given no arguments, will display all toggles and values. Given only a toggle as an argument, it will switch it on or off. Optional second and third claim/subclaim name argument, defaults to location.";
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		SubClaimArea claim = CommandUtils.getSubClaimWithNamesOrLocation(1, args, sender);
		if(args.length == 0 || (args.length == 1 && args[0].equals("list"))) {
			if(CommandUtils.canManagePerms(sender, claim)) {
				for(ClaimPermissionToggle toggle : ClaimPermissionRegistry.getTogglePermissions()) {
					boolean toggled = claim.isPermissionToggled(toggle);
					String toggledStr = toggled ? "ON" : "OFF";
					toggledStr = toggle.getDefault() == toggled ? GREEN + toggledStr : RED + toggledStr;
					sendSMessage(sender, YELLOW + toggle.parsedName + BLUE + ": " + toggledStr, new CommandChatStyle("/claimit subclaim toggle " + toggle.parsedName, true, getHoverFor(toggle, claim)));
				}
				return;
			} else {
			    throw new CommandException("You cannot modify toggles of this subclaim!");
			}
		}
		if(args.length > 3 || args.length < 1) {
			throw new WrongUsageException(this.getUsage(sender) + "\n Invalid argument count!");
		}
		
		ClaimPermissionToggle perm = CommandUtils.getPermissionToggle(args[0], this.getUsage(sender));
		
		// Argh, if they don't have edit others and admin on, they aren't the owner, and they cannot manage
		if(!CommandUtils.isAdminNoded(sender, "claimit.command.claimit.subclaim.toggle.others") && sender instanceof EntityPlayer && !claim.isOwner((EntityPlayer) sender) && !claim.inPermissionList(ClaimPermissions.MANAGE_PERMS, ((EntityPlayer) sender).getGameProfile().getId())) {
		    throw new CommandException("You cannot modify toggles of this subclaim!");
		}
		
		if(CommandUtils.canManagePerms(sender, claim)) {
			if(perm.getForceEnabled()) {
				claim.setPermissionToggle(perm, perm.getForceValue());
				throw new CommandException("This toggle cannot be modified. It has been forced to a value by the server.");
			}
			claim.flipPermissionToggle(perm);
			sendSMessage(sender, BLUE + "Set " + YELLOW + perm.parsedName + BLUE + " to " + getStringFor(perm, claim.isPermissionToggled(perm)), new CommandChatStyle("/claimit subclaim toggle " + perm.parsedName, true, getHoverFor(perm, claim)));
		} else {
			sendMessage(sender, RED, "You cannot modify toggles of this subclaim!");
		}

	}
	
	private static String getStringFor(ClaimPermissionToggle perm, boolean toggled) {
        String toggledStr = toggled ? "ON" : "OFF";
        return perm.getDefault() == toggled ? GREEN + toggledStr : RED + toggledStr;
	}
	
	private static String getHoverFor(ClaimPermissionToggle perm, ClaimArea claim) {
	    return BLUE + "Click to toggle " + YELLOW + perm.parsedName + BLUE + " to " + getStringFor(perm, !claim.isPermissionToggled(perm)) + "\n" + BLUE + "Help Info: " + YELLOW + perm.helpInfo;
	}
	
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<String>();
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getTogglePermissions(list));
        } else if(args.length == 2 || args.length == 3) {
            return CommandUtils.getSubclaimCompletions(list, 1, args, sender);
        }
        return list;
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim.toggle";
    }

}
