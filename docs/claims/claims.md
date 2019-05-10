# Claims
This is ClaimIt, after all! How do I check for, manage, and ensure permissions in claims? How can I stop my mod from bypassing claims?
You've come to the right place for all this info.
<br><br>
<h1>Retrieving Claims at a Position</h1>

This one is very simple. You will need a `World`, and a `BlockPos`. It's done like so:

```java
ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
```

That's it! Keep in mind `claim` can *always* be null, so check before doing things with it!
<br><br>
<h1>Retrieving Claims by Name</h1>

This one is also fairly simple. First let me introduce the topic of a "True Name". To stop conflicting claim names between players, all claims have their owner's UUID appended to the beginning of the name with an underscore in the backend. This means you require an owner `UUID` and the name, `String`, before you can get a claim by name.

```java
ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(name, ownerUUID);
```

You can also skip the UUID step if you already have it appended:

```java
ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(trueName);
```

Remember, whenever you retrieve a claim, it is `Nullable`.
<br><br>

<h1>Permissions in Claims</h1>
<br>
<h2>Checking Member Permissions</h2>

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

<h2>Checking Toggle Permissions</h2>

Toggles are claim-specific boolean values. Getting them is very simple:

```java
claim.isPermissionToggled(togglePermission);
```

Similarly, you may ask where the toggles are at? They are also located in `ClaimPermissions`. Make sure the types match.