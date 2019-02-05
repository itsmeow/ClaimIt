package its_meow.claimit.claim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import its_meow.claimit.permission.ClaimPermissionMember;
import its_meow.claimit.permission.ClaimPermissionRegistry;
import its_meow.claimit.permission.ClaimPermissionToggle;
import its_meow.claimit.permission.ClaimPermissions;
import its_meow.claimit.permission.EnumPermissionType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ClaimArea {

	/** X position of the lowest (closest to -inf, -inf) corner **/
	private int posX;
	/** Z position of the lowest (closest to -inf, -inf) corner **/
	private int posZ;
	/** Dimension ID of the claim's location -
	 *  FINAL **/
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
	private Map<ClaimPermissionMember, ArrayList<UUID>> memberLists;
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
		this.memberLists = new HashMap<ClaimPermissionMember, ArrayList<UUID>>();
		this.toggles = new HashMap<ClaimPermissionToggle, Boolean>();
		
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
			if(ClaimManager.getManager().isAdmin(player)) {
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

	public boolean hasPermission(ClaimPermissionMember permission, EntityPlayer player) {
		if(permission.type == EnumPermissionType.TOGGLE) {
			return false;
		}
		if(this.isOwner(player)) {
			return true;
		}
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(array != null && array.contains(EntityPlayer.getUUID(player.getGameProfile()))) {
			return true;
		}
		return false;
	}
	
	/** Tells whether a permission is enabled in a claim or not 
	 *  @param perm - The permission to check
	 *  @return The toggle status (on = true) **/
	public boolean isPermissionToggled(ClaimPermissionToggle perm) {
		return this.toggles.get(perm);
	}

	/** Do NOT use this for permission checking. Only for use in removing members. 
	 * Why: doesn't account for admins or the owner of the claim. It purely returns if a member is in the list.**/
	public boolean inPermissionList(ClaimPermissionMember permission, UUID id) {
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(array != null && array.contains(id)) {
			return true;
		}
		return false;
	}
	
	/** Get the member arrays for a permission 
	 * @param permission - The permission to get the array for. 
	 * @return The array used to store members of permission
	 * **/
	@Nullable
	public ArrayList<UUID> getArrayForPermission(ClaimPermissionMember permission) {
		return memberLists.get(permission);
	}
	
	/** Adds a member to the member list with a given permission and player object 
	 * This runs {@link ClaimArea::addMember(ClaimPermissionMember, UUID)} after converting the player to UUID
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
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(!array.contains(uuid)) {
			array.add(uuid);
			return true;
		}
		return false;
	}
	
	/** Removes a member from the member list with a given permission and player object
	 * This runs {@link ClaimArea::removeMember(ClaimPermissionMember, UUID)} after converting the player to UUID
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
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(array.contains(uuid)) {
			array.remove(uuid);
			return true;
		}
		return false;
	}
	
	/** Returns a list of member UUIDs along with a set of their permissions. Used for easier displaying of member data. **/
	public Map<UUID, HashSet<ClaimPermissionMember>> getMembers() {
		HashMap<UUID, HashSet<ClaimPermissionMember>> map = new HashMap<UUID, HashSet<ClaimPermissionMember>>();
		for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
			ArrayList<UUID> members = this.getArrayForPermission(perm);
			for(UUID member : members) {
				if(map.get(member) == null) {
					HashSet<ClaimPermissionMember> set = new HashSet<ClaimPermissionMember>();
					set.add(perm);
					if(map.containsKey(member)) {
						map.remove(member);
					}
					map.put(member, set);
				} else {
					HashSet<ClaimPermissionMember> set = map.get(member);
					set.add(perm);
					if(map.containsKey(member)) {
						map.remove(member);
					}
					map.put(member, set);
				}
			}
		}
		return map;
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

}
