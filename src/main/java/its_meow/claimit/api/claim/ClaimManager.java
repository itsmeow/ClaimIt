package its_meow.claimit.api.claim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.event.ClaimAddedEvent;
import its_meow.claimit.api.event.ClaimCreatedEvent;
import its_meow.claimit.api.event.ClaimDeserializationEvent;
import its_meow.claimit.api.event.ClaimSerializationEvent;
import its_meow.claimit.api.serialization.ClaimSerializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClaimManager {

	private static ClaimManager instance = null;
	private ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
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

	/** Removes a claim. **/
	public void deleteClaim(ClaimArea claim) {
		claims.remove(claim);
	}

	/** @return A copy of the claims list. Final. **/
	public final ImmutableList<ClaimArea> getClaimsList() {
		return ImmutableList.copyOf(claims);
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

	@Nullable
	/** Gets the claim by the viewable name and the owner
	 * @param name - The viewable name set by the player 
	 * @param owner - The UUID of the owner of the claim 
	 * @returns The claim with this name and owner or null if no claim is found **/
	public ClaimArea getClaimByNameAndOwner(String name, UUID owner) {
		for(ClaimArea claim : this.claims) {
			if(claim.isTrueOwner(owner)) {
				if(claim.getTrueViewName().equals(owner + "_" + name)) {
					return claim;
				}
			}
		}
		return null;
	}

	@Nullable
	/** Gets the claim by the viewable name and the owner
	 * @param name - The true name of the claim (with UUID prefix)
	 * @returns The claim with this name and owner or null if no claim is found **/
	public ClaimArea getClaimByTrueName(String name) {
		for(ClaimArea claim : this.claims) {
			if(claim.getTrueViewName().equals(name)) {
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
	
	/** Check claim is not overlapping/illegal and add to list. Fires ClaimAddedEvent
     * @param claim - The claim to be added 
     * @returns true if claim was added, false if not. **/
	public boolean addClaim(ClaimArea claim) {
	    return addClaim(claim, true);
	}
	
	/** Check claim is not overlapping/illegal and add to list. Does not fire a ClaimAddedEvent
     * @param claim - The claim to be added 
     * @returns true if claim was added, false if not. **/
	public boolean addClaimNoEvent(ClaimArea claim) {
	    return addClaim(claim, false);
	}

	/** Check claim is not overlapping/illegal and add to list 
	 * @param claim - The claim to be added 
	 * @param fireEvent - If true, will fire a Claim Added event
	 * @returns true if claim was added, false if not. **/
	private boolean addClaim(ClaimArea claim, boolean fireEvent) {
		if(claims.size() != 0) {
			for(ClaimArea claimI : claims) {
				if(claimI.getDimensionID() == claim.getDimensionID()) {
					for(int i = 0; i <= claim.getSideLengthX(); i++) {
						for(int j = 0; j <= claim.getSideLengthZ(); j++) {
							BlockPos toCheck = new BlockPos(claim.getMainPosition().getX() + i, 0, claim.getMainPosition().getZ() + j);
							if(claimI.isBlockPosInClaim(toCheck)) {
								return false;
							}
						}
					}
					
					for(int i = 0; i <= claimI.getSideLengthX(); i++) {
						for(int j = 0; j <= claimI.getSideLengthZ(); j++) {
							BlockPos toCheck = new BlockPos(claimI.getMainPosition().getX() + i, 0, claimI.getMainPosition().getZ() + j);
							if(claim.isBlockPosInClaim(toCheck)) {
								return false;
							}
						}
					}
				}
			}
		}
		
		boolean doAdd = true;
		if(fireEvent) {
		    ClaimCreatedEvent event = new ClaimCreatedEvent(claim);
		    MinecraftForge.EVENT_BUS.post(event);
		    if(event.isCanceled()) {
		        doAdd = false;
		    }
		}
		if(doAdd) {
		    addClaimToListInsecurely(claim);
		}
		return doAdd;
	}

	/** Clears the list of players with admin enabled. **/
	public void clearAdmins() {
		this.admins.clear();
	}

	/** Adds a claim to the claim list without checking for overlaps. Don't use! **/
	private void addClaimToListInsecurely(ClaimArea claim) {
        ClaimAddedEvent event = new ClaimAddedEvent(claim);
        MinecraftForge.EVENT_BUS.post(event);
		claims.add(claim);
		//this.serialize(claim.getWorld());
	}

	/** Gets all claims owned by a UUID
	 * @param uuid - The UUID of the player to be searched for
	 * @return A {@link Set} of ClaimAreas owned by the player. If no claims are owned, returns null.
	 * **/
	@Nullable
	public Set<ClaimArea> getClaimsOwnedByPlayer(UUID uuid) {
		HashSet<ClaimArea> owned = new HashSet<ClaimArea>();
		for(ClaimArea claim : this.claims) {
			if(claim.isTrueOwner(uuid)) {
				owned.add(claim);
			}
		}
		return owned.size() > 0 ? owned : null;
	}

	/** Forces a world to save claim data. Removes all claim data that is stored and adds current data. **/
	public void serialize() {
		ClaimItAPI.logger.info("Saving claims data.");
		ClaimSerializer store = ClaimSerializer.get();
		if(store != null && store.data != null && store.data.getSize() > 0) {
			Set<String> toRemove = new HashSet<String>();
			for(String key : store.data.getKeySet()) { // Remove all data
				if(!key.equals("")) {
					toRemove.add(key);
				}
			}
			for(String key : toRemove) {
				store.data.removeTag(key);
			}
		}
		for(ClaimArea claim : claims) {
	        String serialName = claim.getSerialName();
	        NBTTagCompound data = claim.serialize();
	        
			ClaimSerializationEvent event = new ClaimSerializationEvent(data);
			MinecraftForge.EVENT_BUS.post(event);

			if(!event.isCanceled()) {
				store.data.setTag("CLAIM_" + serialName, data);
				store.markDirty();
			}
		}
	}

	/** Forces a world to load claim data. 
	 * Overwrites new claim data since last load (this is because it is used at world/server startup)! A "reload" should probably save before doing this. **/
	public void deserialize() {
		claims.clear();
		ClaimSerializer store = ClaimSerializer.get();
		NBTTagCompound comp = store.data;
		if(comp != null) {
			for(String key : comp.getKeySet()) {
				System.out.println("Loading " + key);
	            ClaimArea claim = ClaimArea.deserialize(comp.getCompoundTag(key), key);

	            ClaimDeserializationEvent event = new ClaimDeserializationEvent(claim);
	            MinecraftForge.EVENT_BUS.post(event);

	            if(!event.isCanceled()) {
	                this.addClaimNoEvent(event.getClaim());
	            } else {
	                ClaimItAPI.logger.log(Level.INFO, "Event cancelled loading of this claim. ");
	            }
			}
		}
	}

	/** Attempts to get name from UUID cache. Requires World to get server instance. 
	 * @param uuid - The UUID to attempt to retrieve the name for
	 * @param world - A world instance. This is used to get the server instance.
	 * @return The name for this UUID or the UUID as a String if none was found**/
	@Nonnull
	public static String getPlayerName(UUID uuid, World worldIn) {
		String name = null;
		GameProfile profile = worldIn.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(uuid);
		if(profile != null) {
			name = profile.getName();
		} else {
		    name = uuid.toString();
		}
		return name;
	}

	/** Clears the list of stored claims. WARNING: If data is saved after this is done (like, if the server shuts down) all the claims data will be gone permanently. This is only used before deserialization. **/
	public void clearClaims() {
		this.claims.clear();
	}

}
