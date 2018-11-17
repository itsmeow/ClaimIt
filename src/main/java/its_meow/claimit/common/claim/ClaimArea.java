package its_meow.claimit.common.claim;

import java.util.UUID;

import javax.annotation.Nullable;

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

	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, String ownerName) {
		this.dimID = dimID;
		this.posX = posX;
		this.posZ = posZ;
		this.sideLengthX = sideLengthX;
		this.sideLengthZ = sideLengthZ;
		this.ownerUUID = EntityPlayer.getOfflineUUID(ownerName);
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
	}

	public ClaimArea(int dimID, int posX, int posZ, int sideLengthX, int sideLengthZ, EntityPlayer player) {
		this(dimID, posX, posZ, sideLengthX, sideLengthZ, player.getName());
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
	
	@Nullable
	public UUID getOwner() {
		return this.ownerUUID;
	}
	
	public int getDimensionID() {
		return dimID;
	}
	
	public World getWorld() {
		return DimensionManager.getWorld(this.dimID);
	}

}
