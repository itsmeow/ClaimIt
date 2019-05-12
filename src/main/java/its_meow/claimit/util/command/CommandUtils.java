package its_meow.claimit.util.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import its_meow.claimit.AdminManager;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandUtils {

    @Nullable
    public static ClaimArea getClaimWithName(String claimName, ICommandSender sender) {
        ClaimArea claim = null;
        ClaimManager mgr = ClaimManager.getManager();
        if(sender instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) sender);
            claim = mgr.getClaimByNameAndOwner(claimName, player.getUniqueID());
            if(claim == null && AdminManager.isAdmin(player)) {
                sendMessage(sender, AQUA + "Using true name.");
                claim = mgr.getClaimByTrueName(claimName);
            }
        } else { // sender is console/commandblock
            if(sender.canUseCommand(2, "")) {
                sendMessage(sender, "You are console, using true name");
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
            throw new CommandException("Invalid permission." + GREEN +" Valid Permissions: " + YELLOW + validPerms + RED + "\nUsage: " + YELLOW + usage);
        }
        if(permission == null) {
            throw new CommandException("Invalid permission." + GREEN +" Valid Permissions: " + YELLOW + validPerms + RED + "\nUsage: " + YELLOW + usage);
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

    private static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
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

}
