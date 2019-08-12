# User Configs

## Types

A user config type is a set of rules for reading, serializing, and validating data of a specific type.

### Creating Types

In order to create a type, you first need a class that represents your data. In this case, we will be using `Integer` from `java.lang`.<br>
Now, make a class that extends `UserConfigType`. Since our class is `Integer`, we will extend `UserConfigType<Integer>`.<br>

Now, define your constructor and pass the class you are extending as an argument to the superconstructor.<br>

```java
public UserConfigTypeInteger() {
    super(Integer.class);
}
```

After that, you will need to write NBT serializers. This is so the data can persist.<br>
Note in the lambdas, the first argument is the tag compound provided for your type, and the second is a string provided for your config. You should NEVER write to any other string than the one provided in the second argument. If needed, make a tag compound and write multiple tags there.<br>
In the `NBTSerializer`, the third argument is the value that should be written, cast it to whatever your data type is.<br>

```java
@Override
protected NBTSerializer<Integer> getSerializer() {
	return (c, s, i) -> c.setInteger(s, (int) i);
}

@Override
protected NBTDeserializer<Integer> getDeserializer() {
	return (c, s) -> c.getInteger(s);
}
```

The final two are string parsing rules.<br>
`isValidValue` should return true if the argument String represents your data accurately.<br>

`fromString` should return your data type from a string, given `isValidValue` was true for this string.

```java
@Override
public boolean isValidValue(String in) {
	try {
		Integer.parseInt(in);
		return true;
	} catch(NumberFormatException e) {
		return false;
	}
}

@Override
public Integer fromString(String valueStr) {
	return Integer.parseInt(valueStr);
}
```


All together, your type should look like this:

```java
public class UserConfigTypeInteger extends UserConfigType<Integer> {

	 public UserConfigTypeInteger() {
		 super(Integer.class);
	 }

    @Override
    protected NBTSerializer<Integer> getSerializer() {
        return (c, s, i) -> c.setInteger(s, (int) i);
    }

    @Override
    protected NBTDeserializer<Integer> getDeserializer() {
        return (c, s) -> c.getInteger(s);
    }

    @Override
    public boolean isValidValue(String in) {
        try {
            Integer.parseInt(in);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Integer fromString(String valueStr) {
        return Integer.parseInt(valueStr);
    }

}
```

### Registering Types

In this example, we are registering a hypothetical type `UserConfigTypeInteger`.

```java
UserConfigTypeRegistry.addType(UserConfigTypeInteger.class, INTEGER);
```

`INTEGER` should be an instance of `UserConfigTypeInteger`. It is important that you store this instance somewhere so you can use it to register individual configs on.

## User Configs

User Configs are named configurable values attached to players with a default value and help information. They also have a data type, such as boolean or float.<br>

### Creating User Configs

Creating a User Config is simple, given a type.<br>
There are three built in types you can use, or you can use a custom one (see above).<br>
Builtin Types: `UserConfigTypeRegistry.BOOLEAN`, `UserConfigTypeRegistry.FLOAT`, and `UserConfigTypeRegistry.STRING`
To create an instance of a User Config, which can then be registered, you will need the data type of a config type. As an example: `Boolean` for `UserConfigTypeRegistry.BOOLEAN`.
Creating a new config looks like this:

```java
public static final UserConfig<Boolean> MY_CONFIG = new UserConfig<Boolean>("my_config", false, "The first argument is the config name. The second argument is the default value. This is what players will start with the value set as. This is the help information displayed for your config");
```

Now you'll need to register your config.

### Registering User Configs

Keep in mind the type you used for your config. You'll need to register the config to that type. Let's register our hypothetical `my_config` boolean.

```java
UserConfigTypeRegistry.BOOLEAN.addConfig(MY_CONFIG);
```

You can also use any custom type instance (such as the `INTEGER` example from the Types section), so long as you can access it. The only requirement is they have matching data types (`UserConfigType<Boolean>` and `UserConfig<Boolean>`)