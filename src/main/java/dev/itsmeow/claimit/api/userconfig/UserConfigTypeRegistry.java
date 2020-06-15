package dev.itsmeow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

public class UserConfigTypeRegistry {
	
    public static final UserConfigTypeBoolean BOOLEAN = new UserConfigTypeBoolean();
    public static final UserConfigTypeString STRING = new UserConfigTypeString();
    public static final UserConfigTypeFloat FLOAT = new UserConfigTypeFloat();
    
    public static final Map<Class<? extends UserConfigType<?>>, UserConfigType<?>> registryMap = new HashMap<Class<? extends UserConfigType<?>>, UserConfigType<?>>();
    
    public static <T1, T extends UserConfigType<T1>> void addType(Class<T> typeType, T type) {
        registryMap.put(typeType, type);
    }
    
    static {
        addType(UserConfigTypeBoolean.class, BOOLEAN);
        addType(UserConfigTypeFloat.class, FLOAT);
        addType(UserConfigTypeString.class, STRING);
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T1, T extends UserConfigType<T1>> T getRegistry(Class<T> type) {
        if(registryMap.get(type) != null && type.isAssignableFrom(registryMap.get(type).getClass())) {
            return (T) registryMap.get(type);
        }
        return null;
    }
    
    public static ImmutableMap<Class<? extends UserConfigType<?>>, UserConfigType<?>> getRegistries() {
        return ImmutableMap.copyOf(registryMap);
    }

}
