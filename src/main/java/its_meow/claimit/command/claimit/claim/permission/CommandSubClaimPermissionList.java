package its_meow.claimit.command.claimit.claim.permission;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.FTC;
import its_meow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimPermissionList extends CommandCIBase {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claim permission list [claimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Lists member permissions within a claim. Argument one is optional claim name. Defaults to location.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ClaimManager m = ClaimManager.getManager();
        if(args.length == 0) {
            if(sender instanceof EntityPlayer) {
                ClaimArea claim = m.getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
                if(claim != null) {
                    outputMembers(claim, sender);
                } else {
                    throw new CommandException("There is no claim here! Specify a name. Usage: " + this.getUsage(sender));
                }
            } else {
                throw new CommandException("Must specify name as non-player.");
            }
        } else if(args.length == 1) {
            ClaimArea claim = CommandUtils.getClaimWithName(args[0], sender);
            if(claim != null) {
                outputMembers(claim, sender);
            } else {
                throw new CommandException("There is no claim with this name" + (CommandUtils.isAdmin(sender) ? "!" : " that you own!"));
            }
        } else {
            throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
        }
    }

    private static void outputMembers(ClaimArea claim, ICommandSender sender) throws CommandException {
        ImmutableSetMultimap<UUID,ClaimPermissionMember> permMap = claim.getMembers();
        Set<Group> groups = GroupManager.getGroupsForClaim(claim);
        if(!CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.claim.permission.list.others")) {
            throw new CommandException("You cannot view the members of this claim!");
        }
        if((permMap == null || permMap.isEmpty())) {
            sendMessage(sender, RED, "This claim has no members.");
        } else {
            for(UUID uuid : permMap.keySet()) {
                sendMessage(sender, new FTC(YELLOW, CommandUtils.getNameForUUID(uuid, sender.getEntityWorld().getMinecraftServer())), new FTC(BLUE, " - "), new FTC(GREEN, getMemberLine(uuid, permMap.get(uuid))));
            }
        }
        if(groups != null && groups.size() > 0) {
            for(Group group : groups) {
                SetMultimap<UUID, ClaimPermissionMember> singletonMap = MultimapBuilder.hashKeys().hashSetValues().build();
                singletonMap.putAll(group.getOwner(), ClaimPermissionRegistry.getMemberPermissions());
                singletonMap.putAll(group.getMembers());
                ImmutableSetMultimap<UUID, ClaimPermissionMember> groupPermMap = ImmutableSetMultimap.copyOf(singletonMap);

                if(groupPermMap.size() > 0) {
                    sendMessage(sender, new FTC(YELLOW, Form.BOLD, "Members from: "), new FTC(GREEN, group.getName()));
                    groupPermMap.keySet().forEach((uuid) -> {
                        sendMessage(sender, new FTC(YELLOW, " -- " + CommandUtils.getNameForUUID(uuid, sender.getEntityWorld().getMinecraftServer())), new FTC(BLUE, " - "), new FTC(GREEN, getMemberLine(uuid, groupPermMap.get(uuid))));
                    });
                } else {
                    sendMessage(sender, Form.BOLD, "This is a bad, bad bug. Report this. A group is marked for this claim, but it does contain any members");
                }
            }
        } else {
            sendMessage(sender, RED, "This claim has no group members.");
        }
    }

    private static String getMemberLine(UUID member, Set<ClaimPermissionMember> permSet) {
        String permString = "";
        for(ClaimPermissionMember p : permSet) {
            permString += p.parsedName + BLUE + ", " + GREEN;
        }
        int end = permString.lastIndexOf(',');
        return permString.substring(0, end);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.claim.permission.list";
    }

}
