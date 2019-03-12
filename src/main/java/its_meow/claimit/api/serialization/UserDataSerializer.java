package its_meow.claimit.api.serialization;

import its_meow.claimit.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class UserDataSerializer extends WorldSavedData {
	private static final String DATA_NAME = Ref.MOD_ID + "_UsersData";
	public NBTTagCompound data = new NBTTagCompound();
	
	public UserDataSerializer() {
		super(DATA_NAME);
	}
	
	public UserDataSerializer(String s) {
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
	
	public static UserDataSerializer get() {
		World world = DimensionManager.getWorld(0);
		UserDataSerializer save = (UserDataSerializer) world.getMapStorage().getOrLoadData(UserDataSerializer.class, DATA_NAME);
		if(save == null) {
			save = new UserDataSerializer();
			world.getMapStorage().setData(DATA_NAME, save);
		}
		return save;
	}
}