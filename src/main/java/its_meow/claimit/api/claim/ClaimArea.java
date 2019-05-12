package its_meow.claimit.api.claim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.api.permission.ClaimPermissions;
import its_meow.claimit.api.util.nbt.ClaimNBTUtil;
import its_meow.claimit.api.util.objects.BiMultiMap;
import its_meow.claimit.api.util.objects.ClaimChunkUtil;
import its_meow.claimit.api.util.objects.ClaimChunkUtil.ClaimChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ClaimArea {

	/** X position of the lowest (closest to -inf, -inf) corner **/
	private int posX;
	/** Z position of the lowest (closest to -inf, -inf) corner **/
	private int posZ;
	/** Dimension ID of the claim's location **/
	private final int dimID;
	/** Length of the side in the X direction extending from +1 of posX **/
	private int sideLengthX;
	/** Length of the side in the Z direction extending from +1 of posZ **/
	private int sideLengthZ;
	/** The UUID of the owner **/
	private UUID ownerUUID;
	/** The offline UUID of the owner**/
	private UUID ownerUUIDOffline;
	/** The name used to serialize the ClaimArea **/
	private String name;
	/** The name used to refer to this ClaimArea by the player 
	 * defaults to {@link name}**/
	private String viewName;
	/* Private field for storing member UUIDs */
	private BiMultiMap<ClaimPermissionMember, UUID> memberLists;
	private Map<ClaimPermissionToggle, Boolean> toggles;

	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, EntityPlayer player) {
		this(dimID, posX, posZ, sideLengthX, sideLengthZ, EntityPlayer.getUUID(player.getGameProfile()), EntityPlayer.getOfflineUUID(player.getName()));
	}

	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, UUID ownerUUID, UUID ownerUUIDOffline) {
		this.dimID = dimID;
		this.posX = posX;
		this.posZ = posZ;
		this.sideLengthX = sideLengthX;
		this.sideLengthZ = sideLengthZ;
		this.ownerUUID = ownerUUID;
		this.ownerUUIDOffline = ownerUUIDOffline;
		this.memberLists = new BiMultiMap<ClaimPermissionMember, UUID>();
		this.toggles = new HashMap<ClaimPermissionToggle, Boolean>();
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
		this.name = ownerUUID.toString() + dimID + posX + posZ + sideLengthX + sideLengthZ;
		this.viewName = ownerUUID.toString() + "_" + Math.abs(posX) + Math.abs(posZ) + dimID + Math.round(Math.random() * 100);
	}

	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, UUID ownerUUID, UUID ownerUUIDOffline, String trueViewName) {
		this(dimID, posX, posZ, sideLengthX, sideLengthZ, ownerUUID, ownerUUIDOffline);
		this.viewName = trueViewName;
	}

	public boolean isOwner(EntityPlayer player) {
		try {
			if(this.getOwner().equals(EntityPlayer.getUUID(player.getGameProfile()))) {
				// If online UUID does match then make sure offline does too
				if(this.getOwnerOffline().equals(EntityPlayer.getOfflineUUID(player.getName()))) {
					return true;
				}
			}
			if(ClaimManager.getManager().isAdmin(player) && player.canUseCommand(0, "claimit.claim.manage.others")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isTrueOwner(EntityPlayer player) {
		try {
			if(this.getOwner().equals(EntityPlayer.getUUID(player.getGameProfile()))) {
				// If online UUID does match then make sure offline does too
				if(this.getOwnerOffline().equals(EntityPlayer.getOfflineUUID(player.getName()))) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isTrueOwner(UUID owner) {
		try {
			if(this.getOwner().equals(owner)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean canModify(EntityPlayer player) {
		return hasPermission(ClaimPermissions.MODIFY, player);
	}

	public boolean canUse(EntityPlayer player) {
		return hasPermission(ClaimPermissions.USE, player);
	}

	public boolean canEntity(EntityPlayer player) {
		return hasPermission(ClaimPermissions.ENTITY, player);
	}

	public boolean canPVP(EntityPlayer player) {
		return hasPermission(ClaimPermissions.PVP, player);
	}
	
	public boolean canManage(EntityPlayer player) {
		return hasPermission(ClaimPermissions.MANAGE_PERMS, player);
	}


	public boolean hasPermission(ClaimPermissionMember permission, EntityPlayer player) {		
		return this.isOwner(player) || isMemberPermissionToggled(permission) || this.memberLists.getValues(permission).contains(player.getGameProfile().getId()) || hasPermissionFromGroup(permission, player);
	}
	
	private boolean hasPermissionFromGroup(ClaimPermissionMember permission, EntityPlayer player) {
	    for(Group group : GroupManager.getGroups()) {
            if(permission != ClaimPermissions.MANAGE_PERMS && group.hasPermissionInClaim(player, permission, this)) {
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

	/** Adds a member to the member list with a given permission and player object 
	 * This runs {@link ClaimArea#addMember(ClaimPermissionMember, UUID)} after converting the player to UUID
	 * @param permission - The permission which will be used
	 * @param player - The player that will be added
	 * @return Whether the adding was successful or not (if the player is already in the list) **/
	public boolean addMember(ClaimPermissionMember permission, EntityPlayer player) {
		UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
		return this.addMember(permission, uuid);
	}

	/** Adds a member to the member list with a given permission and UUID
	 * @param permission - The permission which will be used
	 * @param uuid - The player UUID that will be added
	 * @return Whether the adding was successful or not (if the player is already in the list)**/
	public boolean addMember(ClaimPermissionMember permission, UUID uuid) {
		return this.memberLists.put(permission, uuid);
	}

	/** Removes a member from the member list with a given permission and player object
	 * This runs {@link ClaimArea#removeMember(ClaimPermissionMember, UUID)} after converting the player to UUID
	 * @param permission - The permission which will be removed
	 * @param player - The player that will be removed
	 * @return Whether the removal was successful or not (false: if the player never had the permission) **/
	public boolean removeMember(ClaimPermissionMember permission, EntityPlayer player) {
		UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
		return this.removeMember(permission, uuid);
	}

	/** Removes a member from the member list with a given permission and UUID
	 * @param permission - The permission which will be removed
	 * @param uuid - The player UUID that will be removed
	 * @return Whether the removal was successful or not (false: if the player never had the permission) **/
	public boolean removeMember(ClaimPermissionMember permission, UUID uuid) {
		return this.memberLists.remove(permission, uuid);
	}
	
	protected void addMembers(SetMultimap<ClaimPermissionMember, UUID> memberLists) {
	    for(ClaimPermissionMember key : memberLists.keySet()) {
	        this.memberLists.putAll(key, memberLists.get(key));
	    }
	}

	/** Returns a list of member UUIDs along with a set of their permissions. Used for easier displaying of member data. **/
	public ImmutableSetMultimap<UUID,ClaimPermissionMember> getMembers() {
		return this.memberLists.getValuesToKeys();
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

	@Nullable
	/** @return The UUID of the owner of this claim **/
	public UUID getOwner() {
		return this.ownerUUID;
	}

	@Nullable
	/** @return The offline UUID of the owner of this claim **/
	public UUID getOwnerOffline() {
		return this.ownerUUIDOffline;
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

	/** @return Name used for serialization. Also a Unique ID as it contains all the claim data concatenated.**/
	public String getSerialName() {
		return name;
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
			if(claim.getTrueViewName().equals(nameIn) && claim != this) { // Claim has the same name, is not this claim, and is owned by the same player
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
        UUID ownerOffline = this.getOwnerOffline();
        NBTTagCompound data = new NBTTagCompound();
        data.setIntArray("CLAIMINFO", claimVals);
        data.setString("OWNERUID", owner.toString());
        data.setString("OWNERUIDOFF", ownerOffline.toString());
        data.setString("TRUEVIEWNAME", this.getTrueViewName());
        
        data = ClaimNBTUtil.writeMembers(data, this.memberLists.getKeysToValues());
        data = ClaimNBTUtil.writeToggles(data, this.getToggles());
        return data;
	}
	
	public static ClaimArea deserialize(NBTTagCompound tag, String keyName) {
	    int[] claimVals = tag.getIntArray("CLAIMINFO");
        UUID owner = UUID.fromString(tag.getString("OWNERUID"));
        UUID ownerOffline = UUID.fromString(tag.getString("OWNERUIDOFF"));
        String trueViewName = tag.getString("TRUEVIEWNAME");
        if(trueViewName == null || trueViewName.equals("")) {
            trueViewName = keyName;
        }
        if(claimVals.length > 0 && claimVals[0] == 0) {
            ClaimItAPI.logger.debug("Valid version.");
            ClaimArea claim = new ClaimArea(claimVals[1], claimVals[2], claimVals[3], claimVals[4], claimVals[5], owner, ownerOffline, trueViewName);
            claim.addMembers(ClaimNBTUtil.readMembers(tag));
            claim.setToggles(ClaimNBTUtil.readToggles(tag));
            return claim;
        } else {
            ClaimItAPI.logger.log(Level.FATAL, "Detected version that doesn't exist yet! Mod was downgraded? Claim cannot be loaded.");
            throw new RuntimeException("Canceled loading to prevent loss of claim data. If you recently downgraded versions, please upgrade or contact author.");
        }
	}
}
