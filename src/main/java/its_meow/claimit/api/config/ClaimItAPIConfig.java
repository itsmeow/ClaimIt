package its_meow.claimit.api.config;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ClaimItAPI.MOD_ID, name = ClaimItAPI.MOD_ID + "-2")
@Mod.EventBusSubscriber(modid = ClaimItAPI.MOD_ID)
public class ClaimItAPIConfig extends Configuration {

    @Config.Comment("Enable or disable the entire subclaim system")
    public static boolean enable_subclaims = true;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent event) {
        if(event.getModID().equals(ClaimItAPI.MOD_ID)) {
            ConfigManager.sync(event.getModID(), Config.Type.INSTANCE);
        }
    }

    /* BEGIN LEGACY CONFIG */

    public static void readConfig(Configuration cfg) {
        cfg.load();
        loadFields(cfg);
        if(cfg.hasChanged()) {
            cfg.save();
        }
    }

    public static void loadFields(Configuration cfg) {
        String claimPermissions = "claim_permissions";
        cfg.addCustomCategoryComment(claimPermissions, "Configure toggle permissions and their values");
        for(ClaimPermissionToggle toggle : ClaimPermissionRegistry.getTogglePermissions()) {
            boolean doForce = cfg.getBoolean("do_force_" + toggle.parsedName + "_value", claimPermissions + "." + toggle.parsedName, false, "Set true to force " + toggle.parsedName + " in claims to the value of 'force_" + toggle.parsedName + "_value'");
            boolean forceValue = cfg.getBoolean("force_" + toggle.parsedName + "_value", claimPermissions + "." + toggle.parsedName, true, "Set to whatever value you want this to be if 'do_force_" + toggle.parsedName + "_value' is true");
            toggle.setForceEnabled(doForce);
            toggle.setForceValue(forceValue);
            boolean defaultValue = cfg.getBoolean("default_value", claimPermissions + "." + toggle.parsedName, toggle.getDefault(), "Sets the default value for this toggle in new claims and under the help information for this toggle.");
            toggle.setDefault(defaultValue);
        }
    }

}
