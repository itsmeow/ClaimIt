package dev.itsmeow.claimit.api.claim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import dev.itsmeow.claimit.api.ClaimItAPI;
import dev.itsmeow.claimit.api.event.claim.ClaimAddedEvent;
import dev.itsmeow.claimit.api.event.claim.ClaimCreatedEvent;
import dev.itsmeow.claimit.api.event.claim.ClaimRemovedEvent;
import dev.itsmeow.claimit.api.event.claim.ClaimsClearedEvent;
import dev.itsmeow.claimit.api.event.claim.serialization.ClaimDeserializationEvent;
import dev.itsmeow.claimit.api.event.claim.serialization.ClaimSerializationEvent;
import dev.itsmeow.claimit.api.serialization.ClaimSerializer;
import dev.itsmeow.claimit.api.util.objects.BiMultiMap;
import dev.itsmeow.claimit.api.util.objects.ClaimChunkUtil;
import dev.itsmeow.claimit.api.util.objects.ClaimChunkUtil.ClaimChunk;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

public class ClaimManager {

    private static ClaimManager instance = null;
    private ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
    private BiMultiMap<UUID, ClaimArea> ownedClaims = new BiMultiMap<UUID, ClaimArea>();
    private Map<Integer, BiMultiMap<ClaimChunk, ClaimArea>> chunks = new HashMap<Integer, BiMultiMap<ClaimChunk, ClaimArea>>();

    protected ClaimManager() {}

    public static ClaimManager getManager() {
        if(instance == null) {
            instance = new ClaimManager();
        }

        return instance;
    }

    /** Removes a claim. 
     * @return True if the {@link ClaimRemovedEvent} wasn't canceled and if the claim was present in the list
     **/
    public boolean deleteClaim(ClaimArea claim) {
        if(!MinecraftForge.EVENT_BUS.post(new ClaimRemovedEvent(claim))) {
            if(claims.remove(claim)) {
                ClaimItAPI.logger.debug("Removed claim " + claim.hashCode());
                if(chunks.containsKey(claim.getDimensionID())) {
                    chunks.get(claim.getDimensionID()).removeValueFromAll(claim);
                }
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
        int dimID = world.provider.getDimension();
        chunks.putIfAbsent(dimID, new BiMultiMap<ClaimChunk, ClaimArea>());
        Set<ClaimArea> claimsInChunk = chunks.get(dimID).getValues(chunk);
        if(claimsInChunk != null && claimsInChunk.size() > 0) {
            for(ClaimArea claim : claimsInChunk) {
                if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
                    return claim;
                }
            }
        } else {
            for(ClaimArea claim : claims) {
                if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
                    chunks.get(dimID).put(chunk, claim); // Cache this for faster retrieval
                    return claim;
                }
            }
        }
        return null;
    }

    @Nullable
    /**
     * Gives a list of claims in a ClaimChunk
     * @param dimensionID - The dimension ID of the world to check
     * @param chunk - The chunk
     * @return A list of claims or empty list or null
     */
    public ImmutableSet<ClaimArea> getClaimsInChunk(int dimensionID, ClaimChunk chunk) {
        if(this.chunks.containsKey(dimensionID)) {
            return ImmutableSet.copyOf(this.chunks.get(dimensionID).getValues(chunk));
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
            if(claim.isOwner(owner)) {
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
     * @param claim - The claim to be added **/
    public ClaimAddResult addClaim(ClaimArea claim) {
        return addClaim(claim, true);
    }

    /** Check claim is not overlapping/illegal and add to list. Does not fire a ClaimAddedEvent
     * @param claim - The claim to be added **/
    public ClaimAddResult addClaimNoEvent(ClaimArea claim) {
        return addClaim(claim, false);
    }

    /** Check claim is not overlapping/illegal and add to list 
     * @param claim - The claim to be added 
     * @param fireEvent - If true, will fire a Claim Added event **/
    private ClaimAddResult addClaim(ClaimArea claim, boolean fireEvent) {
        if(claim.getArea() > 46000 * 46000 || claim.getArea() < 4) {
            return ClaimAddResult.TOO_LARGE;
        }
        if(claims.size() != 0) {
            for(ClaimArea claimI : claims) {
                if(claimI.getDimensionID() == claim.getDimensionID()) {
                    for(int i = 0; i <= claim.getSideLengthX(); i++) {
                        for(int j = 0; j <= claim.getSideLengthZ(); j++) {
                            BlockPos toCheck = new BlockPos(claim.getMainPosition().getX() + i, 0, claim.getMainPosition().getZ() + j);
                            if(claimI.isBlockPosInClaim(toCheck)) {
                                return ClaimAddResult.OVERLAP;
                            }
                        }
                    }

                    for(int i = 0; i <= claimI.getSideLengthX(); i++) {
                        for(int j = 0; j <= claimI.getSideLengthZ(); j++) {
                            BlockPos toCheck = new BlockPos(claimI.getMainPosition().getX() + i, 0, claimI.getMainPosition().getZ() + j);
                            if(claim.isBlockPosInClaim(toCheck)) {
                                return ClaimAddResult.OVERLAP;
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
            return ClaimAddResult.ADDED;
        } else {
            return ClaimAddResult.CANCELLED;
        }
    }

    /** Adds a claim to the claim list without checking for overlaps. Don't use! **/
    private void addClaimToListInsecurely(ClaimArea claim) {
        ClaimAddedEvent event = new ClaimAddedEvent(claim);
        MinecraftForge.EVENT_BUS.post(event);
        claims.add(claim);
        ownedClaims.put(claim.getOwner(), claim);
        ClaimItAPI.logger.debug("Added claim " + claim.hashCode() + " owned by " + claim.getOwner());
        for(ClaimChunk c : claim.getOverlappingChunks()) {
            chunks.putIfAbsent(claim.getDimensionID(), new BiMultiMap<ClaimChunk, ClaimArea>());
            chunks.get(claim.getDimensionID()).put(c, claim);
        }
    }

    /** Gets all claims owned by a UUID
     * @param uuid - The UUID of the player to be searched for
     * @return A {@link Set} of ClaimAreas owned by the player. If no claims are owned, returns an empty list.
     * **/
    public ImmutableSet<ClaimArea> getClaimsOwnedByPlayer(UUID uuid) {
        return ImmutableSet.copyOf(ownedClaims.getValues(uuid));
    }

    /** Forces a world to save claim data. Removes all claim data that is stored and adds current data. **/
    public void serialize() {
        ClaimItAPI.logger.debug("Saving claims data.");
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
            String serialName = String.valueOf(claim.hashCode());
            NBTTagCompound data = claim.serialize();

            ClaimSerializationEvent event = new ClaimSerializationEvent(data);
            MinecraftForge.EVENT_BUS.post(event);

            if(!event.isCanceled()) {
                store.data.setTag("CLAIM_" + serialName, data);
                store.markDirty();
                ClaimItAPI.logger.debug("Serializing claim with hash " + serialName);
            }
        }
    }

    /** Forces a world to load claim data. 
     * Overwrites new claim data since last load (this is because it is used at world/server startup)! A "reload" should probably save before doing this. **/
    public void deserialize() {
        try { 
            this.clearClaims();
            ClaimSerializer store = ClaimSerializer.get();
            NBTTagCompound comp = store.data;
            if(comp != null) {
                for(String key : comp.getKeySet()) {
                    ClaimItAPI.logger.debug("Loading " + key);
                    ClaimArea claim = ClaimArea.deserialize(comp.getCompoundTag(key), key);

                    ClaimDeserializationEvent event = new ClaimDeserializationEvent(claim);
                    MinecraftForge.EVENT_BUS.post(event);

                    if(!event.isCanceled()) {
                        this.addClaimNoEvent(event.getClaim());
                    } else {
                        ClaimItAPI.logger.debug("Event cancelled loading of this claim.");
                    }
                }
            } else {
                ClaimItAPI.logger.warn("No claim data tag was found.");
            }
        } catch(Exception e) {
            e.printStackTrace();
            ClaimItAPI.logger.warn("Error deserializing claims! Copying data to world/data/claimitapi_ClaimsData.dat.old");
            World world = DimensionManager.getWorld(0);
            if(world != null) {
                File file = new File(world.getSaveHandler().getWorldDirectory().getAbsolutePath() + "/data/claimitapi_ClaimsData.dat");
                if(file.exists()) {
                    try {
                        Files.copy(file, new File(file.getAbsolutePath() + ".old"));
                    } catch(IOException e1) {
                        e1.printStackTrace();
                        ClaimItAPI.logger.error("Failed to copy claimitapi_ClaimsData.dat");
                    }
                }
            } else {
                ClaimItAPI.logger.error("Could not backup claims data due to null overworld object!");
            }
        }
    }

    public void clearClaims() {
        MinecraftForge.EVENT_BUS.post(new ClaimsClearedEvent.Pre());
        claims.clear();
        ownedClaims.clear();
        chunks.clear();
        ClaimItAPI.logger.debug("Cleared claims list");
        MinecraftForge.EVENT_BUS.post(new ClaimsClearedEvent.Post());
    }

    public static enum ClaimAddResult {
        ADDED,
        OVERLAP,
        CANCELLED,
        TOO_LARGE,
        SAME_NAME,
        ALREADY_EXISTS,
        OUT_OF_BOUNDS;
    }

}
