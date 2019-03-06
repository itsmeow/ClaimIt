package its_meow.claimit.api.claim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import its_meow.claimit.api.userconfig.UserConfig;
import its_meow.claimit.api.userconfig.UserConfigBasic;

public class UserConfigManager {
	
	private static UserConfigManager instance = null;
	
	private UserConfigManager() {}

	public static UserConfigManager getManager() {
		if(instance == null) {
			instance = new UserConfigManager();
		}

		return instance;
	}
	
	private Map<UUID, Set<UserConfig<?>>> configs = new HashMap<UUID, Set<UserConfig<?>>>(); 
	
	public <T> Set<UserConfig<T>> getUserConfigs(UUID uuid) {
		if(!configs.containsKey(uuid)) {
			configs.put(uuid, new HashSet<UserConfig<?>>());
		}
		return configs.get(uuid);
	}
	
	@Nullable
	public <T> T getUserConfig(UUID uuid, UserConfig<T> config) {
		Set<UserConfig<T>> configs = this.getUserConfigs(uuid);
		for(UserConfig<T> config : configs) {
			if(config.parsedName.equals(config.parsedName)) {
				return config;
			}
		}
		return null;
	}
	
	
	
}
