package its_meow.claimit.claim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

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
	/* Private fields for storing member UUIDs */
	private ArrayList<UUID> membersModify;
	private ArrayList<UUID> membersUse;
	private ArrayList<UUID> membersEntity;
	private ArrayList<UUID> membersPVP;

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
		this.membersModify = new ArrayList<UUID>();
		this.membersUse = new ArrayList<UUID>();
		this.membersEntity = new ArrayList<UUID>();
		this.membersPVP = new ArrayList<UUID>();
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
		return hasPermission(EnumPerm.MODIFY, player);
	}

	public boolean canUse(EntityPlayer player) {
		return hasPermission(EnumPerm.USE, player);
	}
	
	public boolean canEntity(EntityPlayer player) {
		return hasPermission(EnumPerm.ENTITY, player);
	}
	
	public boolean canPVP(EntityPlayer player) {
		return hasPermission(EnumPerm.PVP, player);
	}

	public boolean hasPermission(EnumPerm permission, EntityPlayer player) {
		if(this.isOwner(player)) {
			return true;
		}
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(array != null && array.contains(EntityPlayer.getUUID(player.getGameProfile()))) {
			return true;
		}
		return false;
	}
	
	/** Do NOT use this for permission checking. Only for use in removing members. **/
	public boolean inPermissionList(EnumPerm permission, UUID id) {
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(array != null && array.contains(id)) {
			return true;
		}
		return false;
	}
	
	@Nullable
	public ArrayList<UUID> getArrayForPermission(EnumPerm permission) {
		switch(permission) {
		case MODIFY: return membersModify;
		case USE: return membersUse;
		case ENTITY: return membersEntity;
		case PVP: return membersPVP;
		default: return null;
		}
	}
	
	public boolean addMember(EnumPerm permission, EntityPlayer player) {
		UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
		return this.addMember(permission, uuid);
	}
	
	public boolean addMember(EnumPerm permission, UUID uuid) {
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(!array.contains(uuid)) {
			array.add(uuid);
			return true;
		}
		return false;
	}

	public boolean removeMember(EnumPerm permission, EntityPlayer player) {
		UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
		return this.removeMember(permission, uuid);
	}
	
	public boolean removeMember(EnumPerm permission, UUID uuid) {
		ArrayList<UUID> array = getArrayForPermission(permission);
		if(array.contains(uuid)) {
			array.remove(uuid);
			return true;
		}
		return false;
	}
	
	public Map<UUID, HashSet<EnumPerm>> getMembers() {
		HashMap<UUID, HashSet<EnumPerm>> map = new HashMap<UUID, HashSet<EnumPerm>>();
		for(EnumPerm perm : EnumPerm.values()) {
			ArrayList<UUID> members = this.getArrayForPermission(perm);
			for(UUID member : members) {
				if(map.get(member) == null) {
					HashSet<EnumPerm> set = new HashSet<EnumPerm>();
					set.add(perm);
					if(map.containsKey(member)) {
						map.remove(member);
					}
					map.put(member, set);
				} else {
					HashSet<EnumPerm> set = map.get(member);
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
	
	public boolean isBlockPosInClaim(BlockPos blockPos) {
		boolean isInXRange = (blockPos.getX() < this.getHXZPosition().getX() + 1) && (blockPos.getX() > this.posX - 1);
		boolean isInZRange = (blockPos.getZ() < this.getHXZPosition().getZ() + 1) && (blockPos.getZ() > this.posZ - 1);

		return isInXRange && isInZRange;
	}

	/** Returns posX and posZ as BlockPos **/
	public BlockPos getMainPosition() {
		return new BlockPos(posX, 0, posZ);
	}

	/** Returns the highest X and Z corner **/
	public BlockPos getHXZPosition() {
		return new BlockPos(posX + sideLengthX, 0, posZ + sideLengthZ);
	}

	/** Returns the highest X and lowest Z corner **/
	public BlockPos getHXLZPosition() {
		return new BlockPos(posX + sideLengthX, 0, posZ);
	}

	/** Returns the lowest X and the highest Z corner **/
	public BlockPos getLXHZPosition() {
		return new BlockPos(posX, 0, posZ + sideLengthZ);
	}

	public BlockPos[] getTwoMainClaimCorners() {
		BlockPos[] corners = new BlockPos[2];
		corners[0] = this.getMainPosition();
		corners[1] = this.getHXZPosition();
		return corners;
	}

	public BlockPos[] getFourCorners() {
		BlockPos[] corners = new BlockPos[4];
		corners[0] = this.getMainPosition();
		corners[1] = this.getHXLZPosition();
		corners[2] = this.getLXHZPosition();
		corners[3] = this.getHXZPosition();
		return corners;
	}

	public int getSideLengthX() {
		return sideLengthX;
	}

	public int getSideLengthZ() {
		return sideLengthZ;
	}

	@Nullable
	public UUID getOwner() {
		return this.ownerUUID;
	}

	@Nullable
	public UUID getOwnerOffline() {
		return this.ownerUUIDOffline;
	}

	public int getDimensionID() {
		return dimID;
	}

	public World getWorld() {
		return DimensionManager.getWorld(this.dimID);
	}

	public int getArea() {
		return (sideLengthX + 1) * (sideLengthZ + 1);
	}

	// Versions Store:
	// 0: {0, dimID, posX, posZ, sideLengthX, sideLengthZ}

	public int[] getSelfAsInt() {
		// 0 is version for updates to serialization, interpret version if changes are made to serialization structure
		int[] s = {0, dimID, posX, posZ, sideLengthX, sideLengthZ};
		return s;
	}

	public String getSerialName() {
		return name;
	}
	
	public String getTrueViewName() {
		return viewName;
	}
	
	public String getDisplayedViewName() {
		if(!viewName.contains("_")) { // The data was edited! Making a new name...
			this.viewName = ownerUUID.toString() + "_" + Math.abs(posX) + Math.abs(posZ) + dimID + Math.round(Math.random() * 100);
		}
		return viewName.substring(viewName.indexOf('_') + 1);
	}
	
	/** Sets the used name for this ClaimArea. 
	 * @param nameIn - Name to set
	 * @param player - Player that owns the claim (will not work if not the true owner)
	 * @return True if no other claims by the owner have this as their name **/
	public boolean setViewName(String nameIn, EntityPlayer player) {
		boolean pass = true;
		if(!this.isTrueOwner(player)) { // Player does not own this claim
			return false;
		}
		for(ClaimArea claim : ClaimManager.getManager().getClaimsList()) {
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
