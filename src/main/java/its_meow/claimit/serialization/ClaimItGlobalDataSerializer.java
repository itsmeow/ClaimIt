package its_meow.claimit.serialization;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.serialization.BaseSaveData;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ClaimItGlobalDataSerializer extends BaseSaveData {
    
    private static final String DATA_NAME = ClaimIt.MOD_ID + "_UserData";
    
    public ClaimItGlobalDataSerializer() {
        super(DATA_NAME);
    }
    
    public ClaimItGlobalDataSerializer(String s) {
        super(s);
    }
    
    public static ClaimItGlobalDataSerializer get() {
        World world = DimensionManager.getWorld(0);
        ClaimItGlobalDataSerializer save = (ClaimItGlobalDataSerializer) world.getMapStorage().getOrLoadData(ClaimItGlobalDataSerializer.class, DATA_NAME);
        if(save == null) {
            save = new ClaimItGlobalDataSerializer();
            world.getMapStorage().setData(DATA_NAME, save);
        }
        return save;
    }
}