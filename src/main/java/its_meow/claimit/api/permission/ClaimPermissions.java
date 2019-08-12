package its_meow.claimit.api.permission;

public class ClaimPermissions {

	public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember("modify", "Allows modification of blocks - placing and breaking.");
	public static final ClaimPermissionMember USE = new ClaimPermissionMember("use", "Allows use of items (flint and steel, etc), interactions with blocks (chests, furnaces, doors, levers), and interactions with entities (shearing sheep).");
	public static final ClaimPermissionMember ENTITY = new ClaimPermissionMember("entity", "Allows damaging entities and interactions with entities (shearing sheep, need use as well!)");
	public static final ClaimPermissionMember PVP = new ClaimPermissionMember("pvp", "Allows damaging other players. Can be disabled by server admin.");
	public static final ClaimPermissionMember MANAGE_PERMS = new ClaimPermissionMember("manage_perms", "Allows use of toggle commands as well as adding or removing members");

	public static final ClaimPermissionToggle MODIFY_TOGGLE = new ClaimPermissionToggle("modify", false, "Allows modification of blocks for all users if enabled.");
	public static final ClaimPermissionToggle USE_TOGGLE = new ClaimPermissionToggle("use", false, "Allows use of items (flint and steel, etc), interactions with blocks (chests, furnaces, doors, levers), and interactions with entities (shearing sheep) for all users if enabled.");
	public static final ClaimPermissionToggle ENTITY_TOGGLE = new ClaimPermissionToggle("entity", false, "Allows damaging entities and interactions with entities (shearing sheep, need use as well!) for all users if enabled.");
	public static final ClaimPermissionToggle PVP_TOGGLE = new ClaimPermissionToggle("pvp", false, "Allows damaging other players in the claim for all users if enabled.");
    
	public static final ClaimPermissionToggle ALLOW_FAKE_PLAYER_BYPASS = new ClaimPermissionToggle("allow_fake_player_bypass", true, "Grants fake players permissions in the claim. This applies to things like BC Quarries or \"Block Breakers\"");

	public static void register() {
		// Members with toggles
		ClaimPermissionRegistry.addPermission(MODIFY, MODIFY_TOGGLE);
		ClaimPermissionRegistry.addPermission(USE, USE_TOGGLE);
		ClaimPermissionRegistry.addPermission(ENTITY, ENTITY_TOGGLE);
		ClaimPermissionRegistry.addPermission(PVP, PVP_TOGGLE);
		
		// Members
		ClaimPermissionRegistry.addPermission(MANAGE_PERMS);
		
		// Toggles
		ClaimPermissionRegistry.addPermission(ALLOW_FAKE_PLAYER_BYPASS);
	}

}
