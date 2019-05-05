package its_meow.claimit.api;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.config.ClaimConfig;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissions;
import its_meow.claimit.api.userconfig.UserConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimItAPI.MOD_ID)
@Mod(modid = ClaimItAPI.MOD_ID, name = ClaimItAPI.NAME, version = ClaimItAPI.VERSION, acceptedMinecraftVersions = ClaimItAPI.acceptedMCV, acceptableRemoteVersions = "*")
public class ClaimItAPI {
    
    public static final String MOD_ID = "claimitapi";
    public static final String VERSION = "@APIVERSION@";
    public static final String NAME = "ClaimIt API";
    public static final String acceptedMCV = "[1.12,1.12.2]";
    
    @Instance(ClaimItAPI.MOD_ID) 
    public static ClaimItAPI mod;
    
    public static Configuration config;
    public static final Logger logger = LogManager.getLogger("claimitapi");

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        ClaimManager.getManager().clearAdmins();
        ClaimManager.getManager().deserialize(); // Clears claims list as well
        UserConfigManager.getManager().deserialize();
        GroupManager.deserialize();
    }
    
    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        ClaimManager.getManager().serialize();
        UserConfigManager.getManager().serialize();
        GroupManager.serialize();
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClaimPermissions.register();
        
        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "claimit_api.cfg")); 
        ClaimConfig.readConfig();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if(config.hasChanged()){
            config.save();
        }
    }
    
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save e) {
        if(!e.getWorld().isRemote) {
            ClaimManager.getManager().serialize();
            UserConfigManager.getManager().serialize();
            GroupManager.serialize();
        }
    }
    
}
