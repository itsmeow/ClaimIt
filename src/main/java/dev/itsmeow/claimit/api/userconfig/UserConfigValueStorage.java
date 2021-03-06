package dev.itsmeow.claimit.api.userconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import dev.itsmeow.claimit.api.userconfig.UserConfigType.UserConfig;
import dev.itsmeow.claimit.api.userconfig.UserConfigValueStorage.UserConfigTypeStorage.UserConfigUUIDValue;

public class UserConfigValueStorage<T1> {

    public final UserConfigTypeStorage<T1> storage = new UserConfigTypeStorage<T1>();
    
    @Nullable
    public T1 getValueFor(UserConfig<T1> config, UUID uuid) {
        if(storage.values.get(config) == null) {
            return null;
        }
        return storage.values.get(config).uuids.get(uuid);
    }

    public void setValue(UserConfig<T1> config, UUID uuid, T1 value) {
        storage.values.putIfAbsent(config, new UserConfigUUIDValue<T1>());
        storage.values.get(config).uuids.put(uuid, value);
    }

    public static class UserConfigTypeStorage<T1> {

        public final Map<UserConfig<T1>, UserConfigUUIDValue<T1>> values = new HashMap<UserConfig<T1>, UserConfigUUIDValue<T1>>();

        public static class UserConfigUUIDValue<T1> {

            public final Map<UUID, T1> uuids = new HashMap<UUID, T1>();

        }

    }

}