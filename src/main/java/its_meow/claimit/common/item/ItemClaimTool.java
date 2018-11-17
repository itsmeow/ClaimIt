package its_meow.claimit.common.item;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.common.claim.ClaimManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
		if(!ClaimManager.getManager().isBlockInAnyClaim(pos, worldIn)) {
			
			return EnumActionResult.SUCCESS;
		}
		if(!worldIn.isRemote) {
			player.sendMessage(new TextComponentString("You cannot set a corner inside an existing claim!"));
		}
		return EnumActionResult.FAIL;
	}
	
	
	
}
