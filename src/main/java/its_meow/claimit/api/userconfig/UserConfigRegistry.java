package its_meow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserConfigRegistry {
	
	private static Map<String, UserConfigHolder<?>> configs = new HashMap<String, UserConfigHolder<?>>();
	
	/** Add a member permission to the registry to be used in claims 
	 * @param config - The config to add
	 * @param type - Type of this config **/
	public static <T> void addConfig(Class<T> type, UserConfig<T> config) {
		
		if(configs.containsKey(config.parsedName)) {
			throw new RuntimeException("Identical config ID registered: " + config.parsedName);
		}
		configs.put(config.parsedName, new UserConfigHolder<T>(type, config));
	}
	
	/** @param Type of the configs to get 
	 *  @return The list of user configs with given type **/
	@SuppressWarnings("unchecked")
	public static final <T> Set<UserConfig<T>> getConfigs(Class<T> type) {
		Set<UserConfig<T>> set = new HashSet<UserConfig<T>>();
		for(UserConfigHolder<?> configHolder : configs.values()) {
			if(configHolder.type == type) {
				set.add((UserConfig<T>) configHolder.config);
			}
		}
		return set;
	}
	
	/** @param name - The name of the config to get
	 *  @param type - Class for the given type of config you want
	 *  @return The config with this name or null if no such config with that type **/
	@SuppressWarnings("unchecked")
	public static final <T> UserConfig<T> getConfig(Class<T> type, String name) {
		if(configs.get(name).type == type) {
			return (UserConfig<T>) configs.get(name).config;
		}
		return null;
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
