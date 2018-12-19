package its_meow.claimit.common.item;

import java.util.UUID;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.common.claim.ClaimArea;
import its_meow.claimit.common.claim.ClaimManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
			if(ClaimManager.getManager().isBlockInAnyClaim(worldIn, pos)) {
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(worldIn, pos);
				BlockPos[] corners = claim.getTwoMainClaimCorners();
				UUID owner = claim.getOwner();
				String ownerName = ClaimManager.getPlayerName(owner.toString(), worldIn);
				if(ownerName == null) {
					ownerName = owner.toString();
				}
				int dim = claim.getDimensionID();
				
				sendMessage(player, "§l§n§9Information for claim owned by §a" + ownerName + "§9:");
				sendMessage(player, "§9Dimension: §5" + dim);
				sendMessage(player, "§9Area: §b" + (claim.getSideLengthX() + 1) + "§9x§b" + (claim.getSideLengthZ() + 1) + " §9(§b" + claim.getArea() + "§9) ");
				sendMessage(player, "§9Corner 1: §2" + (corners[0].getX()) + ", " + (corners[0].getZ()));
				sendMessage(player, "§9Corner 2: §2" + (corners[1].getX()) + ", " + (corners[1].getZ()));
			} else {
				sendMessage(player, "§cThe block you are looking at is not owned by anyone!");
			}
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	public static void sendMessage(EntityPlayer player, String message) {
		player.sendMessage(new TextComponentString(message));
	}

}
