package its_meow.claimit.common.claim;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BreakEventHandler {
	
	@SubscribeEvent
	public void onBlockBroken(BreakEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getPlayer();
			// First make sure online UUID doesn't match
			if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
				// If online UUID doesn't match then make sure offline doesn't either
				if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
					e.setCanceled(true);
				}
			}
		}
	}
	
}
