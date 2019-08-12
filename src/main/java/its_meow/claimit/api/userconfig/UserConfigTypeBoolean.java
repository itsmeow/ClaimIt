package its_meow.claimit.api.userconfig;

import its_meow.claimit.api.util.nbt.NBTDeserializer;
import its_meow.claimit.api.util.nbt.NBTSerializer;

public class UserConfigTypeBoolean extends UserConfigType<Boolean> {

	public UserConfigTypeBoolean() {
		super(Boolean.class);
	}

    @Override
    protected NBTSerializer<Boolean> getSerializer() {
        return (c, s, b) -> c.setBoolean(s, (boolean) b);
    }

    @Override
    protected NBTDeserializer<Boolean> getDeserializer() {
        return (c, s) -> c.getBoolean(s);
    }

    @Override
    public boolean isValidValue(String in) {
        return in.equalsIgnoreCase("true") || in.equalsIgnoreCase("false");
    }

    @Override
    public Boolean fromString(String valueStr) {
        return Boolean.parseBoolean(valueStr);
    }

}
