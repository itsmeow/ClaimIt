package its_meow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class UserConfigRegistry {
	
	@SuppressWarnings("rawtypes")
	private static Map<String, Pair<Class, UserConfig>> configs = new HashMap<String, Pair<Class, UserConfig>>();
	
	/** Add a member permission to the registry to be used in claims 
	 * @param permission - The member permission to add **/
	public static <T> void addConfig(Class<T> type, UserConfig<T> config) {
		
		if(configs.containsKey(config.parsedName)) {
			throw new RuntimeException("Identical config ID registered: " + config.parsedName);
		}
		configs.put(config.parsedName, Pair.of(type, config));
	}
	
	/** @return The list of user configs **/
	@SuppressWarnings("unchecked")
	public static final <T> Set<UserConfig<T>> getConfigs(Class<T> type) {
		Set<UserConfig<T>> set = new HashSet<UserConfig<T>>();
		for(Pair<Class, UserConfig> config : configs.values()) {
			if(config.getLeft() == type) {
				set.add(config.getRight());
			}
		}
		return set;
	}
	
	/** @param name - The name of the config to get
	 *  @return The config with this name or null if no such config **/
	@SuppressWarnings("unchecked")
	public static final <T> UserConfig<T> getConfig(Class<T> type, String name) {
		return configs.get(name).getRight();
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
