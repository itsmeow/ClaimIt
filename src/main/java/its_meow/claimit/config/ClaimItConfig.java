package its_meow.claimit.config;

import its_meow.claimit.ClaimIt;
import net.minecraftforge.common.config.Config;

@Config(modid = ClaimIt.MOD_ID)
public class ClaimItConfig {
    
    @Config.Comment("Disables the ability to have any PVP in claims.")
    public static boolean forceNoPVPInClaim = false;
    
    @Config.Comment("Put here the item ID that you wish to use for claiming.")
    public static String claim_create_item = "minecraft:shears";
    
    @Config.Comment("Should match the display name of the claiming item, this is what is shown to users in the base command menu.")
    public static String claim_create_item_display = "Shears";
    
    @Config.RangeInt(min = 4, max = Integer.MAX_VALUE)
    @Config.Comment("The default maximum area a claim can be for non-admins, in square blocks. Default 40,000 sq blocks = 200 blocks x 200 blocks. This can be increased and decreased via the claimblocks command.")
    public static int default_claim_max_area = 40_000;
    
}