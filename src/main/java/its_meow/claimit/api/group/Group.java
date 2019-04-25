package its_meow.claimit.api.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.util.ClaimNBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

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
        return isOwner(player.getGameProfile().getId());
    }
    
    public boolean isOwner(UUID uuid) {
        return this.owner.equals(uuid);
    }
    
    protected void addMembers(ArrayList<UUID> members) {
        this.members.addAll(members);
    }
    
    protected void addMemberPerms(Map<ClaimPermissionMember, ArrayList<UUID>> memberLists) {
        this.memberLists = ClaimNBTUtil.mergeMembers(this.memberLists, memberLists);
    }
    
    protected void addClaims(ArrayList<ClaimArea> claims) {
        this.claims.addAll(claims);
    }
    
    public NBTTagCompound serialize() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setUniqueId("owner", this.owner);
        compound.setString("name", name);
        compound = ClaimNBTUtil.writeUUIDs(compound, "MEMBERSLIST", this.members);
        compound = ClaimNBTUtil.writeMembers(compound, this.memberLists);
        compound = ClaimNBTUtil.writeClaimNames(compound, this.claims);
        return compound;
    }
    
    public static Group deserialize(NBTTagCompound compound) {
        UUID owner = compound.getUniqueId("owner");
        String name = compound.getString("name");
        Group group = new Group(name, owner);
        group.addMembers(ClaimNBTUtil.readUUIDs(compound, "MEMBERSLIST"));
        group.addMemberPerms(ClaimNBTUtil.readMembers(compound));
        group.addClaims(ClaimNBTUtil.readClaimNames(compound));
        return group;
    }
}
