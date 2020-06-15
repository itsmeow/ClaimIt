package dev.itsmeow.claimit.api.userconfig;

import java.util.UUID;

import javax.annotation.Nullable;

import dev.itsmeow.claimit.api.serialization.GlobalDataSerializer;
import dev.itsmeow.claimit.api.userconfig.UserConfigType.UserConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class UserConfigManager {

    @Nullable
    public static <T1, T extends UserConfigType<T1>> UserConfigValueStorage<T1> getStorage(Class<T> type) {
        return UserConfigTypeRegistry.getRegistry(type).storage;
    }

    public static void serialize() {
        NBTTagCompound data = GlobalDataSerializer.get().data;
        NBTTagCompound configsData = new NBTTagCompound();
        for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
            UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
            for(UserConfig<?> config : type.getConfigs().values()) {
                try {
                    type.storage.storage.values.get(config).uuids.forEach((uuid, value) -> {
                        if(!configsData.hasKey(uuid.toString())) {
                            configsData.setTag(uuid.toString(), new NBTTagCompound());
                        }
                        NBTTagCompound userTag = configsData.getCompoundTag(uuid.toString());
                        type.getSerializer().serialize(userTag, config.parsedName, type.type.cast(value));
                        configsData.setTag(uuid.toString(), userTag);
                    });
                } catch (NullPointerException e) {}
            }
        }
        data.setTag("USERCONFIG", configsData);
        WorldSavedData wsd = GlobalDataSerializer.get();
        wsd.markDirty();
    }

    public static void deserialize() {
        NBTTagCompound data = GlobalDataSerializer.get().data;
        NBTTagCompound configsData = data.getCompoundTag("USERCONFIG");
        for(String uuidStr : configsData.getKeySet()) {
            if(configsData.hasKey(uuidStr, Constants.NBT.TAG_COMPOUND)) {
                NBTTagCompound userTag = configsData.getCompoundTag(uuidStr);
                UUID uuid = UUID.fromString(uuidStr);
                for(String parseName : userTag.getKeySet()) {
                    for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                        UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                        UserConfig<?> config = type.getConfig(parseName);
                        if(config != null) {
                            Object val = type.getDeserializer().deserialize(userTag, config.parsedName);
                            type.setValue(config, uuid, val);
                        }
                    }
                }
            }
        }
    }

}
