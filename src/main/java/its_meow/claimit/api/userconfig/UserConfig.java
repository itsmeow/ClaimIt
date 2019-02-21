package its_meow.claimit.api.userconfig;

public class UserConfig {
	
	public final String defaultValue;
	public final String parsedName;
	public final String helpInfo;
	
	/** Creates a user configuration field. Register.
	 * @param parsedName - The name users will type for this configuration value
	 * @param defaultValue - The default value of this config. Of generic type.
	 * @param helpInfo - The info to display in the help entry for this config.
	 * **/
	public UserConfig(String parsedName, String defaultValue, String helpInfo) {
		this.parsedName = parsedName;
		this.helpInfo = helpInfo;
		this.defaultValue = defaultValue;
	}
}
