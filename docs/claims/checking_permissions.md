# Checking Permissions in Claims

<br>

## Checking Member Permissions

Now you have your `ClaimArea` objects. These are very versatile and useful objects that can give you all sorts of information about a claim.
The number one thing about claims is probably permissions. They control who can and cannot do things inside a given area. Retrieving permissions for a user is also very easy.
Keep in mind, the owner and server administrators (in admin mode) are always considered to have a permission, unless you use other methods of retrieval.
For most, you want to include those. Here's how you do it:

```java
boolean hasPerm = claim.hasPermission(permission, player);
```

You're probably wondering where you can get permission instances, no worries! You can either use your own (we'll cover this) or use the built in permissions. There are also several built in helper methods for specific permissions in `ClaimArea`
The main class that contains permissions is `ClaimPermissions`.

```java
boolean canBreak = claim.hasPermission(ClaimPermissions.MODIFY, player);
```

I mentioned built in methods, take a look at these:

```java
claim.canModify(player);
claim.canUse(player);
claim.canEntity(player);
claim.canPVP(player);
claim.canManage(player);
```

You can also check specifically if a user is the owner of a claim via `claim.isTrueOwner(player)` or `claim.isTrueOwner(uuid)`.
There's a reason the word "true" is in there. `claim.isOwner(player)` returns the same thing given an owner, but also returns true for administrators. Generally it's a good idea to use `isOwner`, because administrators should be able to fully manage claims, as the owner would.

## Checking Toggle Permissions

Toggles are claim-specific boolean values. Getting them is very simple:

```java
claim.isPermissionToggled(togglePermission);
```

Similarly, you may ask where the toggles are at? They are also located in `ClaimPermissions`. Make sure the types match.