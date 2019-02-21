package its_meow.claimit.api.userconfig;

public class UserConfigBasic extends UserConfig {
	
	public String value;
	
	public UserConfigBasic(UserConfig config, String value) {
		super(config.parsedName, config.defaultValue, config.helpInfo);
		this.value = value;
	}
	
	public UserConfigBasic(UserConfig config) {
		super(config.parsedName, config.defaultValue, config.helpInfo);
		this.value = config.defaultValue;
	}
	
}
