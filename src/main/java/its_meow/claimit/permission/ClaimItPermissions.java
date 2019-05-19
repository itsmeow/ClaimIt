package its_meow.claimit.permission;

import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;

public class ClaimItPermissions {
    
    public static final ClaimPermissionToggle LIVING_MODIFY = new ClaimPermissionToggle("living_modify", false, "Turning on allows zombies to break doors, enderman to take blocks, and other entity interactions that break blocks.");
    public static final ClaimPermissionToggle DROP_ITEM = new ClaimPermissionToggle("drop_item", true, "Turning on allows players to drop items.");
    public static final ClaimPermissionToggle PICKUP_ITEM = new ClaimPermissionToggle("pickup_item", true, "Turning on allows players to pick up items.");
    public static final ClaimPermissionToggle ALLOW_PROJECTILES = new ClaimPermissionToggle("allow_projectiles", false, "Turning on allows projectiles to impact - this may make animals vulnerable to enchantments like Flame, since they ignore damage events!");
    public static final ClaimPermissionToggle ENTITY_SPAWN = new ClaimPermissionToggle("entity_spawn", false, "Turning on allows mobs and animals to spawn.");
    public static final ClaimPermissionToggle PRESSURE_PLATE = new ClaimPermissionToggle("pressure_plate", true, "Disabling blocks pressure plates all reasons except players with USE. Having on still prevents players without USE from using them, but allows mobs to use them.");
    public static final ClaimPermissionToggle EXPLOSION = new ClaimPermissionToggle("explosion", false, "Enabling allows explosions to damage entities and blocks inside the claim.");
    
    public static void register() {
        // Toggles
        ClaimPermissionRegistry.addPermission(LIVING_MODIFY, DROP_ITEM, PICKUP_ITEM, ALLOW_PROJECTILES, ENTITY_SPAWN, PRESSURE_PLATE, EXPLOSION);
    }
    
}