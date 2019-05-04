package its_meow.claimit.api.serialization;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

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
    
    @SuppressWarnings("unchecked")
    protected static <T extends BaseSaveData>T get(Class<T> clazz, String DATA_NAME) {
        World world = DimensionManager.getWorld(0);
        BaseSaveData save = (BaseSaveData) world.getMapStorage().getOrLoadData(clazz, DATA_NAME);
        if(save == null) {
            try {
                save = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            world.getMapStorage().setData(DATA_NAME, save);
        }
        if(clazz.isAssignableFrom(save.getClass())) {
            return (T) save;
        } else {
            return null;
        }
    }
    
}