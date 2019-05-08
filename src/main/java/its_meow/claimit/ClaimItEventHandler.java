package its_meow.claimit;

import java.util.HashMap;

import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.userconfig.UserConfigs;
import its_meow.claimit.util.userconfig.UserConfigTypeRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class ClaimItEventHandler {
    
    private static HashMap<EntityPlayer, BlockPos> lastMsPos = new HashMap<EntityPlayer, BlockPos>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent e) {
        ClaimManager mgr = ClaimManager.getManager();
        if(!e.player.world.isRemote && e.player.ticksExisted % 5 == 0) {
            if(e.player.ticksExisted % 2500 == 0) {
                lastMsPos.clear();
            }
            if(lastMsPos.get(e.player) == null) {
                lastMsPos.put(e.player, e.player.getPosition());
            }
            BlockPos pos = e.player.getPosition();
            if(mgr.isBlockInAnyClaim(e.player.world, lastMsPos.get(e.player))) {
                if(!mgr.isBlockInAnyClaim(e.player.world, pos)) {
                    Boolean value = UserConfigTypeRegistry.BOOLEAN.storage.getValueFor(UserConfigs.EXIT_MESSAGE, e.player.getGameProfile().getId());
                    if(value == null || value) {
                        e.player.sendStatusMessage(new TextComponentString(TextFormatting.GOLD + "You are no longer in a claimed area."), true);
                    }
                }
            } else {
                if(mgr.isBlockInAnyClaim(e.player.world, pos)) {
                    Boolean value = UserConfigTypeRegistry.BOOLEAN.storage.getValueFor(UserConfigs.ENTRY_MESSAGE, e.player.getGameProfile().getId());
                    if((value == null || value)) {
                        e.player.sendStatusMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "You are now in a claimed area."), true);
                    }
                }
            }
            lastMsPos.put(e.player, pos);
        }
    }
    
}
