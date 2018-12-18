package its_meow.claimit.common.claim;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import its_meow.claimit.ClaimIt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClaimManager {

	private static ClaimManager instance = null;
	private Set<ClaimArea> claims = new HashSet<ClaimArea>();
	private Set<EntityPlayer> admins = new HashSet<EntityPlayer>();

	private ClaimManager() {}

	public static ClaimManager getManager() {
		if(instance == null) {
			instance = new ClaimManager();
		}

		return instance;
	}
	
	/** Adds a player to the admin list, allowing claim bypass **/
	public void addAdmin(EntityPlayer player) {
		admins.add(player);
	}
	
	/** Removes a player from the admin list, removing claim bypass **/
	public void removeAdmin(EntityPlayer player) {
		admins.remove(player);
	}
	
	/** Tells whether a player is an admin/has claim bypass
	 * @return True if has admin, false if not. **/
	public boolean isAdmin(EntityPlayer player) {
		return admins.contains(player);
	}

	/** Removes a claim. Requires player object as verification of ownership **/
	public boolean deleteClaim(ClaimArea claim, EntityPlayer player) {
		if(claim.isOwner(player)) {
				claims.remove(claim);
				this.serialize(claim.getWorld());
				return true;
		}
		return false;
	}

	@Nullable
	/** Gets the claim at a given BlockPos in a World 
	 * @param world - The world checked for claim 
	 * @param pos - The position checked for a claim 
	 * @returns The claim at the location or null if no claim is found **/
	public ClaimArea getClaimAtLocation(World world, BlockPos pos) {
		if(claims.size() == 0) {
			return null;
		}
		for(ClaimArea claim : claims) {
			if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
				return claim;
			}
		}
		return null;
	}

	/** Gets the claim at a given BlockPos in a World and returns true if not null
	 * @param world - The world checked for claim 
	 * @param pos - The position checked for a claim 
	 * @returns True if a claim is found, false if one is not found **/
	public boolean isBlockInAnyClaim(World world, BlockPos pos) {
		if(claims.size() == 0) {
			return false;
		}
		for(ClaimArea claim : claims) {
			if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
				return true;
			}
		}
		return false;
	}

	private static int dimT = 0;
	
	/** Check claim is not overlapping/illegal and add to list 
	 * @param claim - The claim to be added 
	 * @returns true if claim was added, false if not. **/
	public boolean addClaim(ClaimArea claim) {
		dimT = 0;
		claims.stream().forEach(c -> dimT += c.getDimensionID() != claim.getDimensionID() ? 0 : 1);
		if(dimT != 0) {
			//Check for nearby claims
			int nearby = 0;
			Set<ClaimArea> nearbyClaims = new HashSet<ClaimArea>();
			for(ClaimArea claimI : claims) {
				if(claimI.getDimensionID() == claim.getDimensionID()) {
					int xDistance = Math.abs(claim.getMainPosition().getX() - claimI.getMainPosition().getX());
					int zDistance = Math.abs(claim.getMainPosition().getZ() - claimI.getMainPosition().getZ());
					if(xDistance < (claimI.getSideLengthX() + claim.getSideLengthX()) && zDistance < (claimI.getSideLengthZ() + claim.getSideLengthZ())) {
						nearby++;
						nearbyClaims.add(claimI);
					}
				}
			}
			if(nearby > 0) { // Some claims could overlap nearby
				BlockPos[] claimCorners = claim.getFourCorners();
				int overlaps = 0;
				for(BlockPos corner : claimCorners) { // Check if any corners are in nearby claims
					for(ClaimArea claimI : nearbyClaims) {
						if(claimI.isBlockPosInClaim(corner)) {
							overlaps++;
						}
					}
				}
				for(ClaimArea claimI : nearbyClaims) { // Check if any corners are in nearby claims
					BlockPos[] claimCornerForThis = claimI.getFourCorners();
					for(BlockPos corner : claimCornerForThis) {
						if(claim.isBlockPosInClaim(corner)) {
							overlaps++;
						}
					}
				}
				if(overlaps == 0) { // Not overlapping nearby claims, adding.
					addClaimToListInsecurely(claim);
					return true;
				}
			} else { // No claims within side lengths, can add freely
				addClaimToListInsecurely(claim);
				return true;
			}
		} else { // New dimension, don't need to check
			addClaimToListInsecurely(claim);
			return true;
		}
		return false;
	}

	/** Adds a claim to the claim list without checking for overlaps. Don't use! **/
	private void addClaimToListInsecurely(ClaimArea claim) {
		claims.add(claim);
		//this.serialize(claim.getWorld());
	}

	/** Forces a world to save claim data 
	 * @param world - The world to be saved **/
	public void serialize(World world) {
		if(!world.isRemote) {
			ClaimSerializer store = ClaimSerializer.get(world);
			for(ClaimArea claim : claims) {
				if(claim.getWorld() == world) {
					int[] claimVals = claim.getSelfAsInt();
					UUID owner = claim.getOwner();
					UUID ownerOffline = claim.getOwnerOffline();
					String serialName = claim.getSerialName();
					store.data.setIntArray(serialName, claimVals);
					store.data.setUniqueId(serialName + "_UID", owner);
					store.data.setUniqueId(serialName + "_UIDOFF", ownerOffline);
					store.markDirty();
					System.out.println("Saving claim: " + serialName);
				}
			}
		}
	}
	
	/** Forces a world to load claim data. 
	 * Overwrites new claim data since last load!
	 * @param world - The world to be saved **/
	public void deserialize(World world) {
		if(!world.isRemote) {
			ClaimSerializer store = ClaimSerializer.get(world);
			NBTTagCompound comp = store.data;
			if(comp != null ) {
				for(String key : comp.getKeySet()) {
					if(!key.contains("_UID")) {
						System.out.println("Loading " + key);
						int[] claimVals = comp.getIntArray(key);
						UUID owner = comp.getUniqueId(key + "_UID");
						UUID ownerOffline = comp.getUniqueId(key + "_UIDOFF");
						if(claimVals.length > 0 && claimVals[0] == 0) {
							System.out.println("Valid version.");
							ClaimArea claim = new ClaimArea(claimVals[1], claimVals[2], claimVals[3], claimVals[4], claimVals[5], owner, ownerOffline);
							this.addClaim(claim);
						} else {
							ClaimIt.logger.log(Level.FATAL, "Detected version that doesn't exist yet! Mod was downgraded? Claim cannot be loaded.");
						}
					}
				}
			}
		}
	}

}
