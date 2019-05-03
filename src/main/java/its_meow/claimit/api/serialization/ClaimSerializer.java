package its_meow.claimit.api.serialization;

import its_meow.claimit.api.ClaimItAPI;

public class ClaimSerializer extends BaseSaveData {
    
	private static final String DATA_NAME = ClaimItAPI.MOD_ID + "_ClaimsData";
	
	public ClaimSerializer() {
		super(DATA_NAME);
	}
	
	public ClaimSerializer(String s) {
	    super(s);
	}
	
	public static ClaimSerializer get() {
		return BaseSaveData.get(ClaimSerializer.class, DATA_NAME);
	}
}