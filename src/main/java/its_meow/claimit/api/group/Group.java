package its_meow.claimit.api.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import net.minecraft.entity.player.EntityPlayer;

public class Group {

    protected String name;
    protected Map<ClaimPermissionMember, ArrayList<UUID>> memberLists;
    protected ArrayList<UUID> members = new ArrayList<UUID>();
    protected ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
    protected UUID owner;

    public Group(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.memberLists = new HashMap<ClaimPermissionMember, ArrayList<UUID>>();
        for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
            if(!memberLists.containsKey(perm)) {
                this.memberLists.put(perm, new ArrayList<UUID>());
            }
        }
    }

    public boolean addMember(UUID uuid) {
        return members.add(uuid);
    }

    public boolean addMember(EntityPlayer player) {
        return addMember(player.getGameProfile().getId());
    }
    
    public void removeMember(EntityPlayer player) {
        removeMember(player.getGameProfile().getId());
    }
    
    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
        memberLists.keySet().forEach(p -> memberLists.get(p).remove(uuid));
        ArrayList<ClaimArea> toRemove = new ArrayList<ClaimArea>();
        claims.forEach(c -> {if(c.isTrueOwner(uuid)) toRemove.add(c);});
        toRemove.forEach(c -> claims.remove(c));
    }

    public boolean addMemberPermission(UUID uuid, ClaimPermissionMember permission) {
        return this.memberLists.get(permission).add(uuid);
    }

    public boolean addMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return addMemberPermission(player.getGameProfile().getId(), permission);
    }

    public boolean removeMemberPermission(UUID uuid, ClaimPermissionMember permission) {
        return this.memberLists.get(permission).remove(uuid);
    }

    public boolean removeMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return removeMemberPermission(player.getGameProfile().getId(), permission);
    }
    
    public boolean hasMemberPermission(UUID uuid, ClaimPermissionMember permission) {
        return this.memberLists.get(permission).contains(uuid);
    }
    
    public boolean hasMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return hasMemberPermission(player.getGameProfile().getId(), permission);
    }

    public boolean addClaim(ClaimArea claim) {
        return claims.add(claim);
    }
    
    public boolean removeClaim(ClaimArea claim) {
        return claims.remove(claim);
    }
    
    public boolean hasClaim(ClaimArea claim) {
        return claims.contains(claim);
    }
    
    public boolean hasPermissionInClaim(EntityPlayer player, ClaimPermissionMember permission, ClaimArea claim) {
        return this.hasClaim(claim) && this.hasMemberPermission(player, permission);
    }
    
    public ImmutableList<ClaimArea> getClaims() {
        return ImmutableList.copyOf(claims);
    }

    public String getName() {
        return name;
    }

    public boolean isOwner(EntityPlayer player) {
        return false;
    }
    
    public boolean isOwner(UUID uuid) {
        return false;
    }
}
