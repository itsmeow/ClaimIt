package its_meow.claimit.api.userconfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserConfigRegistry {
	
	private static Map<String, UserConfig> configs = new HashMap<String, UserConfig>();
	
	/** Add a member permission to the registry to be used in claims 
	 * @param permission - The member permission to add **/
	public static void addConfig(UserConfig config) {
		
		if(configs.containsKey(config.parsedName)) {
			throw new RuntimeException("Identical config ID registered: " + config.parsedName);
		}
		configs.put(config.parsedName, config);
	}
	
	/** @return The list of user configs **/
	public static final Collection<UserConfig> getConfigs() {
		return configs.values();
	}
	
	/** @param name - The name of the config to get
	 *  @return The config with this name or null if no such config **/
	public static final UserConfig getConfig(String name) {
		return configs.get(name);
	}
	
	/** @return A string containing the names of all member permissions separated by a space **/
	public static String getValidConfigList() {
		String validNames = "";
		for(String name : UserConfigRegistry.configs.keySet()) {
			validNames += name + " ";
		}
		return validNames;
	}
	
}
