package its_meow.claimit.api.group;

public class GroupManager {
	
	private static GroupManager instance = null;

	private GroupManager() {}

	public static GroupManager getManager() {
		if(instance == null) {
			instance = new GroupManager();
		}

		return instance;
	}
	
}
