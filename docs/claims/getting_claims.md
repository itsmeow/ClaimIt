# Getting Claims
This is a short guide on retrieving claim instances given a location, or player and name.
<br><br>
## Retrieving Claims at a Position

This one is very simple. You will need a `World`, and a `BlockPos`. It's done like so:

```java
ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
```

That's it! Keep in mind `claim` can *always* be null, so check before doing things with it!
<br><br>
## Retrieving Claims by Name

This one is also fairly simple. First let me introduce the topic of a "True Name". To stop conflicting claim names between players, all claims have their owner's UUID appended to the beginning of the name with an underscore in the backend. This means you require an owner `UUID` and the name, `String`, before you can get a claim by name.

```java
ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(name, ownerUUID);
```

You can also skip the UUID step if you already have it appended:

```java
ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(trueName);
```

Remember, whenever you retrieve a claim, it is `Nullable`.