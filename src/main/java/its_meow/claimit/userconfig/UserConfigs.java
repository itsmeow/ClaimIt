package its_meow.claimit.userconfig;

import its_meow.claimit.api.userconfig.UserConfigBoolean;
import its_meow.claimit.api.userconfig.UserConfigRegistry;

public class UserConfigs {
	
	public static final UserConfigBoolean ENTRY_MESSAGE = new UserConfigBoolean("entry_message", true, "Enable to show chat messages upon entry of claims.");
	public static final UserConfigBoolean EXIT_MESSAGE = new UserConfigBoolean("exit_message", true, "Enable to show chat messages upon exit of claims.");
	
	public static void register() {
		UserConfigRegistry.addConfig(ENTRY_MESSAGE);
		UserConfigRegistry.addConfig(EXIT_MESSAGE);
	}
	
}
