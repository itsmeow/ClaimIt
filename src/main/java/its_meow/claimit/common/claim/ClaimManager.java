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

	private ClaimManager() {}

	public static ClaimManager getManager() {
		if(instance == null) {
			instance = new ClaimManager();
		}

		return instance;
	}

	/** Removes a claim. Requires player object as verification of ownership **/
	public boolean deleteClaim(ClaimArea claim, EntityPlayer player) {
		if(claim.getOwner() == player.getUUID(player.getGameProfile())) {
			if(claim.getOwnerOffline() == player.getOfflineUUID(player.getName())) {
				claims.remove(claim);
				this.serialize(claim.getWorld());
				return true;
			}
		}
		return false;
	}

	public boolean doesPlayerOwnClaim(ClaimArea claim, EntityPlayer player) {
		try {
			if(claim.getOwner() == player.getUUID(player.getGameProfile())) {
				// If online UUID does match then make sure offline does too
				if(claim.getOwnerOffline() == player.getOfflineUUID(player.getName())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Nullable
	public ClaimArea getClaimAtLocation(World worldIn, BlockPos posIn) {
		if(claims.size() == 0) {
			return null;
		}
		for(ClaimArea claim : claims) {
			if(claim.getWorld() == worldIn && claim.isBlockPosInClaim(posIn)) {
				return claim;
			}
		}
		return null;
	}


	public boolean isBlockInAnyClaim(BlockPos pos, World world) {
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
	/** Check claim is not overlapping/illegal and add to list **/
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

	/** For use INTERNALLY ONLY!!! There's a reason this is private. Don't mess with it. **/
	private void addClaimToListInsecurely(ClaimArea claim) {
		claims.add(claim);
		//this.serialize(claim.getWorld());
	}

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
