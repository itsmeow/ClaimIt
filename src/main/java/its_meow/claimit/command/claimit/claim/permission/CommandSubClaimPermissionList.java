package its_meow.claimit.command.claimit.claim.permission;

import static net.minecraft.util.text.TextFormatting.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
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
        Map<UUID, HashSet<ClaimPermissionMember>> permMap = claim.getMembers();
        Set<Group> groups = GroupManager.getGroupsForClaim(claim);
        if(sender instanceof EntityPlayer) {
            if(!claim.canManage((EntityPlayer) sender)) {
                throw new CommandException("You cannot view the members of this claim!");
            }
        }
        if((permMap == null || permMap.isEmpty())) {
            sendMessage(sender, RED + "This claim has no members.");
        } else {
            for(UUID uuid : permMap.keySet()) {
                sendMessage(sender, YELLOW + CommandUtils.getNameForUUID(uuid, sender.getEntityWorld().getMinecraftServer()) + BLUE + " - " + GREEN + getMemberLine(uuid, permMap.get(uuid)));
            }
        }
        if(groups.size() > 0) {
            for(Group group : groups) {
                Map<UUID, Set<ClaimPermissionMember>> groupPermMap = group.getMembers();
                groupPermMap.put(group.getOwner(), ClaimPermissionRegistry.getMemberPermissions());
                if(groupPermMap.size() > 0) {
                    sendMessage(sender, YELLOW + "" + BOLD + "Members from: " + GREEN + group.getName());
                    groupPermMap.forEach((uuid, permSet) -> {
                        sendMessage(sender, YELLOW + " -- " + CommandUtils.getNameForUUID(uuid, sender.getEntityWorld().getMinecraftServer()) + BLUE + " - " + GREEN + getMemberLine(uuid, permSet));
                    });
                } else {
                    sendMessage(sender, "This is a bad, bad bug. Report this. A group is marked for this claim, but it does contain any members");
                }
            }
        } else {
            sendMessage(sender, RED + "This claim has no group members.");
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

}
