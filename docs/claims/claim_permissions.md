# Checking Permissions in Claims

## Checking Member Permissions

Now you have your `ClaimArea` objects. These are very versatile and useful objects that can give you all sorts of information about a claim.
The number one thing about claims is probably permissions. They control who can and cannot do things inside a given area. Retrieving permissions for a user is also very easy.
Keep in mind, the owner and server administrators (in admin mode) are always considered to have a permission, unless you use other methods of retrieval.
For most, you want to include those. Here's how you do it:

```java
boolean hasPerm = claim.hasPermission(player, permission);
```

You're probably wondering where you can get permission instances, no worries! You can either use your own (we'll cover this) or use the built in permissions. There are also several built in helper methods for specific permissions in `ClaimArea`
The main class that contains permissions is `ClaimPermissions`.

```java
boolean canBreak = claim.hasPermission(player, ClaimPermissions.MODIFY);
```

You can pass UUIDs to the above methods as well.

I mentioned methods for default permissions:

```java
claim.canModify(player);
claim.canUse(player);
claim.canEntity(player);
claim.canPVP(player);
claim.canManage(player);
```

## Checking Toggle Permissions

Toggles are claim-specific boolean values. Getting them is very simple:

```java
claim.isPermissionToggled(togglePermission);
```

Similarly, you may ask where the toggles are at? They are also located in `ClaimPermissions`. Make sure the types match.

## Checking for Ownership

Simply:

```java
boolean owner = claim.isOwner(player)
```

There's also an overload that accepts a UUID.


# Setting Permissions in Claims
Setting permissions in claims is about as easy as getting them.

## Setting Member Permissions

### Adding members

Adding members is done like so:

```java
claim.addMember(player, memberPermission);
```
You can also use UUIDs:

```java
claim.addMember(uuid, memberPermission,);
```

### Removing members

Removing members is done similarly to adding them:

```java
claim.removeMember(player, memberPermission);
```
UUIDs:

```java
claim.removeMember(uuid, memberPermission);
```

## Setting Toggle Permissions

### Flipping Toggles
You can reverse a toggle easily:

```java
claim.flipPermissionToggle(togglePermission);
```
You may also wish to set its value directly:

```java
claim.setPermissionToggle(togglePermission, booleanValue);
```