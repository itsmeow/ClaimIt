package its_meow.claimit.api.userconfig;

import its_meow.claimit.api.util.nbt.NBTDeserializer;
import its_meow.claimit.api.util.nbt.NBTSerializer;

public class UserConfigTypeString extends UserConfigType<String> {

	public UserConfigTypeString() {
		super(String.class);
	}

    @Override
    protected NBTSerializer<String> getSerializer() {
        return (c, s, v) -> c.setString(s, (String) v);
    }

    @Override
    protected NBTDeserializer<String> getDeserializer() {
        return (c, s) -> c.getString(s);
    }

    @Override
    public boolean isValidValue(String in) {
        return true;
    }

    @Override
    public String fromString(String valueStr) {
        return valueStr;
    }

}
