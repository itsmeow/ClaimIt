package its_meow.claimit.userconfig;

import its_meow.claimit.util.userconfig.UserConfigTypeRegistry;
import its_meow.claimit.util.userconfig.UserConfigType.UserConfig;

public class UserConfigs {
	
	public static final UserConfig<Boolean> ENTRY_MESSAGE = new UserConfig<Boolean>("entry_message", true, "Enable to show chat messages upon entry of claims.");
	public static final UserConfig<Boolean> EXIT_MESSAGE = new UserConfig<Boolean>("exit_message", true, "Enable to show chat messages upon exit of claims.");
	
	public static void register() {
		UserConfigTypeRegistry.BOOLEAN.addConfig(ENTRY_MESSAGE);
		UserConfigTypeRegistry.BOOLEAN.addConfig(EXIT_MESSAGE);
		UserConfigTypeRegistry.FLOAT.addConfig(new UserConfig<Float>("test_float", 32.5F, "Testing float!"));
		UserConfigTypeRegistry.STRING.addConfig(new UserConfig<String>("test_string_1", "yeet!", "Testing float!"));
	}
	
}
