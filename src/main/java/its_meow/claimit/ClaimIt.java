package its_meow.claimit;

import java.util.HashMap;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.command.CommandClaimIt;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.userconfig.UserConfigManager;
import its_meow.claimit.userconfig.UserConfigTypeRegistry;
import its_meow.claimit.userconfig.UserConfigs;
import its_meow.claimit.util.command.ConfirmationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
@Mod(modid = ClaimIt.MOD_ID, name = ClaimIt.NAME, version = ClaimIt.VERSION, acceptedMinecraftVersions = ClaimIt.acceptedMCV, acceptableRemoteVersions = "*", dependencies = "after-required:claimitapi")
public class ClaimIt {
    
    public static final String MOD_ID = "claimit";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "ClaimIt";
    public static final String acceptedMCV = ClaimItAPI.acceptedMCV;
    
	@Instance(ClaimIt.MOD_ID)
	public static ClaimIt mod;
	
	public static Item claiming_item = null;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	    UserConfigs.register();
	}
	
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        UserConfigManager.deserialize();
        ConfirmationManager.getManager().removeAllConfirms();
        event.registerServerCommand(new CommandClaimIt());
        claiming_item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ClaimItConfig.claim_create_item));
    }
    
    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        UserConfigManager.serialize();
    }
    
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save e) {
        UserConfigManager.serialize();
    }
    
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
