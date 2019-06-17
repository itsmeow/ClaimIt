package its_meow.claimit.api.serialization;

import its_meow.claimit.api.ClaimItAPI;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class GlobalDataSerializer extends BaseSaveData {

    private static final String DATA_NAME = ClaimItAPI.MOD_ID + "_GlobalData";

    public GlobalDataSerializer() {
        super(DATA_NAME);
    }

    public GlobalDataSerializer(String s) {
        super(s);
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