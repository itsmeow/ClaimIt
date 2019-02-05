package its_meow.claimit.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClaimPermissionRegistry {

	private static Map<String, ClaimPermissionMember> memberPermissions = new HashMap<String, ClaimPermissionMember>();
	private static Map<String, ClaimPermissionToggle> togglePermissions = new HashMap<String, ClaimPermissionToggle>();
	
	/** Add a member permission to the registry to be used in claims 
	 * @param permission - The member permission to add **/
	public static void addPermission(ClaimPermissionMember permission) {
		
		if(memberPermissions.containsKey(permission.parsedName)) {
			throw new RuntimeException("Identical member permission ID registered: " + permission.parsedName);
		}
		memberPermissions.put(permission.parsedName, permission);
	}
	
	/** Add a toggle permission to the registry to be used in claims 
	 * @param permission - The toggle permission to add **/
	public static void addPermission(ClaimPermissionToggle permission) {
		if(togglePermissions.containsKey(permission.parsedName)) {
			throw new RuntimeException("Identical toggle permission ID registered: " + permission.parsedName);
		}
		togglePermissions.put(permission.parsedName, permission);
	}
	
	/** @return The list of member permissions **/
	public static final Collection<ClaimPermissionMember> getMemberPermissions() {
		return memberPermissions.values();
	}
	
	/** @return The list of toggle permissions **/
	public static final Collection<ClaimPermissionToggle> getTogglePermissions() {
		return togglePermissions.values();
	}
	
	/** @param name - The name of the permission to get
	 *  @return The member permission with this name or null if no such permission **/
	public static final ClaimPermissionMember getPermissionMember(String name) {
		return memberPermissions.get(name);
	}
	
	/** @param name - The name of the permission to get
	 *  @return The toggle permission with this name or null if no such permission **/
	public static final ClaimPermissionToggle getPermissionToggle(String name) {
		return togglePermissions.get(name);
	}
	
	/** @return A string containing the names of all member permissions separated by a space **/
	public static String getValidPermissionListMember() {
		String validPerms = "";
		for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
			validPerms += perm.parsedName + " ";
		}
		return validPerms;
	}
	
	/** @return A string containing the names of all toggle permissions separated by a space **/
	public static String getValidPermissionListToggle() {
		String validPerms = "";
		for(ClaimPermissionToggle perm : ClaimPermissionRegistry.getTogglePermissions()) {
			validPerms += perm.parsedName + " ";
		}
		return validPerms;
	}
	
}
