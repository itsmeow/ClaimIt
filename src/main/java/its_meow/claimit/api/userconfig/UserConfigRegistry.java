package its_meow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.Map;

public class UserConfigRegistry {
	
	public static Map<String, UserConfig<?>> configs = new HashMap<String, UserConfig<?>>();
	
	/** Add a member permission to the registry to be used in claims 
	 * @param config - The config to add
	 * @param type - Type of this config **/
	public static <T> void addConfig(UserConfig<T> config) {
		
		if(configs.containsKey(config.parsedName)) {
			throw new RuntimeException("Identical config ID registered: " + config.parsedName);
		}
		configs.put(config.parsedName, config);
	}
	
	/** @param name - The name of the config to get
	 *  @param type - Class for the given type of config you want
	 *  @return The config with this name or null if no such config with that type **/
	@SuppressWarnings("unchecked")
	public static final <T> UserConfig<T> getConfig(String name) {
		return (UserConfig<T>) configs.get(name);
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
