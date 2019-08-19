package its_meow.claimit.util.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.api.AdminManager;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public class CommandUtils {

    @Nullable
    public static ClaimArea getClaimWithName(String claimName, ICommandSender sender) {
        ClaimArea claim = null;
        ClaimManager mgr = ClaimManager.getManager();
        if(sender instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) sender);
            claim = mgr.getClaimByNameAndOwner(claimName, player.getUniqueID());
            ClaimArea trueC = mgr.getClaimByTrueName(claimName);
            if(claim == null && AdminManager.isAdmin(player)) {
                CommandCIBase.sendMessage(sender, AQUA, "Using true name.");
                claim = mgr.getClaimByTrueName(claimName);
            } else if(claim == null && trueC != null && trueC.isOwner(player)) {
                claim = trueC;
            }
        } else { // sender is console/commandblock
            if(sender.canUseCommand(2, "")) {
                CommandCIBase.sendMessage(sender, YELLOW, "You are console, using true name");
                claim = mgr.getClaimByTrueName(claimName);
            }
        }
        return claim;
    }

    @Nullable
    public static ClaimArea getClaimWithNameOrLocation(String claimName, ICommandSender sender) throws CommandException {
        ClaimArea claim = null;
        if(claimName != null && !claimName.equals("")) {
            claim = CommandUtils.getClaimWithName(claimName, sender);
        } else {
            // Get current location for claim
            if(sender instanceof EntityPlayer) {
                claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
            } else {
                throw new CommandException("Console must specify a true claim name!");
            }
        }
        return claim;
    }

    public static ClaimPermissionMember getPermissionMember(String permName, String usage) throws CommandException {
        String validPerms = ClaimPermissionRegistry.getValidPermissionListMember();
        ClaimPermissionMember permission = null;
        try {
            permission = ClaimPermissionRegistry.getPermissionMember(permName);
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + usage);
        }
        if(permission == null) {
            throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + usage);
        }
        return permission;
    }

    public static ClaimPermissionToggle getPermissionToggle(String permName, String usage) throws CommandException {
        String validPerms = ClaimPermissionRegistry.getValidPermissionListToggle();
        ClaimPermissionToggle permission = null;
        try {
            permission = ClaimPermissionRegistry.getPermissionToggle(permName);
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + usage);
        }
        if(permission == null) {
            throw new CommandException("Invalid permission. Valid Permissions: " + validPerms + "\nUsage: " + usage);
        }
        return permission;
    }

    @Nonnull
    public static SubClaimArea getSubClaimWithNamesOrLocation(int startIndex, String[] args, ICommandSender sender) throws CommandException {
        String claimName = null;
        if(args.length >= startIndex + 1) {
            claimName = args[startIndex];
        }
        ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);
        if(claimName != null && claim == null) {
            claim = ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
        }
        SubClaimArea subClaim = null;
        if(claimName == null && claim == null) {
            throw new CommandException("There is no claim here!");
        } else if(claim != null && claimName != null && args.length == startIndex + 1) {
            if(claim.getSubClaimWithName(claimName) != null) {
                subClaim = claim.getSubClaimWithName(claimName);
            } else {
                throw new CommandException("There is no subclaim in this claim with that name!");
            }
        } else if(claim == null && claimName != null) {
            throw new CommandException("No claim with this name!");
        }

        String subClaimName = null;
        if(args.length == startIndex + 2) {
            subClaimName = args[startIndex + 1];
            subClaim = claim.getSubClaimWithName(subClaimName);
            if(subClaim == null) {
                throw new CommandException("There is no subclaim with that name in that claim!");
            }
        }
        if(args.length <= startIndex && claim != null) {
            subClaim = claim.getSubClaimAtLocation(sender.getPosition());
        }
        if(subClaim == null || claim == null) {
            throw new CommandException("Could not get subclaim!");
        }
        return subClaim;
    }

    public static boolean canManagePerms(ICommandSender sender, ClaimArea claim) {
        if(sender instanceof EntityPlayer) {
            if(claim.canManage((EntityPlayer) sender)) {
                return true;
            }
        } else if(sender.canUseCommand(2, "claimit.claim.manage.others")) { // Console
            return true;
        }
        return false;
    }

    public static UUID getUUIDForName(String username, MinecraftServer server) throws PlayerNotFoundException {
        if(username == null) {
            return null;
        }
        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
        if(profile != null && profile.getName().equals(username)) { // Found the profile!
            return profile.getId();
        } else {
            throw new PlayerNotFoundException("Invalid player: " + username);
        }
    }

    public static Set<UUID> getUUIDsForArgument(Set<UUID> wildcard, String names, ICommandSender sender, MinecraftServer server) throws PlayerNotFoundException {
        Set<UUID> uuids = new HashSet<UUID>();
        if(names == null) {
            return uuids;
        }
        if(names.contains(",")) {
            for(String username : names.split(",")) {
                GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
                if(profile != null && profile.getName().equals(username)) { // Found the profile!
                    uuids.add(profile.getId());
                } else {
                    sender.sendMessage(new FTC(TextFormatting.RED, "No such player " + username));
                }
            }
        } else if(names.equals("*")) {
            return wildcard;
        } else {
            uuids.add(CommandUtils.getUUIDForName(names, server));
            return uuids;
        }
        return uuids;
    }
    
    public static Set<ClaimPermissionMember> getMemberPermissionsForArgument(Set<ClaimPermissionMember> wildcard, String usage, String arg, ICommandSender sender, MinecraftServer server) throws CommandException {
        Set<ClaimPermissionMember> set = new HashSet<ClaimPermissionMember>();
        if(arg == null) {
            return set;
        }
        if(arg.contains(",")) {
            for(String arg1 : arg.split(",")) {
                set.add(CommandUtils.getPermissionMember(arg1, "\n" + YELLOW + usage));
            }
        } else if(arg.equals("*")) {
            return wildcard;
        } else {
            set.add(CommandUtils.getPermissionMember(arg, "\n" + YELLOW + usage));
            return set;
        }
        return set;
    }

    /** Attempts to get name from UUID cache. Requires World to get server instance. 
     * @param uuid - The UUID to attempt to retrieve the name for
     * @param server - The server instance
     * @return The name for this UUID or the UUID as a String if none was found **/
    @Nonnull
    public static String getNameForUUID(UUID uuid, MinecraftServer server) {
        String name = null;
        GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(uuid);
        if(profile != null) {
            name = profile.getName();
        } else {
            name = uuid.toString();
        }
        return name;
    }

    public static boolean isAdmin(ICommandSender sender) {
        return isAdminNoded(sender, "");
    }

    public static boolean isAdminNoded(ICommandSender sender, String permNode) {
        return (((!(sender instanceof EntityPlayer) && sender.canUseCommand(2, permNode)) || ((sender instanceof EntityPlayer) && AdminManager.isAdmin((EntityPlayer) sender) && sender.canUseCommand(0, permNode))));
    }

    public static boolean isAdminNodedNeedsManage(ICommandSender sender, String permNode) {
        return (((!(sender instanceof EntityPlayer) && sender.canUseCommand(2, permNode) && sender.canUseCommand(2, "claimit.claim.manage.others")) || ((sender instanceof EntityPlayer) && AdminManager.isAdmin((EntityPlayer) sender) && sender.canUseCommand(0, "claimit.claim.manage.others") && sender.canUseCommand(0, permNode))));
    }

    public static boolean isAdminWithNodeOrOwner(ICommandSender sender, ClaimArea claim, String permNode) {
        if(sender instanceof EntityPlayer) {
            return claim.isOwner((EntityPlayer) sender) || CommandUtils.isAdminNodedNeedsManage(sender, permNode);
        }
        return CommandUtils.isAdminNodedNeedsManage(sender, permNode);
    }

    public static boolean isAdminWithNodeOrManage(ICommandSender sender, ClaimArea claim, String permNode) {
        if(sender instanceof EntityPlayer) {
            return claim.canManage((EntityPlayer) sender) || CommandUtils.isAdminNodedNeedsManage(sender, permNode);
        }
        return CommandUtils.isAdminNodedNeedsManage(sender, permNode);
    }

    public static List<String> getOwnedClaimNames(@Nullable List<String> list, ICommandSender sender) {
        if(list == null) {
            list = new ArrayList<String>();
        }
        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            Set<ClaimArea> owned = ClaimManager.getManager().getClaimsOwnedByPlayer(player.getGameProfile().getId());
            if(owned != null) {
                for(ClaimArea claim : owned) {
                    list.add(claim.getDisplayedViewName());
                }
            }
        }
        return list;
    }

    public static List<String> getSubclaimNames(@Nullable List<String> list, ICommandSender sender, ClaimArea parent) {
        if(list == null) {
            list = new ArrayList<String>();
        }
        if(sender instanceof EntityPlayer) {
            Set<SubClaimArea> owned = parent.getSubClaims();
            if(owned != null && owned.size() > 0) {
                for(SubClaimArea subclaim : owned) {
                    list.add(subclaim.getDisplayedViewName());
                }
            }
        }
        return list;
    }

    public static List<String> getRelevantGroupNames(ICommandSender sender) {
        if(sender instanceof EntityPlayer) {
            UUID uuid = ((EntityPlayer) sender).getGameProfile().getId();
            return GroupManager.getGroups().stream().filter(g -> (g.getMembers().keySet().contains(uuid) || g.isOwner(uuid))).collect(ArrayList<String>::new, (l, g) -> l.add(g.getName()), (l, l1) -> l1.addAll(l));
        }
        return new ArrayList<String>();
    }

    public static List<String> getPossiblePlayers(@Nullable List<String> list, MinecraftServer server, ICommandSender sender, String[] args) {
        if(list == null) {
            list = new ArrayList<String>();
        }
        list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()));
        return list;
    }

    public static List<String> getMemberPermissionsAndWildcard(@Nullable List<String> list) {
        list = getMemberPermissions(list);
        list.add("*");
        return list;
    }
    
    public static List<String> getMemberPermissions(@Nullable List<String> list) {
        if(list == null) {
            list = new ArrayList<String>();
        }
        for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) { 
            list.add(perm.parsedName);
        }
        return list;
    }

    public static List<String> getTogglePermissions(@Nullable List<String> list) {
        if(list == null) {
            list = new ArrayList<String>();
        }
        for(ClaimPermissionToggle perm : ClaimPermissionRegistry.getTogglePermissions()) { 
            list.add(perm.parsedName);
        }
        return list;
    }

    public static boolean checkDefaultNode(EntityPlayer player, int permLevel, String node) {
        return player.canUseCommand(permLevel, node) || !Loader.isModLoaded("sponge");
    }

    public static List<String> getSubclaimCompletions(@Nullable List<String> list, int startIndex, String[] args, ICommandSender sender) {
        if(list == null) {
            list = new ArrayList<String>();
        }
        if(args.length == startIndex + 1) {
            if(!ClaimManager.getManager().isBlockInAnyClaim(sender.getEntityWorld(), sender.getPosition())) {
                list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender)));
            } else {
                list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getSubclaimNames(null, sender, ClaimManager.getManager().getClaimAtLocation(sender.getEntityWorld(), sender.getPosition()))));
            }
        } else if(args.length == startIndex + 2) {
            ClaimArea claim = CommandUtils.getClaimWithName(args[startIndex], sender);
            if(claim != null) {
                list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getSubclaimNames(null, sender, claim)));
            }
        }
        return list;
    }

}
