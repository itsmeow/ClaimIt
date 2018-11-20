package its_meow.claimit.common.item;

import java.util.UUID;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.common.claim.ClaimArea;
import its_meow.claimit.common.claim.ClaimManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemClaimInfoTool extends Item {

	public ItemClaimInfoTool() {
		this.setMaxStackSize(1);
		this.setCreativeTab(ClaimIt.tab);
		this.setRegistryName("claiminfotool");
		this.setUnlocalizedName("claiminfotool");
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}
		
		IBlockState state = worldIn.getBlockState(pos);
		if(state.getBlock() != Blocks.AIR) {
			if(ClaimManager.getManager().isBlockInAnyClaim(pos, worldIn)) {
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(worldIn, pos);
				BlockPos[] corners = claim.getTwoMainClaimCorners();
				UUID owner = claim.getOwner();
				//worldIn.getMinecraftServer()
			}
		}
		
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
