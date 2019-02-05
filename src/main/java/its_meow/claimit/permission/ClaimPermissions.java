package its_meow.claimit.permission;

public class ClaimPermissions {
	
	public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember("modify");
	public static final ClaimPermissionMember USE = new ClaimPermissionMember("use");
	public static final ClaimPermissionMember ENTITY = new ClaimPermissionMember("entity");
	public static final ClaimPermissionMember PVP = new ClaimPermissionMember("pvp");
	
	public static void register() {
		ClaimPermissionRegistry.addPermission(MODIFY);
		ClaimPermissionRegistry.addPermission(USE);
		ClaimPermissionRegistry.addPermission(ENTITY);
		ClaimPermissionRegistry.addPermission(PVP);
	}
	
}
