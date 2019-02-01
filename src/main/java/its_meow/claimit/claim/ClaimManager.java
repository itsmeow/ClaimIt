package its_meow.claimit.claim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import its_meow.claimit.ClaimIt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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

	/** Removes a claim. **/
	public void deleteClaim(ClaimArea claim) {
		claims.remove(claim);
	}

	/** @return A copy of the claims list. Final. **/
	public final Set<ClaimArea> getClaimsList() {
		Set<ClaimArea> claimsList = new HashSet<ClaimArea>();
		for(ClaimArea claim : claims) {
			claimsList.add(claim);
		}
		return claimsList;
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

	/** Clears the list of players with admin enabled. **/
	public void clearAdmins() {
		this.admins.clear();
	}

	/** Adds a claim to the claim list without checking for overlaps. Don't use! **/
	private void addClaimToListInsecurely(ClaimArea claim) {
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
			int[] claimVals = claim.getSelfAsInt();
			UUID owner = claim.getOwner();
			UUID ownerOffline = claim.getOwnerOffline();
			String serialName = claim.getSerialName();
			NBTTagCompound data = new NBTTagCompound();
			data.setIntArray("CLAIMINFO", claimVals);
			data.setString("OWNERUID", owner.toString());
			data.setString("OWNERUIDOFF", ownerOffline.toString());
			System.out.println("Owner: " + owner);
			data.setString("TRUEVIEWNAME", claim.getTrueViewName());
			NBTTagCompound memberCompound = new NBTTagCompound();
			for(EnumPerm perm : EnumPerm.values()) {
				NBTTagList members = new NBTTagList();
				for(UUID member : claim.getArrayForPermission(perm)) {
					members.appendTag(new NBTTagString(member.toString()));
				}
				memberCompound.setTag(perm.name(), members);
			}
			data.setTag("MEMBERS", memberCompound);
			store.data.setTag("CLAIM_" + serialName, data);
			store.markDirty();
			System.out.println("Saving claim: " + serialName);
		}
	}

	/** Forces a world to load claim data. 
	 * Overwrites new claim data since last load (this is because it is used at world/server startup)! A "reload" should probably save before doing this. **/
	public void deserialize() {
		claims.clear();
		ClaimSerializer store = ClaimSerializer.get();
		NBTTagCompound comp = store.data;
		if(comp != null ) {
			for(String key : comp.getKeySet()) {
				System.out.println("Loading " + key);
				NBTTagCompound data = comp.getCompoundTag(key);
				int[] claimVals = data.getIntArray("CLAIMINFO");
				UUID owner = UUID.fromString(data.getString("OWNERUID"));
				UUID ownerOffline = UUID.fromString(data.getString("OWNERUIDOFF"));
				System.out.println("Owner: " + owner);
				String trueViewName = data.getString("TRUEVIEWNAME");
				if(trueViewName == null || trueViewName.equals("")) {
					trueViewName = key;
				}
				if(claimVals.length > 0 && claimVals[0] == 0) {
					System.out.println("Valid version.");
					ClaimArea claim = new ClaimArea(claimVals[1], claimVals[2], claimVals[3], claimVals[4], claimVals[5], owner, ownerOffline, trueViewName);

					NBTTagCompound memberCompound = data.getCompoundTag("MEMBERS");
					for(String permString : memberCompound.getKeySet()) {
						if(EnumPerm.valueOf(permString) != null) {
							NBTTagList tagList = memberCompound.getTagList(permString, Constants.NBT.TAG_STRING);
							for(int i = 0; i < tagList.tagCount(); i++) {
								String uuidString = tagList.getStringTagAt(i);
								UUID member = UUID.fromString(uuidString);
								claim.addMember(EnumPerm.valueOf(permString), member);
							}
						}
					}

					this.addClaim(claim);
				} else {
					ClaimIt.logger.log(Level.FATAL, "Detected version that doesn't exist yet! Mod was downgraded? Claim cannot be loaded.");
				}
			}
		}
	}

	/** Attempts to get name from UUID cache, then requests name from Mojang servers. Requires World to get server instance. 
	 * @param uuid - The UUID to attempt to retrieve the name for
	 * @param world - A world instance. This is used to get the server instance.
	 * @return The name of the UUID or null if none was found**/
	@Nullable
	public static String getPlayerName(UUID uuid, World worldIn) {
		String name = null;
		GameProfile profile = worldIn.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(uuid);
		if(profile != null) {
			name = profile.getName();
		}
		if(name != null) {
			return name;
		}

		// Could not get name from cache, request from server.
		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();
			String line;

			while((line = reader.readLine()) != null) {

				sb.append(line + "\n");

			}

			//System.out.println(sb.toString());

			JsonParser parser = new JsonParser();
			JsonElement obj = parser.parse(sb.toString().trim());
			name = obj.getAsJsonObject().get("name").getAsString();
			reader.close();
		} catch (Exception e) {
			System.out.println("Unable to retrieve name for UUID: " + uuid);
			System.out.println("Error: " + e.getMessage());
		}
		return name;
	}
	
	/** Clears the list of stored claims. WARNING: If data is saved after this is done (like, if the server shuts down) all the claims data will be gone permanently. This is only used before deserialization. **/
	public void clearClaims() {
		this.claims.clear();
	}

}
