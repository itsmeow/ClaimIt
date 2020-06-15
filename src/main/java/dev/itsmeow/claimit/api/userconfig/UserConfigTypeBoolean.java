package dev.itsmeow.claimit.api.userconfig;

import dev.itsmeow.claimit.api.util.nbt.NBTDeserializer;
import dev.itsmeow.claimit.api.util.nbt.NBTSerializer;

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
