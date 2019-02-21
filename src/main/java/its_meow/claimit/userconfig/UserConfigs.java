package its_meow.claimit.userconfig;

import its_meow.claimit.api.userconfig.UserConfig;
import its_meow.claimit.api.userconfig.UserConfigRegistry;

public class UserConfigs {
	
	public static final UserConfig ENTRY_MESSAGE = new UserConfig("entry_message", "true", "Enable to show chat messages upon entry of claims.");
	public static final UserConfig EXIT_MESSAGE = new UserConfig("exit_message", "true", "Enable to show chat messages upon exit of claims.");
	
	public static void register() {
		UserConfigRegistry.addConfig(ENTRY_MESSAGE);
		UserConfigRegistry.addConfig(EXIT_MESSAGE);
	}
	
}
