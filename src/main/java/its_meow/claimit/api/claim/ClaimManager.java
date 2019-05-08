package its_meow.claimit.api.claim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.event.ClaimAddedEvent;
import its_meow.claimit.api.event.ClaimCreatedEvent;
import its_meow.claimit.api.event.ClaimDeserializationEvent;
import its_meow.claimit.api.event.ClaimRemovedEvent;
import its_meow.claimit.api.event.ClaimSerializationEvent;
import its_meow.claimit.api.event.ClaimsClearedEvent;
import its_meow.claimit.api.serialization.ClaimSerializer;
import its_meow.claimit.api.util.BiMultiMap;
import its_meow.claimit.api.util.ClaimChunkUtil;
import its_meow.claimit.api.util.ClaimChunkUtil.ClaimChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClaimManager {

	private static ClaimManager instance = null;
	private ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
	private BiMultiMap<UUID, ClaimArea> ownedClaims = new BiMultiMap<UUID, ClaimArea>();
	private BiMultiMap<ClaimChunk, ClaimArea> chunks = new BiMultiMap<ClaimChunk, ClaimArea>();
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

	/** Removes a claim. 
	 * @return True if the {@link ClaimRemovedEvent} wasn't canceled and if the claim was present in the list
	 **/
	public boolean deleteClaim(ClaimArea claim) {
	    if(!MinecraftForge.EVENT_BUS.post(new ClaimRemovedEvent(claim))) {
	        if(claims.remove(claim)) {
	            chunks.removeValueFromAll(claim);
	            ownedClaims.removeValueFromAll(claim);
	            return true;
	        }
	    }
	    return false;
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
	    ClaimChunk chunk = ClaimChunkUtil.getChunk(pos);
	    Set<ClaimArea> claimsInChunk = chunks.getValues(chunk);
	    if(claimsInChunk != null && claimsInChunk.size() > 0) {
	        for(ClaimArea claim : claimsInChunk) {
	            if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
	                return claim;
	            }
	        }
	    } else {
	        for(ClaimArea claim : claims) {
	            if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
	                chunks.put(chunk, claim); // Cache this for faster retrieval
	                return claim;
	            }
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
		return this.getClaimAtLocation(world, pos) != null;
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
		ownedClaims.put(claim.getOwner(), claim);
		for(ClaimChunk c : claim.getOverlappingChunks()) {
		    chunks.put(c, claim);
		}
	}

	/** Gets all claims owned by a UUID
	 * @param uuid - The UUID of the player to be searched for
	 * @return A {@link Set} of ClaimAreas owned by the player. If no claims are owned, returns null.
	 * **/
	@Nullable
	public Set<ClaimArea> getClaimsOwnedByPlayer(UUID uuid) {
		return ownedClaims.getValues(uuid).size() > 0 ? ownedClaims.getValues(uuid) : null;
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
	    this.clearClaims();
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
	                ClaimItAPI.logger.log(Level.INFO, "Event cancelled loading of this claim.");
	            }
			}
		}
	}
	
	public void clearClaims() {
	       MinecraftForge.EVENT_BUS.post(new ClaimsClearedEvent.Pre());
	        claims.clear();
	        ownedClaims.clear();
	        chunks.clear();
	        MinecraftForge.EVENT_BUS.post(new ClaimsClearedEvent.Post());
	}

}
