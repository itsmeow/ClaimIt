package its_meow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import its_meow.claimit.api.claim.UserDataSerializer;
import net.minecraft.nbt.NBTTagCompound;

public class UserConfigManager {
	
	private static UserConfigManager instance = null;
	
	private UserConfigManager() {}

	public static UserConfigManager getManager() {
		if(instance == null) {
			instance = new UserConfigManager();
		}

		return instance;
	}
	
	private Map<UUID, Map<String, Pair<UserConfig<?>, ?>>> configs = new HashMap<UUID, Map<String, Pair<UserConfig<?>, ?>>>(); 
	
	public <T> void set(UUID uuid, UserConfig<T> config, T value) {
		Map<String, Pair<UserConfig<?>, ?>> usr = configs.get(uuid);
		if(usr == null) {
			configs.put(uuid, new HashMap<String, Pair<UserConfig<?>, ?>>());
			usr = configs.get(uuid);
		}
		Pair<UserConfig<?>, ?> pair = Pair.of(config, value);
		usr.put(config.parsedName, pair);
	}
	
	public Boolean get(UUID uuid, UserConfigBoolean config) {
		Map<String, Pair<UserConfig<?>, ?>> usr = configs.get(uuid);
		if(usr == null) {
			return config.defaultValue;
		}
		Pair<UserConfig<?>, ?> value = configs.get(uuid).get(config.parsedName);
		if(value == null || value.getLeft() == null || value.getRight() == null) {
			return config.defaultValue;
		}
		if(value.getLeft() instanceof UserConfigBoolean && value.getLeft() == config) {
			return (Boolean) value.getRight();
		}
		return config.defaultValue;
	}
	
	public Float get(UUID uuid, UserConfigFloat config) {
		Map<String, Pair<UserConfig<?>, ?>> usr = configs.get(uuid);
		if(usr == null) {
			return config.defaultValue;
		}
		Pair<UserConfig<?>, ?> value = configs.get(uuid).get(config.parsedName);
		if(value == null || value.getLeft() == null || value.getRight() == null) {
			return config.defaultValue;
		}
		if(value.getLeft() instanceof UserConfigFloat && value.getLeft() == config) {
			return (Float) value.getRight();
		}
		return config.defaultValue;
	}
	
	public String get(UUID uuid, UserConfigString config) {
		Map<String, Pair<UserConfig<?>, ?>> usr = configs.get(uuid);
		if(usr == null) {
			return config.defaultValue;
		}
		Pair<UserConfig<?>, ?> value = configs.get(uuid).get(config.parsedName);
		if(value == null || value.getLeft() == null || value.getRight() == null) {
			return config.defaultValue;
		}
		if(value.getLeft() instanceof UserConfigString && value.getLeft() == config) {
			return (String) value.getRight();
		}
		return config.defaultValue;
	}
	
	public void serialize() {
		NBTTagCompound data = UserDataSerializer.get().data;
		for(UUID uuid : configs.keySet()) {
			NBTTagCompound configData = new NBTTagCompound();
			for(String str : configs.get(uuid).keySet()) {
				Pair<UserConfig<?>, ?> pair = configs.get(uuid).get(str);
				if(pair.getLeft() instanceof UserConfigBoolean) {
					configData.setBoolean(str, (Boolean) pair.getRight());
				}
				if(pair.getLeft() instanceof UserConfigFloat) {
					configData.setFloat(str, (Float) pair.getRight());
				}
				if(pair.getLeft() instanceof UserConfigString) {
					configData.setString(str, (String) pair.getRight());
				}
			}
			data.setTag(uuid.toString(), configData);
		}
	}
	
	public void deserialize() {
		NBTTagCompound data = UserDataSerializer.get().data;
		for(String uuidStr : data.getKeySet()) {
			UUID uuid = UUID.fromString(uuidStr);
			configs.put(uuid, new HashMap<String, Pair<UserConfig<?>, ?>>());
			for(String parsedName : data.getCompoundTag(uuidStr).getKeySet()) {
				UserConfig<?> config = UserConfigRegistry.getConfig(parsedName);
				Object value = config.defaultValue;
				if(config instanceof UserConfigBoolean) {
					value = data.getCompoundTag(uuidStr).getBoolean(parsedName);
				}
				if(config instanceof UserConfigFloat) {
					value = data.getCompoundTag(uuidStr).getFloat(parsedName);
				}
				if(config instanceof UserConfigString) {
					value = data.getCompoundTag(uuidStr).getString(parsedName);
				}
				configs.get(uuid).put(parsedName, Pair.of(config, value));
			}
		}
	}
	
	
	
}
