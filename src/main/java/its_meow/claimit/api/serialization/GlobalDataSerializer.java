package its_meow.claimit.api.serialization;

import its_meow.claimit.api.ClaimItAPI;

public class GlobalDataSerializer extends BaseSaveData {

    private static final String DATA_NAME = ClaimItAPI.MOD_ID + "_GlobalData";

    public GlobalDataSerializer() {
        super(DATA_NAME);
    }

    public GlobalDataSerializer(String s) {
        super(s);
    }

    public static GlobalDataSerializer get() {
        return BaseSaveData.get(GlobalDataSerializer.class, DATA_NAME);
    }
}