package its_meow.claimit.claim;

import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.config.ClaimConfig;

public class ClaimPermissions {
	
	public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember("modify", "Allows modification of blocks - placing and breaking.");
	public static final ClaimPermissionMember USE = new ClaimPermissionMember("use", "Allows use of items (flint and steel, etc), interactions with blocks (chests, furnaces, doors, levers), and interactions with entities (shearing sheep).");
	public static final ClaimPermissionMember ENTITY = new ClaimPermissionMember("entity", "Allows damaging entities and interactions with entities (shearing sheep, need use as well!)");
	public static final ClaimPermissionMember PVP = new ClaimPermissionMember("pvp", "Allows damaging other players. Can be disabled by server admin.");
	public static final ClaimPermissionMember MANAGE_PERMS = new ClaimPermissionMember("manage_perms", "Allows use of toggle commands as well as adding or removing members");
	
	public static final ClaimPermissionToggle LIVING_MODIFY = new ClaimPermissionToggle("living_modify", false, "Turning on allows zombies to break doors, and other entity interactions that break blocks.", false, false);
	public static final ClaimPermissionToggle DROP_ITEM = new ClaimPermissionToggle("drop_item", false, "Turning on allows players to drop items. Can be forced on by server admins.", !ClaimConfig.allowDisableDrop, true);
	public static final ClaimPermissionToggle PICKUP_ITEM = new ClaimPermissionToggle("pickup_item", false, "Turning on allows players to pick up items. Can be forced on by server admins.", !ClaimConfig.allowDisablePickup, true);
	public static final ClaimPermissionToggle ALLOW_PROJECTILES = new ClaimPermissionToggle("allow_projectiles", false, "Turning on allows projectiles to impact - this may make animals vulnerable!", false, false);
	public static final ClaimPermissionToggle ENTITY_SPAWN = new ClaimPermissionToggle("entity_spawn", false, "Turning on allows mobs and animals to spawn. Can be forced on by server admins.", !ClaimConfig.allowDisableSpawns, true);
	
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
		ClaimPermissionRegistry.addPermission(ALLOW_PROJECTILES);
		ClaimPermissionRegistry.addPermission(ENTITY_SPAWN);
	}
	
}
