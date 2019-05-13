package its_meow.claimit.api.util.objects;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public abstract class MemberContainer {
    
    protected BiMultiMap<ClaimPermissionMember, UUID> memberLists;
    protected UUID ownerUUID;
    
    public MemberContainer(UUID owner) {
        this.ownerUUID = owner;
        this.memberLists = new BiMultiMap<ClaimPermissionMember, UUID>();
    }
    
    /**
     * Give a permission to a member and add them to the member list if not present
     * @param uuid - The member's UUID
     * @param permission - The member permission to add
     * @return true if the member is not owner
     */
    public boolean addMember(UUID uuid, ClaimPermissionMember permission) {
        if(uuid.equals(ownerUUID)) {
            return false;
        }
        return this.memberLists.put(permission, uuid);
    }

    /**
     * Give a permission to a member and add them to the member list if not present
     * @param player - The member
     * @param permission - The member permission to add
     * @return true if the member is not owner
     */
    public boolean addMember(EntityPlayer player, ClaimPermissionMember permission) {
        return addMember(player.getGameProfile().getId(), permission);
    }
    
    /**
     * Removes a permission from a member and removes them from the list if they have no permissions left
     * @param uuid - The member's UUID
     * @param permission - The member permission to remove
     * @return true if the member has the permission or is not the owner
     */
    public boolean removeMember(UUID uuid, ClaimPermissionMember permission) {
        if(uuid.equals(ownerUUID)) {
            return false;
        }
        return this.memberLists.remove(permission, uuid);
    }
    
    /**
     * Removes a permission from a member and removes them from the list if they have no permissions left
     * @param player - The member\
     * @param permission - The member permission to remove
     * @return true if the member has the permission
     */
    public boolean removeMember(EntityPlayer player, ClaimPermissionMember permission) {
        return removeMember(player.getGameProfile().getId(), permission);
    }
    
    /**
     * Determines if a member has a permission.
     * @param uuid - The member's UUID
     * @param permission - The member permission to check
     * @return true if the member has this permission or member is owner
     */
    public abstract boolean hasPermission(UUID uuid, ClaimPermissionMember permission);
    
    /**
     * Determines if a member has a permission
     * @param player - The member
     * @param permission - The member permission to check
     * @return true if the member has this permission
     */
    public boolean hasPermission(EntityPlayer player, ClaimPermissionMember permission) {
        return hasPermission(player.getGameProfile().getId(), permission);
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
        return this.ownerUUID.equals(uuid);
    }
    
    /**
     * @return The UUID of the owner
     */
    public UUID getOwner() {
        return this.ownerUUID;
    }
    
    /**
     * Adds a set of members to the list
     * @param memberLists - The list of members to add
     */
    protected void addMembers(SetMultimap<ClaimPermissionMember, UUID> memberLists) {
        for(ClaimPermissionMember key : memberLists.keySet()) {
            this.memberLists.putAll(key, memberLists.get(key));
        }
    }
    
    /**
     * Gets if the member is in the list for a permission - Does not account for Owner
     * @param permission - The permission to check
     * @param id - The UUID of the member
     * @return true if the list contains this member
     */
    public boolean inPermissionList(ClaimPermissionMember permission, UUID id) {
        Set<UUID> members = this.memberLists.getValues(permission);
        if(members != null && members.contains(id)) {
            return true;
        }
        return false;
    }
    
    /** Returns a list of member UUIDs along with a set of their permissions. Used for easier displaying of member data. **/
    public ImmutableSetMultimap<UUID,ClaimPermissionMember> getMembers() {
        return this.memberLists.getValuesToKeys();
    }
    
    public static SetMultimap<ClaimPermissionMember, UUID> readMembers(NBTTagCompound tag) {
        SetMultimap<ClaimPermissionMember, UUID> map = MultimapBuilder.hashKeys().hashSetValues().build();
        NBTTagCompound memberCompound = tag.getCompoundTag("MEMBERS");
        for(String permString : memberCompound.getKeySet()) {
            if(ClaimPermissionRegistry.getPermissionMember(permString) != null) {
                NBTTagList tagList = memberCompound.getTagList(permString, Constants.NBT.TAG_STRING);
                ClaimPermissionMember perm = ClaimPermissionRegistry.getPermissionMember(permString);
                for(int i = 0; i < tagList.tagCount(); i++) {
                    String uuidString = tagList.getStringTagAt(i);
                    UUID member = UUID.fromString(uuidString);
                    map.put(perm, member);
                }
            }
        }
        return map;
    }
    
    public NBTTagCompound writeMembers(NBTTagCompound tag) {
        NBTTagCompound memberCompound = new NBTTagCompound();
        for(ClaimPermissionMember perm : this.memberLists.getKeysToValues().keySet()) {
            NBTTagList members = new NBTTagList();
            for(UUID member : this.memberLists.getKeysToValues().get(perm)) {
                members.appendTag(new NBTTagString(member.toString()));
            }
            memberCompound.setTag(perm.parsedName, members);
        }
        tag.setTag("MEMBERS", memberCompound);
        return tag;
    }
    
}
