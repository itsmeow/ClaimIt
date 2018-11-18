package its_meow.claimit.common.item;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.common.claim.ClaimArea;
import its_meow.claimit.common.claim.ClaimManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemClaimTool extends Item {

	public ItemClaimTool() {
		this.setMaxStackSize(1);
		this.setCreativeTab(ClaimIt.tab);
		this.setRegistryName("claimtool");
		this.setUnlocalizedName("claimtool");
	}







	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound data = stack.getTagCompound();
		if(data == null) {
			NBTTagCompound newTag = new NBTTagCompound();
			data = newTag;
			stack.setTagCompound(newTag);
		}
		boolean isInClaim = ClaimManager.getManager().isBlockInAnyClaim(pos, worldIn);
		System.out.println("Overlaps: " + isInClaim);
		if(!isInClaim) {
			int[] posArray = {pos.getX(), pos.getZ()};
			if(data.hasKey("Corner1")) {
				if(!worldIn.isRemote)
					player.sendMessage(new TextComponentString("Added corner 2 at " + posArray[0] + ", " + posArray[1]));
				int[] corner1 = data.getIntArray("Corner1");
				int[] corner2 = posArray;
				BlockPos c1 = new BlockPos(corner1[0], 0, corner1[1]);
				BlockPos c2 = new BlockPos(corner2[0], 0, corner2[1]);
				/* Not needed due to ClaimArea constructor
					if(c1.subtract(c2).getX() < 0 && c1.subtract(c2).getY() < 0) {
						BlockPos c = c1; // Swap values to make c1 the proper corner
						c1 = c2;
						c2 = c;
					}*/
				BlockPos sideL = c2.subtract(c1); // Subtract to get side lengths
				// Claim corners are automatically corrected to proper values by constructor
				ClaimArea newClaim;
				newClaim = new ClaimArea(player.dimension, c1.getX(), c1.getZ(), sideL.getX(), sideL.getZ(), player);
				boolean didClaim = ClaimManager.getManager().addClaim(newClaim); // Add claim
				if(!worldIn.isRemote)
					player.sendMessage(new TextComponentString(didClaim ? "Claim added successfully!" : "This claim overlaps another claim!"));
				// Remove data so a new claim can be made.
				data.removeTag("Corner1");
				return didClaim ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
			} else {
				data.setIntArray("Corner1", posArray);
				if(!worldIn.isRemote)
					player.sendMessage(new TextComponentString("Added corner 1 at " + posArray[0] + ", " + posArray[1]));
			}
			return EnumActionResult.SUCCESS;
		} else {
			if(!worldIn.isRemote) {
				data.removeTag("Corner1");
				player.sendMessage(new TextComponentString("You cannot set a corner inside an existing claim!"));
			}
		}
		return EnumActionResult.FAIL;
	}



}
