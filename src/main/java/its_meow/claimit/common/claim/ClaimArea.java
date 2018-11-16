package its_meow.claimit.common.claim;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ClaimArea {

	private int posX;
	private int posY;
	private final int dimID;
	private int sideLengthX;
	private int sideLengthY;
	private UUID ownerUUID;

	public ClaimArea(int dimID, int posX, int posY, int sideLengthX, int sideLengthY, String ownerName) {
		this.dimID = dimID;
		this.posX = posX;
		this.posY = posY;
		this.sideLengthX = sideLengthX;
		this.sideLengthY = sideLengthY;
		this.ownerUUID = EntityPlayer.getOfflineUUID(ownerName);
	}

	public ClaimArea(int dimID, int posX, int posY, int sideLengthX, int sideLengthY, EntityPlayer player) {
		this(dimID, posX, posY, sideLengthX, sideLengthY, player.getName());
	}
	
	/** Returns posX and posY as BlockPos **/
	public BlockPos getMainPosition() {
		return new BlockPos(posX, 0, posY);
	}
	
	
	
	@Nullable
	public BlockPos[] getTwoClaimCorners() {
		if(this.sideLengthX == 0 || this.sideLengthY == 0) {
			return null;
		}
		BlockPos[] corners = new BlockPos[2];
		corners[0] = this.getMainPosition();
		
		return corners;
	}

}
