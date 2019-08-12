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
    
    @Config.RangeInt(min = 1)
    @Config.Comment("Sets the maximum time borders can be shown with /claimit showborders. Please note each second is around 12 to 30 packets from the server to each player in order to show borders, therefore it is limited.")
    public static int max_show_borders_seconds = 30;
    
    @Config.RangeInt(min = 0)
    @Config.Comment("Sets the cooldown in seconds between each use of show borders")
    public static int show_borders_cooldown = 60;
    
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    @Config.Comment("The period, in ticks (1/20 of a second), at which \"claim_blocks_accrual_amount\" rewards will be seperated by. 0 to disable.")
    public static int claim_blocks_accrual_period = 0;
    
    @Config.Comment("The amount of claim blocks to be rewarded to players every \"claim_blocks_accrual_period\" ticks")
    public static int claim_blocks_accrual_amount = 0;
    
}