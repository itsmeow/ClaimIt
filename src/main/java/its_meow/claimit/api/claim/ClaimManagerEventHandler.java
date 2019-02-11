package its_meow.claimit.api.claim;

import its_meow.claimit.Ref;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Ref.MOD_ID)
public class ClaimManagerEventHandler {
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save e) {
		if(!e.getWorld().isRemote) {
			ClaimManager.getManager().serialize();
		}
	}
	
}
