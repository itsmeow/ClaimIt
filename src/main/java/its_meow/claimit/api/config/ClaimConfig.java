package its_meow.claimit.api.config;

import its_meow.claimit.api.ClaimItAPI;
import net.minecraftforge.common.config.Configuration;

public class ClaimConfig extends Configuration {
	
	public static boolean allowDisableSpawns = true;
	public static boolean allowDisablePickup = true;
	public static boolean allowDisableDrop = true;
	public static boolean forceNoPVPInClaim = false;
	
	public static void readConfig() {
		Configuration cfg = ClaimItAPI.config;
		try {
			cfg.load();
			loadFields(cfg);
		} catch (Exception e1) {
			ClaimItAPI.logger.error("Failed loading ClaimIt configuration!", e1);
		} finally {
			if(cfg.hasChanged()) {
				cfg.save();
			}
		}
	}
	
	public static void loadFields(Configuration cfg) {
		String claimPermissions = "claim_permissions";
		cfg.addCustomCategoryComment(claimPermissions, "Force specific permissions to not work");
		allowDisableSpawns = cfg.getBoolean("allow_disable_spawns", claimPermissions, true, "Set to false to prevent users from disabling spawns");
		allowDisablePickup = cfg.getBoolean("allow_disable_pickup", claimPermissions, true, "Set to false to prevent users from disabling item pickup");
		allowDisableDrop = cfg.getBoolean("allow_disable_drop", claimPermissions, true, "Set to false to prevent users from disabling item drop");
		forceNoPVPInClaim = cfg.getBoolean("force_no_pvp", claimPermissions, false, "Disallows any PVP in any claim - even if users have permissions");
	}
	
}
