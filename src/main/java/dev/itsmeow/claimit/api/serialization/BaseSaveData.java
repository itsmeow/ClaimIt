package dev.itsmeow.claimit.api.serialization;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public abstract class BaseSaveData extends WorldSavedData {
    
    public NBTTagCompound data = new NBTTagCompound();
    
    public BaseSaveData(String name) {
        super(name);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        data = nbt;
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = data;
        return compound;
    }
    
}