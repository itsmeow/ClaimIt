package its_meow.claimit.api.serialization;

import its_meow.claimit.api.ClaimItAPI;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ClaimSerializer extends BaseSaveData {
    
	private static final String DATA_NAME = ClaimItAPI.MOD_ID + "_ClaimsData";
	
	public ClaimSerializer() {
		super(DATA_NAME);
	}
	
	public ClaimSerializer(String s) {
	    super(s);
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