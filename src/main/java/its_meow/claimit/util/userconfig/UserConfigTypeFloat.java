package its_meow.claimit.util.userconfig;

import its_meow.claimit.api.util.NBTDeserializer;
import its_meow.claimit.api.util.NBTSerializer;

public class UserConfigTypeFloat extends UserConfigType<Float> {

	public UserConfigTypeFloat() {
		super(Float.class);
	}

    @Override
    protected NBTSerializer<Float> getSerializer() {
        return (c, s, f) -> c.setFloat(s, (float) f);
    }

    @Override
    protected NBTDeserializer<Float> getDeserializer() {
        return (c, s) -> c.getFloat(s);
    }

    @Override
    public boolean isValidValue(String in) {
        try {
            Float.parseFloat(in);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Float fromString(String valueStr) {
        return Float.parseFloat(valueStr);
    }

}
