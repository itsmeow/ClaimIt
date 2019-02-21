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
	
	private Map<UUID, Set<UserConfigBasic>> configs = new HashMap<UUID, Set<UserConfigBasic>>(); 
	
	public Set<UserConfigBasic> getUserConfigs(UUID uuid) {
		if(!configs.containsKey(uuid)) {
			configs.put(uuid, new HashSet<UserConfigBasic>());
		}
		return configs.get(uuid);
	}
	
	@Nullable
	public UserConfigBasic getUserConfig(UUID uuid, UserConfig config) {
		Set<UserConfigBasic> configs = this.getUserConfigs(uuid);
		for(UserConfigBasic basic : configs) {
			if(config.parsedName.equals(basic.parsedName)) {
				return basic;
			}
		}
		return null;
	}
	
	
	
}
