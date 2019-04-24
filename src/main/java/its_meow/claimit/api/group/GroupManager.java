package its_meow.claimit.api.group;

import java.util.HashMap;

import javax.annotation.Nullable;

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
	//private static HashMap<UUID, HashSet<Group>> userGroups = new HashMap<UUID, HashSet<Group>>();
	//private static HashMap<ClaimArea, HashSet<Group>> claimGroups = new HashMap<ClaimArea, HashSet<Group>>();

	public static boolean addGroup(Group group) {
		if(groups.containsKey(group.getName())) {
			return false;
		}
		groups.put(group.getName(), group);
		/*for(UUID uuid : group.members) {
			if(userGroups.get(uuid) == null) {
				userGroups.put(uuid, new HashSet<Group>());
			}
			userGroups.get(uuid).add(group);
		}
		for(ClaimArea claim : group.claims) {
			if(claimGroups.get(claim) == null) {
				claimGroups.put(claim, new HashSet<Group>());
			}
			claimGroups.get(claim).add(group);
		}*/
		return true;
	}
	
	@Nullable
	public static Group getGroup(String name) {
		return groups.get(name);
	}

}