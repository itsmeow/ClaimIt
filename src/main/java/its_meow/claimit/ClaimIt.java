package its_meow.claimit;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.command.CommandClaimIt;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.userconfig.UserConfigs;
import its_meow.claimit.util.ConfirmationManager;
import its_meow.claimit.util.userconfig.UserConfigManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    
}
