package its_meow.claimit.serialization;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.serialization.BaseSaveData;

public class ClaimItGlobalDataSerializer extends BaseSaveData {
    
    private static final String DATA_NAME = ClaimIt.MOD_ID + "_UserData";
    
    public ClaimItGlobalDataSerializer() {
        super(DATA_NAME);
    }
    
    public ClaimItGlobalDataSerializer(String s) {
        super(s);
    }
    
    public static ClaimItGlobalDataSerializer get() {
        return BaseSaveData.get(ClaimItGlobalDataSerializer.class, DATA_NAME);
    }
}