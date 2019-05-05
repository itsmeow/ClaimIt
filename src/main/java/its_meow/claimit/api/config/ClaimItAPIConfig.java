package its_meow.claimit.api.config;

import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import net.minecraftforge.common.config.Configuration;

public class ClaimItAPIConfig extends Configuration {

    public static void readConfig(Configuration cfg) {
        cfg.load();
        loadFields(cfg);
        if(cfg.hasChanged()) {
            cfg.save();
        }
    }

    public static void loadFields(Configuration cfg) {
        String claimPermissions = "claim_permissions";
        cfg.addCustomCategoryComment(claimPermissions, "Force specific permissions to not work");
        for(ClaimPermissionToggle toggle : ClaimPermissionRegistry.getTogglePermissions()) {
            boolean doForce = cfg.getBoolean("do_force_" + toggle.parsedName + "_value", claimPermissions, false, "Set true to force " + toggle.parsedName + " in claims to the value of 'force_" + toggle.parsedName + "_value'");
            boolean forceValue = cfg.getBoolean("force_" + toggle.parsedName + "_value", claimPermissions, true, "Set to whatever value you want this to be if 'do_force_" + toggle.parsedName + "_value' is true");
            toggle.setForceEnabled(doForce);
            toggle.setForceValue(forceValue);
        }
    }

}
