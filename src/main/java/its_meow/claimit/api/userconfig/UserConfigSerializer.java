package its_meow.claimit.api.userconfig;

import its_meow.claimit.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class UserConfigSerializer extends WorldSavedData {
	private static final String DATA_NAME = Ref.MOD_ID + "_UserConfigData";
	public NBTTagCompound data = new NBTTagCompound();
	
	public UserConfigSerializer() {
		super(DATA_NAME);
	}
	
	public UserConfigSerializer(String s) {
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
	
	public static UserConfigSerializer get() {
		World world = DimensionManager.getWorld(0);
		UserConfigSerializer save = (UserConfigSerializer) world.getMapStorage().getOrLoadData(UserConfigSerializer.class, DATA_NAME);
		if(save == null) {
			save = new UserConfigSerializer();
			world.getMapStorage().setData(DATA_NAME, save);
		}
		return save;
	}
}