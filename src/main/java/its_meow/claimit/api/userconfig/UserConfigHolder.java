package its_meow.claimit.api.userconfig;

public class UserConfigHolder<T> {
	
	Class<T> type;
	UserConfig<T> config;
	
	public UserConfigHolder(Class<T> type, UserConfig<T> config) {
		this.type = type;
		this.config = config;
	}

}
