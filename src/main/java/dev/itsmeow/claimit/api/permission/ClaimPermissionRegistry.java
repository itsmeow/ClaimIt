package dev.itsmeow.claimit.api.permission;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

public class ClaimPermissionRegistry {

	private static BiMap<String, ClaimPermissionMember> memberPermissions = HashBiMap.create();
	private static BiMap<String, ClaimPermissionToggle> togglePermissions = HashBiMap.create();
	private static Map<ClaimPermissionMember, ClaimPermissionToggle> memberToggleMap = new HashMap<ClaimPermissionMember, ClaimPermissionToggle>();
	
	/** Add a member permission to the registry to be used in claims 
	 * @param permission - The member permission to add **/
	public static void addPermission(ClaimPermissionMember permission) {
		if(memberPermissions.containsKey(permission.parsedName)) {
			throw new RuntimeException("Identical member permission ID registered: " + permission.parsedName);
		}
		if(memberPermissions.containsValue(permission)) {
            throw new RuntimeException("Indentical member permissions registered under IDs: " + memberPermissions.inverse().get(permission) + " and " + permission.parsedName);
        }
		memberPermissions.put(permission.parsedName, permission);
	}
	
	/** Add a toggle permission to the registry to be used in claims 
	 * @param permission - The toggle permission to add **/
	public static void addPermission(ClaimPermissionToggle permission) {
		if(togglePermissions.containsKey(permission.parsedName)) {
			throw new RuntimeException("Identical toggle permission ID registered: " + permission.parsedName);
		}
		if(togglePermissions.containsValue(permission)) {
		    throw new RuntimeException("Indentical toggle permissions registered under IDs: " + togglePermissions.inverse().get(permission) + " and " + permission.parsedName);
		}
		togglePermissions.put(permission.parsedName, permission);
	}
	
	/** Add a list of toggle permissions to the registry to be used in claims 
     * @param permissions - The toggle permissions to add **/
    public static void addPermission(ClaimPermissionToggle... permissions) {
        for(ClaimPermissionToggle permission : permissions) {
            addPermission(permission);
        }
    }
    
    /** Add a list of member permissions to the registry to be used in claims 
     * @param permissions - The member permissions to add **/
    public static void addPermission(ClaimPermissionMember... permissions) {
        for(ClaimPermissionMember permission : permissions) {
            addPermission(permission);
        }
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
	public static final ImmutableSet<ClaimPermissionMember> getMemberPermissions() {
		return ImmutableSet.copyOf(memberPermissions.values());
	}
	
	/** @return The list of toggle permissions **/
	public static final ImmutableSet<ClaimPermissionToggle> getTogglePermissions() {
		return ImmutableSet.copyOf(togglePermissions.values());
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
