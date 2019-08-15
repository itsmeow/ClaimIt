package its_meow.claimit.api.claim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.claim.ClaimManager.ClaimAddResult;
import its_meow.claimit.api.event.claim.ClaimCheckPermissionEvent;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.api.permission.ClaimPermissions;
import its_meow.claimit.api.util.nbt.ClaimNBTUtil;
import its_meow.claimit.api.util.objects.ClaimChunkUtil;
import its_meow.claimit.api.util.objects.ClaimChunkUtil.ClaimChunk;
import its_meow.claimit.api.util.objects.MemberContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class ClaimArea extends MemberContainer {

    /** X position of the lowest (closest to -inf, -inf) corner **/
    protected int posX;
    /** Z position of the lowest (closest to -inf, -inf) corner **/
    protected int posZ;
    /** Dimension ID of the claim's location **/
    private final int dimID;
    /** Length of the side in the X direction extending from +1 of posX **/
    protected int sideLengthX;
    /** Length of the side in the Z direction extending from +1 of posZ **/
    protected int sideLengthZ;
    /** The name used to refer to this ClaimArea by the player 
     * defaults to {@link name}**/
    protected String viewName;
    protected Map<ClaimPermissionToggle, Boolean> toggles;
    public Set<SubClaimArea> subclaims;

    public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, EntityPlayer player) {
        this(dimID, posX, posZ, sideLengthX, sideLengthZ, player.getGameProfile().getId());
    }

    public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, UUID ownerUUID) {
        super(ownerUUID);
        this.dimID = dimID;
        this.posX = posX;
        this.posZ = posZ;
        this.sideLengthX = sideLengthX;
        this.sideLengthZ = sideLengthZ;
        this.ownerUUID = ownerUUID;
        this.toggles = new HashMap<ClaimPermissionToggle, Boolean>();
        this.subclaims = new HashSet<SubClaimArea>();
        for(ClaimPermissionToggle perm : ClaimPermissionRegistry.getTogglePermissions()) {
            this.toggles.putIfAbsent(perm, perm.defaultValue);
        }

        // Simplify main corner to the lowest x and y value
        if(this.sideLengthX < 0 || this.sideLengthZ < 0) {
            if(this.sideLengthX < 0) {
                this.posX += this.sideLengthX;
                this.sideLengthX = Math.abs(this.sideLengthX);
            }
            if(this.sideLengthZ < 0) {
                this.posZ += this.sideLengthZ;
                this.sideLengthZ = Math.abs(this.sideLengthZ);
            }
        }
        this.viewName = ownerUUID.toString() + "_" + Math.abs(posX) + Math.abs(posZ) + dimID + Math.round(Math.random() * 100);
    }

    public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, UUID ownerUUID, String trueViewName) {
        this(dimID, posX, posZ, sideLengthX, sideLengthZ, ownerUUID);
        this.viewName = trueViewName;
    }
    
    public ClaimAddResult addSubClaim(SubClaimArea subclaim) {
        if(this.subclaims.contains(subclaim)) return ClaimAddResult.ALREADY_EXISTS;
        if(subclaim.getArea() == this.getArea()) return ClaimAddResult.TOO_LARGE;
        if(subclaim.parent != this) throw new RuntimeException("Invalid parent for subclaim");
        for(BlockPos corner : subclaim.getFourCorners()) {
            if(!this.isBlockPosInClaim(corner)) {
                return ClaimAddResult.OUT_OF_BOUNDS;
            }
        }
        for(SubClaimArea subclaimI : this.subclaims) {
            for(int i = 0; i <= subclaim.getSideLengthX(); i++) {
                for(int j = 0; j <= subclaim.getSideLengthZ(); j++) {
                    BlockPos toCheck = new BlockPos(subclaim.getMainPosition().getX() + i, 0, subclaim.getMainPosition().getZ() + j);
                    if(subclaimI.isBlockPosInClaim(toCheck)) {
                        return ClaimAddResult.OVERLAP;
                    }
                }
            }
            
            for(int i = 0; i <= subclaimI.getSideLengthX(); i++) {
                for(int j = 0; j <= subclaimI.getSideLengthZ(); j++) {
                    BlockPos toCheck = new BlockPos(subclaimI.getMainPosition().getX() + i, 0, subclaimI.getMainPosition().getZ() + j);
                    if(subclaim.isBlockPosInClaim(toCheck)) {
                        return ClaimAddResult.OVERLAP;
                    }
                }
            }
        }
        subclaims.add(subclaim);
        return ClaimAddResult.ADDED;
    }
    
    public boolean removeSubClaim(SubClaimArea subclaim) {
        return subclaims.remove(subclaim);
    }
    
    @Nonnull
    public ClaimArea getMostSpecificClaim(BlockPos pos) {
        for(SubClaimArea subclaim : this.subclaims) {
            if(subclaim.isBlockPosInClaim(pos)) {
                return subclaim;
            }
        }
        return this;
    }
    
    @Nullable
    public SubClaimArea getSubClaimAtLocation(BlockPos pos) {
        for(SubClaimArea subclaim : this.subclaims) {
            if(subclaim.isBlockPosInClaim(pos)) {
                return subclaim;
            }
        }
        return null;
    }
    
    @Nullable
    public SubClaimArea getSubClaimWithName(String viewName) {
        for(SubClaimArea subclaim : this.subclaims) {
            if(subclaim.getDisplayedViewName().equals(viewName)) {
                return subclaim;
            }
        }
        return null;
    }

    public boolean isOwner(UUID owner) {
        return this.getOwner().equals(owner);
    }

    public boolean canModify(EntityPlayer player) {
        return hasPermission(player, ClaimPermissions.MODIFY);
    }

    public boolean canUse(EntityPlayer player) {
        return hasPermission(player, ClaimPermissions.USE);
    }

    public boolean canEntity(EntityPlayer player) {
        return hasPermission(player, ClaimPermissions.ENTITY);
    }

    public boolean canPVP(EntityPlayer player) {
        return hasPermission(player, ClaimPermissions.PVP);
    }

    public boolean canManage(EntityPlayer player) {
        return hasPermission(player, ClaimPermissions.MANAGE_PERMS);
    }

    @Override
    public boolean hasPermission(EntityPlayer player, ClaimPermissionMember permission) {
        if(player instanceof FakePlayer) {
            if(this.isPermissionToggled(ClaimPermissions.ALLOW_FAKE_PLAYER_BYPASS)) {
                return true;
            }
        }
        return hasPermission(player.getGameProfile().getId(), permission);
    }

    @Override
    public boolean hasPermission(UUID uuid, ClaimPermissionMember permission) {
        ClaimCheckPermissionEvent event = new ClaimCheckPermissionEvent(this, uuid, permission);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.getResult() == Result.ALLOW) {
            return true;
        } else if(event.getResult() == Result.DENY) {
            return false;
        } else {
            return (this.isOwner(uuid) || isMemberPermissionToggled(permission) || this.memberLists.getValues(permission).contains(uuid) || hasPermissionFromGroup(permission, uuid));
        }
    }

    private boolean hasPermissionFromGroup(ClaimPermissionMember permission, UUID uuid) {
        for(Group group : GroupManager.getGroups()) {
            if(permission != ClaimPermissions.MANAGE_PERMS && group.hasPermissionInClaim(uuid, permission, this)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMemberPermissionToggled(ClaimPermissionMember permission) {
        ClaimPermissionToggle toggle = ClaimPermissionRegistry.getToggleFor(permission);
        return toggle != null && isPermissionToggled(toggle);
    }

    /** Tells whether a permission is enabled in a claim or not 
     *  @param perm - The permission to check
     *  @return The toggle status (on = true) **/
    public boolean isPermissionToggled(ClaimPermissionToggle perm) {
        if(perm.getForceEnabled()) {
            return perm.getForceValue();
        }
        if(!toggles.containsKey(perm)) {
            return perm.defaultValue;
        }
        return this.toggles.get(perm);
    }

    /** Switches the toggle on a permission. (true to false, false to true)
     * @param perm - Permission to flip
     * **/
    public void flipPermissionToggle(ClaimPermissionToggle perm) {
        this.toggles.put(perm, !this.toggles.get(perm));
    }

    /** Sets a permission toggle.
     * @param perm - Permission to set
     * @param value - What to set the permission to
     * **/
    public void setPermissionToggle(ClaimPermissionToggle perm, boolean value) {
        this.toggles.put(perm, value);
    }

    public ImmutableMap<ClaimPermissionToggle, Boolean> getToggles() {
        return ImmutableMap.copyOf(this.toggles);
    }

    /** Do NOT use this for permission checking. Only for use in removing members. 
     * Why: doesn't account for admins or the owner of the claim. It purely returns if a member is in the list.**/
    public boolean inPermissionList(ClaimPermissionMember permission, UUID id) {
        Set<UUID> members = this.memberLists.getValues(permission);
        if(members != null && members.contains(id)) {
            return true;
        }
        return false;
    }

    protected void setToggles(Map<ClaimPermissionToggle, Boolean> toggles) {
        toggles.forEach((p, b) -> this.toggles.put(p, b));
    }

    /** (Please account for world differences if using this!)
     * @param blockPos - The BlockPos to check for
     * @return True if the given BlockPos is within the claim's bounds. **/
    public boolean isBlockPosInClaim(BlockPos blockPos) {
        boolean isInXRange = (blockPos.getX() < this.getHXZPosition().getX() + 1) && (blockPos.getX() > this.posX - 1);
        boolean isInZRange = (blockPos.getZ() < this.getHXZPosition().getZ() + 1) && (blockPos.getZ() > this.posZ - 1);

        return isInXRange && isInZRange;
    }

    /** @return posX and posZ as BlockPos **/
    public BlockPos getMainPosition() {
        return new BlockPos(posX, 0, posZ);
    }

    /** @return the highest X and Z corner **/
    public BlockPos getHXZPosition() {
        return new BlockPos(posX + sideLengthX, 0, posZ + sideLengthZ);
    }

    /** @return the highest X and lowest Z corner **/
    public BlockPos getHXLZPosition() {
        return new BlockPos(posX + sideLengthX, 0, posZ);
    }

    /** @return the lowest X and the highest Z corner **/
    public BlockPos getLXHZPosition() {
        return new BlockPos(posX, 0, posZ + sideLengthZ);
    }

    /** @return The lowest X and Z corner at [0] and the high X and Z corner at [1] **/
    public BlockPos[] getTwoMainClaimCorners() {
        BlockPos[] corners = new BlockPos[2];
        corners[0] = this.getMainPosition();
        corners[1] = this.getHXZPosition();
        return corners;
    }

    /** @return All four corners **/
    public BlockPos[] getFourCorners() {
        BlockPos[] corners = new BlockPos[4];
        corners[0] = this.getMainPosition();
        corners[1] = this.getHXLZPosition();
        corners[2] = this.getLXHZPosition();
        corners[3] = this.getHXZPosition();
        return corners;
    }

    /** @return The length of the claim in the X direction. Keep in mind this does NOT include the initial block so the actual side length is 1 larger. **/
    public int getSideLengthX() {
        return sideLengthX;
    }

    /** @return The length of the claim in the Z direction. Keep in mind this does NOT include the initial block so the actual side length is 1 larger. **/
    public int getSideLengthZ() {
        return sideLengthZ;
    }

    /** @return The dimension ID of this claim (e.g., 0 for overworld) **/
    public int getDimensionID() {
        return dimID;
    }

    /** @return The server's world instance of this claim (retrieved from {@link DimensionManager}.getWorld(int) **/
    public World getWorld() {
        return DimensionManager.getWorld(this.dimID);
    }

    /** @return The XZ area of this claim - (XLength + 1) * (ZLength + 1) **/
    public int getArea() {
        return (sideLengthX + 1) * (sideLengthZ + 1);
    }

    // Versions Store:
    // 0: {0, dimID, posX, posZ, sideLengthX, sideLengthZ}

    /** @return The claim data as an array for serialization.
     * [0]: The version of this data. This is so if there is an update to the serialization format, you can determine what the version is.
     * [1]: The dimension ID of this claim 
     * [2]: The X position of this claim's lowest corner 
     * [3]: The Z position of this claim's lowest corner
     * [4]: The side length of this claim in the X direction (excluding base block) 
     * [5]: The side length of this claim in the Y direction (excluding base block)
     * **/
    public int[] getSelfAsInt() {
        // 0 is version for updates to serialization, interpret version if changes are made to serialization structure
        int[] s = {0, dimID, posX, posZ, sideLengthX, sideLengthZ};
        return s;
    }

    /** @return The view name of this claim. Starts with the UUID of the owner, an underscore, and then whatever name was set/generated **/
    public String getTrueViewName() {
        return viewName;
    }

    /** @return The view name of this claim that is displayed to the regular user. Can be set by user. **/
    public String getDisplayedViewName() {
        if(!viewName.contains("_")) { // The data was edited! Making a new name...
            this.viewName = ownerUUID.toString() + "_" + Math.abs(posX) + Math.abs(posZ) + dimID + Math.round(Math.random() * 100);
        }
        return viewName.substring(viewName.indexOf('_') + 1);
    }

    /** Sets the displayed name for this ClaimArea. 
     * @param nameIn - Name to set to
     * @return True if no other claims by the owner have this as their name **/
    public boolean setViewName(String nameIn) {
        boolean pass = true;
        for(ClaimArea claim : ClaimManager.getManager().getClaimsOwnedByPlayer(this.getOwner())) {
            if(claim.getTrueViewName().equals(this.ownerUUID + "_" + nameIn) && claim != this) { // Claim has the same name, is not this claim, and is owned by the same player
                pass = false;
            }
        }
        if(pass) {
            this.viewName = this.ownerUUID + "_" + nameIn;
        }
        return pass;
    }

    /**
     * @return A set of chunks that this claim overlaps or intersects.
     */
    public Set<ClaimChunk> getOverlappingChunks() {
        Set<ClaimChunk> chunks = new HashSet<ClaimChunk>();
        ClaimChunk hChunk = ClaimChunkUtil.getChunk(this.getHXZPosition());
        ClaimChunk lChunk = ClaimChunkUtil.getChunk(this.getMainPosition());
        for(int x = lChunk.x; x <= hChunk.x; x++) {
            for(int z = lChunk.z; z <= hChunk.z; z++) {
                chunks.add(new ClaimChunk(x, z));
            }
        }
        return chunks;
    }

    public NBTTagCompound serialize() {
        int[] claimVals = this.getSelfAsInt();
        UUID owner = this.getOwner();
        NBTTagCompound data = new NBTTagCompound();
        data.setIntArray("CLAIMINFO", claimVals);
        data.setString("OWNERUID", owner.toString());
        data.setString("TRUEVIEWNAME", this.getTrueViewName());

        data = super.writeMembers(data);
        data = ClaimNBTUtil.writeToggles(data, this.getToggles());
        data = this.serializeSubClaims(data);
        return data;
    }

    public static ClaimArea deserialize(NBTTagCompound tag, String keyName) {
        int[] claimVals = tag.getIntArray("CLAIMINFO");
        UUID owner = UUID.fromString(tag.getString("OWNERUID"));
        String trueViewName = tag.getString("TRUEVIEWNAME");
        if(trueViewName == null || trueViewName.equals("")) {
            trueViewName = keyName;
        }
        if(claimVals.length > 0 && claimVals[0] == 0) {
            ClaimItAPI.logger.debug("Valid version.");
            ClaimArea claim = new ClaimArea(claimVals[1], claimVals[2], claimVals[3], claimVals[4], claimVals[5], owner, trueViewName);
            claim.addMembers(MemberContainer.readMembers(tag));
            claim.setToggles(ClaimNBTUtil.readToggles(tag));
            if(tag.hasKey("SUBCLAIMS")) {
                tag.getTagList("SUBCLAIMS", Constants.NBT.TAG_COMPOUND).forEach(base -> {
                    claim.addSubClaim(SubClaimArea.deserialize(claim, ((NBTTagCompound) base)));
                });
            }
            return claim;
        } else {
            ClaimItAPI.logger.log(Level.FATAL, "Detected version that doesn't exist yet! Mod was downgraded? Claim cannot be loaded.");
            throw new RuntimeException("Canceled loading to prevent loss of claim data. If you recently downgraded versions, please upgrade or contact author.");
        }
    }
    
    protected NBTTagCompound serializeSubClaims(NBTTagCompound data) {
        NBTTagList subclaimData = new NBTTagList();
        for(SubClaimArea subclaim : this.subclaims) {
            subclaimData.appendTag(subclaim.serialize());
        }
        data.setTag("SUBCLAIMS", subclaimData);
        return data;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.dimID, this.posX, this.posZ, this.ownerUUID, this.sideLengthX, this.sideLengthZ);
    }
}
