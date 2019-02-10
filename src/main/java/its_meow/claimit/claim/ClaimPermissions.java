package its_meow.claimit.claim;

import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;

public class ClaimPermissions {
	
	public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember("modify");
	public static final ClaimPermissionMember USE = new ClaimPermissionMember("use");
	public static final ClaimPermissionMember ENTITY = new ClaimPermissionMember("entity");
	public static final ClaimPermissionMember PVP = new ClaimPermissionMember("pvp");
	public static final ClaimPermissionMember MANAGE_PERMS = new ClaimPermissionMember("manage_perms");
	public static final ClaimPermissionToggle LIVING_MODIFY = new ClaimPermissionToggle("living_modify", false);
	public static final ClaimPermissionToggle DROP_ITEM = new ClaimPermissionToggle("drop_item", false);
	public static final ClaimPermissionToggle PICKUP_ITEM = new ClaimPermissionToggle("pickup_item", false);
	
	public static void register() {
		// Members
		ClaimPermissionRegistry.addPermission(MODIFY);
		ClaimPermissionRegistry.addPermission(USE);
		ClaimPermissionRegistry.addPermission(ENTITY);
		ClaimPermissionRegistry.addPermission(PVP);
		ClaimPermissionRegistry.addPermission(MANAGE_PERMS);
		
		// Toggles
		ClaimPermissionRegistry.addPermission(LIVING_MODIFY);
		ClaimPermissionRegistry.addPermission(DROP_ITEM);
		ClaimPermissionRegistry.addPermission(PICKUP_ITEM);
	}
	
}
