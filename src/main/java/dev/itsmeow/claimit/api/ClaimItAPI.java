package dev.itsmeow.claimit.api;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.itsmeow.claimit.api.claim.ClaimManager;
import dev.itsmeow.claimit.api.config.ClaimItAPIConfig;
import dev.itsmeow.claimit.api.group.GroupManager;
import dev.itsmeow.claimit.api.permission.ClaimPermissions;
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
        ClaimManager.getManager().deserialize(); // Clears claims list as well
        GroupManager.deserialize();
    }
    
    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        ClaimManager.getManager().serialize();
        GroupManager.serialize();
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClaimPermissions.register();
        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "claimit_api.cfg")); 
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        ClaimItAPIConfig.readConfig(config);
        if(config.hasChanged()){
            config.save();
        }
    }
    
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save e) {
        if(!e.getWorld().isRemote) {
            ClaimManager.getManager().serialize();
            GroupManager.serialize();
        }
    }
    
}
