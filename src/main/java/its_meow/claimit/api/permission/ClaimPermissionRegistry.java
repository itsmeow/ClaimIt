package its_meow.claimit.api.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ClaimPermissionRegistry {

	private static Map<String, ClaimPermissionMember> memberPermissions = new HashMap<String, ClaimPermissionMember>();
	private static Map<String, ClaimPermissionToggle> togglePermissions = new HashMap<String, ClaimPermissionToggle>();
	private static Map<ClaimPermissionMember, ClaimPermissionToggle> memberToggleMap = new HashMap<ClaimPermissionMember, ClaimPermissionToggle>();
	
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
	
	/** Add a member permission with a corresponding toggle to override it.
	 * @param permission - The member permission to add 
	 * @param toggle - The toggle permission that overrides this if true **/
	public static void addPermission(ClaimPermissionMember permission, ClaimPermissionToggle toggle) {
		addPermission(permission);
		addPermission(toggle);
		memberToggleMap.put(permission, toggle);
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
	@Nullable
	public static final ClaimPermissionMember getPermissionMember(String name) {
		return memberPermissions.get(name);
	}
	
	/** @param name - The name of the permission to get
	 *  @return The toggle permission with this name or null if no such permission **/
	@Nullable
	public static final ClaimPermissionToggle getPermissionToggle(String name) {
		return togglePermissions.get(name);
	}
	
	/** @param permission - The member permission to get override toggle for
	 *  @return A toggle override for this permission or null if none exists.
	 **/
	@Nullable
	public static final ClaimPermissionToggle getToggleFor(ClaimPermissionMember permission) {
		return memberToggleMap.get(permission);
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
