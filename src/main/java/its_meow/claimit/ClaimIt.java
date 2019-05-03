package its_meow.claimit;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.command.CommandClaimIt;
import its_meow.claimit.userconfig.UserConfigs;
import its_meow.claimit.util.ConfirmationManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ClaimIt.MOD_ID, name = ClaimIt.NAME, version = ClaimIt.VERSION, acceptedMinecraftVersions = ClaimIt.acceptedMCV, updateJSON = ClaimIt.updateJSON, acceptableRemoteVersions = "*")
public class ClaimIt {
    
    public static final String MOD_ID = "claimit";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "ClaimIt";
    public static final String acceptedMCV = ClaimItAPI.acceptedMCV;
    public static final String updateJSON = ClaimItAPI.updateJSON;
    
	@Instance(ClaimIt.MOD_ID)
	public static ClaimIt mod;

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        ConfirmationManager.getManager().removeAllConfirms();
        event.registerServerCommand(new CommandClaimIt());
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        UserConfigs.register();
    }

}
