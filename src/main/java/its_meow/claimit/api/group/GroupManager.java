package its_meow.claimit.api.group;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

public class GroupManager {

	private static GroupManager instance = null;

	private GroupManager() {}

	public static GroupManager getManager() {
		if(instance == null) {
			instance = new GroupManager();
		}

		return instance;
	}

	private static HashMap<String, Group> groups = new HashMap<String, Group>();

	public static boolean addGroup(Group group) {
		if(groups.containsKey(group.getName())) {
			return false;
		}
		groups.put(group.getName(), group);
		return true;
	}
	
	@Nullable
	public static Group getGroup(String name) {
		return groups.get(name);
	}
	
	public static boolean renameGroup(String name, String newName) {
	    if(!groups.containsKey(name) || groups.containsKey(newName)) {
	        return false;
	    }
	    Group group = groups.get(name);
	    groups.remove(name);
	    groups.put(newName, group);
	    group.name = newName;
	    return true;
	}
	
	public static ImmutableSet<Group> getGroups() {
	    return ImmutableSet.copyOf(groups.values());
	}

}