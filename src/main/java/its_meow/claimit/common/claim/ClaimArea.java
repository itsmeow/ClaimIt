package its_meow.claimit.common.claim;

import java.util.UUID;

import javax.annotation.Nullable;

import its_meow.claimit.common.item.ItemClaimInfoTool;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ClaimArea {

	private int posX;
	private int posZ;
	private final int dimID;
	private int sideLengthX;
	private int sideLengthZ;
	private UUID ownerUUID;
	private UUID ownerUUIDOffline;
	private String name;

	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, EntityPlayer player) {
		this.dimID = dimID;
		this.posX = posX;
		this.posZ = posZ;
		this.sideLengthX = sideLengthX;
		this.sideLengthZ = sideLengthZ;
		this.ownerUUID = EntityPlayer.getUUID(player.getGameProfile());
		this.ownerUUIDOffline = EntityPlayer.getOfflineUUID(player.getName());
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
	}
	
	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, UUID ownerUUID) {
		this.dimID = dimID;
		this.posX = posX;
		this.posZ = posZ;
		this.sideLengthX = sideLengthX;
		this.sideLengthZ = sideLengthZ;
		this.ownerUUID = ownerUUID;
		String ownerName = ItemClaimInfoTool.getPlayerName(ownerUUID.toString());
		if(ownerName == null) {
			ownerName = DimensionManager.getWorld(dimID).getMinecraftServer().getPlayerProfileCache().getProfileByUUID(ownerUUID).getName();
			this.ownerUUIDOffline = EntityPlayer.getOfflineUUID(ownerName);
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
	}
	
	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, UUID ownerUUID, UUID ownerUUIDOffline) {
		this.dimID = dimID;
		this.posX = posX;
		this.posZ = posZ;
		this.sideLengthX = sideLengthX;
		this.sideLengthZ = sideLengthZ;
		this.ownerUUID = ownerUUID;
		this.ownerUUIDOffline = ownerUUIDOffline;
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
	}
	
	private void updateName() {
		this.name = ownerUUID.toString() + dimID + posX + posZ + sideLengthX + sideLengthZ;
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

}
