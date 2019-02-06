package its_meow.claimit.permission;

public class ClaimPermissions {
	
	public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember("modify");
	public static final ClaimPermissionMember USE = new ClaimPermissionMember("use");
	public static final ClaimPermissionMember ENTITY = new ClaimPermissionMember("entity");
	public static final ClaimPermissionMember PVP = new ClaimPermissionMember("pvp");
	public static final ClaimPermissionMember MANAGE_PERMS = new ClaimPermissionMember("manage_perms");
	public static final ClaimPermissionToggle TEST_TOGGLE = new ClaimPermissionToggle("toggletest", false);
	
	public static void register() {
		ClaimPermissionRegistry.addPermission(MODIFY);
		ClaimPermissionRegistry.addPermission(USE);
		ClaimPermissionRegistry.addPermission(ENTITY);
		ClaimPermissionRegistry.addPermission(PVP);
		ClaimPermissionRegistry.addPermission(MANAGE_PERMS);
		ClaimPermissionRegistry.addPermission(TEST_TOGGLE);
	}
	
}
