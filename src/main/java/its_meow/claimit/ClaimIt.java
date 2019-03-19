package its_meow.claimit;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.claim.ClaimPermissions;
import its_meow.claimit.command.CommandClaimIt;
import its_meow.claimit.config.ClaimConfig;
import its_meow.claimit.util.ConfirmationManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Ref.MOD_ID, name = Ref.NAME, version = Ref.VERSION, acceptedMinecraftVersions = Ref.acceptedMCV, updateJSON = Ref.updateJSON, acceptableRemoteVersions = "*")
public class ClaimIt {

	@Instance(Ref.MOD_ID) 
	public static ClaimIt mod;

	public static Logger logger;
	public static Configuration config;

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		ClaimManager.getManager().clearAdmins();
		ClaimManager.getManager().deserialize(); // Clears claims list as well
		ConfirmationManager.getManager().removeAllConfirms();
		event.registerServerCommand(new CommandClaimIt());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger("claimit");
		File directory = event.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "claimit.cfg")); 
		ClaimConfig.readConfig();
		
		ClaimPermissions.register();
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		if(config.hasChanged()){
			config.save();
		}
	}

}
