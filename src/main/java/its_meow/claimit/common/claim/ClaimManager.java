package its_meow.claimit.common.claim;

import java.util.HashSet;
import java.util.Set;

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
	
	
	public boolean isBlockInAnyClaim(BlockPos pos, World world) {
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
				if(overlaps == 0) { // Not overlapping nearby claims, adding.
					claims.add(claim);
					return true;
				}
			} else { // No claims within side lengths, can add freely
				claims.add(claim);
				return true;
			}
		} else { // New dimension, don't need to check
			claims.add(claim);
			return true;
		}
		return false;
	}


}
