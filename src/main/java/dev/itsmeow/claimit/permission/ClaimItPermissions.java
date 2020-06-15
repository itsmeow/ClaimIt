package dev.itsmeow.claimit.permission;

import dev.itsmeow.claimit.api.permission.ClaimPermissionRegistry;
import dev.itsmeow.claimit.api.permission.ClaimPermissionToggle;

public class ClaimItPermissions {
    
    public static final ClaimPermissionToggle LIVING_MODIFY = new ClaimPermissionToggle("living_modify", false, "Turning on allows zombies to break doors, enderman to take blocks, and other entity interactions that break blocks.");
    public static final ClaimPermissionToggle DROP_ITEM = new ClaimPermissionToggle("drop_item", true, "Turning on allows players to drop items.");
    public static final ClaimPermissionToggle PICKUP_ITEM = new ClaimPermissionToggle("pickup_item", true, "Turning on allows players to pick up items.");
    public static final ClaimPermissionToggle ALLOW_PROJECTILES = new ClaimPermissionToggle("allow_projectiles", false, "Turning on allows projectiles to impact - this may make animals vulnerable to enchantments like Flame, since they ignore damage events!");
    public static final ClaimPermissionToggle ENTITY_SPAWN = new ClaimPermissionToggle("entity_spawn", false, "Turning on allows mobs and animals to spawn.");
    public static final ClaimPermissionToggle PRESSURE_PLATE = new ClaimPermissionToggle("pressure_plate", true, "Disabling blocks pressure plates for all reasons except players with USE. Having on still prevents players without USE from using them, but allows mobs to use them.");
    public static final ClaimPermissionToggle EXPLOSION = new ClaimPermissionToggle("explosion", false, "Enabling allows explosions to damage entities and blocks inside the claim.");
    public static final ClaimPermissionToggle FIRE_CREATE = new ClaimPermissionToggle("fire_create", false, "Enabling allows fire to be created and spread inside the claim.");
    public static final ClaimPermissionToggle FIRE_CREATE_ON_OBSIDIAN = new ClaimPermissionToggle("fire_create_on_obsidian", true, "Allows creation of fire on obsidian blocks, mostly for nether portals.");
    public static final ClaimPermissionToggle MOB_GRIEF = new ClaimPermissionToggle("mob_grief", false, "Allows mobgriefing (respecting gamerule) within claim.");
    public static final ClaimPermissionToggle MOB_GRIEF_VILLAGER = new ClaimPermissionToggle("mob_grief_villager", true, "Allows mobgriefing (not respecting gamerule) from villagers.");

    public static void register() {
        // Toggles
        ClaimPermissionRegistry.addPermission(LIVING_MODIFY, DROP_ITEM, PICKUP_ITEM, ALLOW_PROJECTILES, ENTITY_SPAWN, PRESSURE_PLATE, EXPLOSION, FIRE_CREATE, FIRE_CREATE_ON_OBSIDIAN, MOB_GRIEF, MOB_GRIEF_VILLAGER);
    }
    
}