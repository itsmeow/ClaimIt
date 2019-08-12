package its_meow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import its_meow.claimit.api.userconfig.UserConfigValueStorage.UserConfigTypeStorage.UserConfigUUIDValue;
import its_meow.claimit.api.util.nbt.NBTDeserializer;
import its_meow.claimit.api.util.nbt.NBTSerializer;

public abstract class UserConfigType<T> {
	
    public final UserConfigValueStorage<T> storage = new UserConfigValueStorage<T>();

    private final Map<String, UserConfig<T>> configs = new HashMap<String, UserConfig<T>>();
    
	public final Class<T> type;
	
	/** Creates a user configuration field. Register.
	 * @param type - The class that represents stored data
	 * **/
	public UserConfigType(Class<T> type) {
		this.type = type;
	}
    
    /** Add a member permission to the registry to be used in claims 
     * @param config - The config to add
     * @param type - Type of this config **/
    public void addConfig(UserConfig<T> config) {
        
        if(configs.containsKey(config.parsedName)) {
            throw new RuntimeException("Identical config ID registered: " + config.parsedName);
        }
        configs.put(config.parsedName, config);
    }
    
    /** @param name - The name of the config to get
     *  @param type - Class for the given type of config you want
     *  @return The config with this name or null if no such config with that type **/
    public final UserConfig<T> getConfig(String name) {
        return configs.get(name);
    }
    
    /** @return A string containing the names of all member permissions separated by a space **/
    public String getValidConfigList() {
        String validNames = "";
        for(String name : configs.keySet()) {
            validNames += name + " ";
        }
        return validNames;
    }
    
    @SuppressWarnings("unchecked")
    public void setValue(UserConfig<?> userConfig, UUID uuid, Object val) {
        storage.storage.values.putIfAbsent((UserConfig<T>) userConfig, new UserConfigUUIDValue<T>());
        storage.storage.values.get(userConfig).uuids.put(uuid, this.fromString(val.toString()));
    }

    public ImmutableMap<String, UserConfig<T>> getConfigs() {
        return ImmutableMap.copyOf(configs);
    }
    
    /**
     * @return A method reference or function that writes to NBTTagCompound a tag with key String, and value T
     */
    protected abstract NBTSerializer<T> getSerializer();
    
    /**
     * @return A method reference or function that reads from NBTTagCompound a tag with key String, and returns value of type T
     */
    protected abstract NBTDeserializer<T> getDeserializer();
    
    /**
     * @return True if input string is an acceptable value for type T
     */
    public abstract boolean isValidValue(String in);
    
    /**
     * Should be preceded by a check for isValidValue usually, so assume proper input.
     * @return The proper value for T, given a string representing T.
     */
    public abstract T fromString(String valueStr);
    
    public static class UserConfig<T> {
        
        public final String parsedName;
        public final T defaultValue;
        public final String helpInfo;
        
        public UserConfig(String parsedName, T defaultValue, String helpInfo) {
            this.parsedName = parsedName;
            this.defaultValue = defaultValue;
            this.helpInfo = helpInfo;
        }
        
    }

}
