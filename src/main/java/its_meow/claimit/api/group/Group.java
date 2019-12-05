package its_meow.claimit.api.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.event.group.GroupClaimAddedEvent;
import its_meow.claimit.api.event.group.GroupClaimRemovedEvent;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.util.nbt.ClaimNBTUtil;
import its_meow.claimit.api.util.objects.MemberContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

public class Group extends MemberContainer {

    protected String name;
    protected String tag = null;

    protected Set<ClaimArea> claims = new HashSet<ClaimArea>();

    public Group(String name, UUID owner) {
        super(owner);
        this.name = name;
    }

    /**
     * Removes a member's owned claims from the claims list
     * @param uuid - The member's UUID
     */
    protected void removeMemberClaims(UUID uuid) {
        ArrayList<ClaimArea> toRemove = new ArrayList<ClaimArea>();
        claims.forEach(c -> {if(c.isOwner(uuid)) toRemove.add(c);});
        toRemove.forEach(c -> claims.remove(c));
    }

    @Override
    public boolean removeMember(UUID uuid, ClaimPermissionMember permission) {
        boolean result = super.removeMember(uuid, permission);
        if(!this.isOwner(uuid) && this.memberLists.getKeys(uuid).size() == 0) {
            this.removeMemberClaims(uuid);
        }
        return result;
    }

    /**
     * Adds a claim to the list
     * @param claim - The claim
     * @return true if the claim was added
     */
    public boolean addClaim(ClaimArea claim) {
        return !MinecraftForge.EVENT_BUS.post(new GroupClaimAddedEvent(this, claim)) && claims.add(claim);
    }

    /**
     * Removes a claim from the list
     * @param claim - The claim
     * @return true if the claim was present, and removed
     */
    public boolean removeClaim(ClaimArea claim) {
        return !MinecraftForge.EVENT_BUS.post(new GroupClaimRemovedEvent(this, claim)) && claims.remove(claim);
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
        return hasPermissionInClaim(player.getGameProfile().getId(), permission, claim);
    }

    /**
     * Determines if a user is granted permission in a claim via this group
     * @param uuid - The UUID of the player to test
     * @param permission - The permission to check
     * @param claim - The claim this permission will be used in
     * @return true if the claim list contains the claim and the member has the permission specified in the group
     */
    public boolean hasPermissionInClaim(UUID uuid, ClaimPermissionMember permission, ClaimArea claim) {
        return this.hasClaim(claim) && this.hasPermission(uuid, permission);
    }

    @Override
    public boolean hasPermission(UUID uuid, ClaimPermissionMember permission) {
        return this.memberLists.getKeys(uuid).contains(permission) || uuid.equals(this.ownerUUID);
    }

    /**
     * @return An Immutable List containing all the claims that this group has
     */
    public ImmutableList<ClaimArea> getClaims() {
        return ImmutableList.copyOf(claims);
    }

    /**
     * @return The name of this group, set by the user
     */
    public String getName() {
        return name;
    }

    protected void addClaims(ArrayList<ClaimArea> claims) {
        claims.forEach(claim -> this.addClaim(claim));
    }

    /**
     * Writes this group and its attributes to an NBT Compound
     * @return The claim represented in a compound
     */
    public NBTTagCompound serialize() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setUniqueId("owner", this.ownerUUID);
        compound.setString("name", name);
        compound = super.writeMembers(compound);
        compound = ClaimNBTUtil.writeClaimNames(compound, this.claims);
        if(tag != null) {
            compound.setString("tag", tag);
        }
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
        group.addMembers(MemberContainer.readMembers(compound));
        group.addClaims(ClaimNBTUtil.readClaimNames(compound));
        if(compound.hasKey("tag", Constants.NBT.TAG_STRING)) {
            group.setTag(compound.getString("tag"));
        }
        return group;
    }

    public void removeAllClaims() {
        this.claims.forEach(claim -> this.removeClaim(claim));
    }

    public void removeAllMembers() {
        this.memberLists.getKeysToValues().forEach((key, value) -> this.memberLists.remove(key, value));
    }

    /**
     * Set a group's tag
     * @param tag - The tag to set this group to
     */
    void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return This group's tag
     */
    @Nullable
    public String getTag() {
        return tag;
    }

}
