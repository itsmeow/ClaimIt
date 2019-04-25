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
    
    /**
     * Used internally to add a member to the list
     * @param uuid - UUID of member
     * @return true if the member is not the owner
     */
    protected boolean addMember(UUID uuid) {
        if(uuid.equals(owner)) {
            return false;
        }
        return members.add(uuid);
    }
    
    /**
     * Used internally to add a member to the list
     * @param player - The member
     * @return true if the member is not the owner
     */
    protected boolean addMember(EntityPlayer player) {
        return addMember(player.getGameProfile().getId());
    }
    
    /**
     * Removes a member internally and removes their owned claims from the claims list if they are not the owner
     * @param player - The member
     */
    protected void removeMember(EntityPlayer player) {
        removeMember(player.getGameProfile().getId());
    }
    
    /**
     * Removes a member internally and removes their owned claims from the claims list if they are not the owner
     * @param uuid - The member's UUID
     */
    protected void removeMember(UUID uuid) {
        if(uuid.equals(owner)) {
            return;
        }
        this.members.remove(uuid);
        memberLists.keySet().forEach(p -> memberLists.get(p).remove(uuid));
        ArrayList<ClaimArea> toRemove = new ArrayList<ClaimArea>();
        claims.forEach(c -> {if(c.isTrueOwner(uuid)) toRemove.add(c);});
        toRemove.forEach(c -> claims.remove(c));
    }
    
    /**
     * Give a permission to a member and add them to the member list if not present
     * @param uuid - The member's UUID
     * @param permission - The member permission to add
     * @return true if the member is not owner
     */
    public boolean addMemberPermission(UUID uuid, ClaimPermissionMember permission) {
        if(uuid.equals(owner)) {
            return false;
        }
        if(!this.members.contains(uuid)) {
            this.addMember(uuid);
        }
        return this.memberLists.get(permission).add(uuid);
    }

    /**
     * Give a permission to a member and add them to the member list if not present
     * @param player - The member
     * @param permission - The member permission to add
     * @return true if the member is not owner
     */
    public boolean addMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return addMemberPermission(player.getGameProfile().getId(), permission);
    }
    
    /**
     * Removes a permission from a member and removes them from the list if they have no permissions left
     * @param uuid - The member's UUID
     * @param permission - The member permission to remove
     * @return true if the member has the permission or is not the owner
     */
    public boolean removeMemberPermission(UUID uuid, ClaimPermissionMember permission) {
        if(uuid.equals(owner)) {
            return false;
        }
        boolean result = this.memberLists.get(permission).remove(uuid);
        boolean inLists = false;
        for(ArrayList<UUID> array : this.memberLists.values()) {
            if(array.contains(uuid)) {
                inLists = true;
            }
        }
        if(!inLists) {
            this.removeMember(uuid);
        }
        return result;
    }
    
    /**
     * Removes a permission from a member and removes them from the list if they have no permissions left
     * @param player - The member\
     * @param permission - The member permission to remove
     * @return true if the member has the permission
     */
    public boolean removeMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return removeMemberPermission(player.getGameProfile().getId(), permission);
    }
    
    /**
     * Determines if a member has a permission - Accounts for owner.
     * @param uuid - The member's UUID
     * @param permission - The member permission to check
     * @return true if the member has this permission or member is owner
     */
    public boolean hasMemberPermission(UUID uuid, ClaimPermissionMember permission) {
        return this.memberLists.get(permission).contains(uuid) || uuid.equals(this.owner);
    }
    
    /**
     * Determines if a member has a permission
     * @param player - The member
     * @param permission - The member permission to check
     * @return true if the member has this permission
     */
    public boolean hasMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return hasMemberPermission(player.getGameProfile().getId(), permission);
    }
    
    /**
     * Adds a claim to the list
     * @param claim - The claim
     * @return true
     */
    public boolean addClaim(ClaimArea claim) {
        return claims.add(claim);
    }
    
    /**
     * Removes a claim from the list
     * @param claim - The claim
     * @return true if the claim was present
     */
    public boolean removeClaim(ClaimArea claim) {
        return claims.remove(claim);
    }
    
    /**
     * Determines if a claim is present in the list
     * @param claim - The claim
     * @return true if the claim is present
     */
    public boolean hasClaim(ClaimArea claim) {
        return claims.contains(claim);
    }
    
    /**
     * Determines if a user is granted permission in a claim via this group
     * @param player - The player to test
     * @param permission - The permission to check
     * @param claim - The claim this permission will be used in
     * @return true if the claim list contains the claim and the member has the permission specified in the group
     */
    public boolean hasPermissionInClaim(EntityPlayer player, ClaimPermissionMember permission, ClaimArea claim) {
        return this.hasClaim(claim) && this.hasMemberPermission(player, permission);
    }
    
    /**
     * @return An Immutable List containing all the claims that this group has
     */
    public ImmutableList<ClaimArea> getClaims() {
        return ImmutableList.copyOf(claims);
    }
    
    /**
     * @return The name of this claim, set by the user
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return true if the player created this group
     */
    public boolean isOwner(EntityPlayer player) {
        return isOwner(player.getGameProfile().getId());
    }
    
    /**
     * @return true if the UUID created this group
     */
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
    
    /**
     * Writes this claim and its attributes to an NBT Compound
     * @return The claim represented in a compound
     */
    public NBTTagCompound serialize() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setUniqueId("owner", this.owner);
        compound.setString("name", name);
        compound = ClaimNBTUtil.writeUUIDs(compound, "MEMBERSLIST", this.members);
        compound = ClaimNBTUtil.writeMembers(compound, this.memberLists);
        compound = ClaimNBTUtil.writeClaimNames(compound, this.claims);
        return compound;
    }
    
    /**
     * Reads a compound and returns a group object with the saved members and claims
     * @param compound - The compound containing a claim object
     * @return A group matching the saved data on the tag compound
     */
    public static Group deserialize(NBTTagCompound compound) {
        UUID owner = compound.getUniqueId("owner");
        String name = compound.getString("name");
        Group group = new Group(name, owner);
        group.addMembers(ClaimNBTUtil.readUUIDs(compound, "MEMBERSLIST"));
        group.addMemberPerms(ClaimNBTUtil.readMembers(compound));
        group.addClaims(ClaimNBTUtil.readClaimNames(compound));
        return group;
    }
    
    /**
     * Gets if the member is in the list for a permission - Does not account for Owner of group
     * @param permission - The permission to check
     * @param id - The UUID of the member
     * @return true if the list contains this member
     */
    public boolean inPermissionList(ClaimPermissionMember permission, UUID id) {
        return this.memberLists.get(permission).contains(id);
    }
}
