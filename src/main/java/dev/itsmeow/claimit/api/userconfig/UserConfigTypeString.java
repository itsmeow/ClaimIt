package dev.itsmeow.claimit.api.userconfig;

import dev.itsmeow.claimit.api.util.nbt.NBTDeserializer;
import dev.itsmeow.claimit.api.util.nbt.NBTSerializer;

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
