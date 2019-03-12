package its_meow.claimit.api.serialization;

import its_meow.claimit.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class ClaimSerializer extends WorldSavedData {
	private static final String DATA_NAME = Ref.MOD_ID + "_ClaimsData";
	public NBTTagCompound data = new NBTTagCompound();
	
	public ClaimSerializer() {
		super(DATA_NAME);
	}
	
	public ClaimSerializer(String s) {
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
	
	public static ClaimSerializer get() {
		World world = DimensionManager.getWorld(0);
		ClaimSerializer save = (ClaimSerializer) world.getMapStorage().getOrLoadData(ClaimSerializer.class, DATA_NAME);
		if(save == null) {
			save = new ClaimSerializer();
			world.getMapStorage().setData(DATA_NAME, save);
		}
		return save;
	}
}