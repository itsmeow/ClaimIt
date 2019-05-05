package its_meow.claimit.config;

import its_meow.claimit.ClaimIt;
import net.minecraftforge.common.config.Config;

@Config(modid = ClaimIt.MOD_ID)
public class ClaimItConfig {
    
    @Config.Comment("Disables the ability to have any PVP in claims.")
    public static boolean forceNoPVPInClaim = false;
    
}