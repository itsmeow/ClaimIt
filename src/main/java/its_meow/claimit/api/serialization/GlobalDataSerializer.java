package its_meow.claimit.api.serialization;

import its_meow.claimit.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class GlobalDataSerializer extends WorldSavedData {
	private static final String DATA_NAME = Ref.MOD_ID + "_GlobalData";
	public NBTTagCompound data = new NBTTagCompound();
	
	public GlobalDataSerializer() {
		super(DATA_NAME);
	}
	
	public GlobalDataSerializer(String s) {
		super(s);
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
	
	public static GlobalDataSerializer get() {
		World world = DimensionManager.getWorld(0);
		GlobalDataSerializer save = (GlobalDataSerializer) world.getMapStorage().getOrLoadData(GlobalDataSerializer.class, DATA_NAME);
		if(save == null) {
			save = new GlobalDataSerializer();
			world.getMapStorage().setData(DATA_NAME, save);
		}
		return save;
	}
}