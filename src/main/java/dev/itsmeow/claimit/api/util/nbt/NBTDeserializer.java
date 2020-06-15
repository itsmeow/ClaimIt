package dev.itsmeow.claimit.api.util.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTDeserializer<T> {
    
    /**
     * Reads a value T from compound with key
     * @param compound - The compound to read form
     * @param key - They key that will be read on the compound
     * @return The value that is mapped to key on compound
     */
    T deserialize(NBTTagCompound compound, String key);
    
}
