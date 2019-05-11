# Adding Permissions
This is a guide for adding new member and toggle permissions that can be utilized by users.


## Adding Member Permissions
Through the API you can add new member permissions that users can grant inside their claims. This may be specific to a feature you want to have its own permission rather than a default.<br>
You should be keeping static final references to your permissions. Creating a permission object is like so:

```java
public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember("modify", "Allows modification of blocks - placing and breaking.");
```

The first argument is a unique name that will be used in commands or other access methods. The second should be a description of what your permission does, this is used for ingame help.
Now, you need to register your member permission. This should be done during preinit.

```java
@SubscribeEvent
public static void preInit(FMLPreInitializationEvent event) {
	ClaimPermissionRegistry.addPermission(MODIFY);
}
```

## Adding Toggle Permissions
You may also wish to add toggleable claim specific interactions.<br>
Just like member permissions, these should be kept statically for later checking.

```java
public static final ClaimPermissionToggle ENTITY_SPAWN = new ClaimPermissionToggle("entity_spawn", false, "Turning on allows mobs and animals to spawn.");
```

And register them like so:

```java
@SubscribeEvent
public static void preInit(FMLPreInitializationEvent event) {
	ClaimPermissionRegistry.addPermission(ENTITY_SPAWN);
}
```

## Toggles for Member Permissions
You may notice while using ClaimIt there are toggles for member permissions to make them essentially "public"
You can also add toggles for member permissions by making two instances (one toggle, one member) with the same name.

```java
public static final ClaimPermissionMember PVP = new ClaimPermissionMember("pvp", "Allows damaging other players. Can be disabled by server admin.");
public static final ClaimPermissionToggle PVP_TOGGLE = new ClaimPermissionToggle("pvp", false, "Allows damaging other players in the claim for all users if enabled.");
```

And then register them at the same time together like this:

```java
@SubscribeEvent
public static void preInit(FMLPreInitializationEvent event) {
	ClaimPermissionRegistry.addPermission(PVP, PVP_TOGGLE);
}
```