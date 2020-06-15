package dev.itsmeow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.permission.ClaimPermissionMember;
import dev.itsmeow.claimit.api.permission.ClaimPermissions;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.command.CommandUtils;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class CommandSubTrust extends CommandCIBase {

    public static final ClaimPermissionMember[] trustPerms = new ClaimPermissionMember[] {ClaimPermissions.MODIFY, ClaimPermissions.USE, ClaimPermissions.ENTITY, ClaimPermissions.PVP};

    @Override
    public String getHelp(ICommandSender sender) {
        return "A shortcut command to add a player to a claim with modify, use, entity, and pvp all at once. Supports player lists with commas.";
    }

    @Override
    public String getName() {
        return "trust";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit trust <username> [claimname]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length < 1) {
            throw new CommandException("Must specify a player or list of players seperated by commas! Usage: " + this.getUsage(sender));
        }
        String username = args[0];
        String claimName = null;
        if(args.length == 2) {
            claimName = args[1];
        }
        ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);
        if(claimName == null && claim == null) {
            throw new CommandException("There is no claim here!");
        } else if(claim == null) {
            throw new CommandException("There is no claim with this name you own!");
        }
        if(!CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.command.claimit.claim.permission.others")) {
            throw new CommandException("You cannot modify members of this claim!");
        }
        Set<UUID> ids = CommandUtils.getUUIDsForArgument(new HashSet<UUID>(), username, sender, server);
        for(UUID id : ids) {
            if(!claim.isOwner(id)) {
                int addCount = 0;
                for(ClaimPermissionMember permission : trustPerms) {
                    if(claim.addMember(id, permission)) {
                        addCount++;
                    }
                }
                if(addCount > 0) {
                    sendMessage(sender, new FTC(GREEN, "Successfully added "), new FTC(YELLOW, username), new FTC(GREEN, " to claim "), new FTC(DARK_GREEN, claim.getDisplayedViewName()), new FTC(GREEN, " with permissions "), new FTC(AQUA, "modify, use, entity, pvp"));
                } else {
                    sendMessage(sender, TextFormatting.RED, "This player already has modify, use, entity, and pvp!");
                }
            }
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<String>();
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getPossiblePlayers(list, server, sender, args));
        } else if(args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(list, sender));
        }
        return list;
    }

    @Override
    public String getPermissionString() {
        return "claimit.trust";
    }

}
