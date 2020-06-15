package dev.itsmeow.claimit.api.util.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTSerializer<T> {
    
    /**
     * A method that writes value to compound with name key
     * @param compound - The compound that will be written to
     * @param key - The key that will be written to
     * @param object - The value that will be written to key on the compound
     */
    void serialize(NBTTagCompound compound, String key, Object object);
    
}