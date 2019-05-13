# Claim Management
This is a guide on managing (adding, removing, and retrieving) claims.

## Getting Claims
This is a short guide on retrieving claim instances given a location, or player and name.
<br><br>
### Retrieving Claims at a Position

This one is very simple. You will need a `World`, and a `BlockPos`. It's done like so:

```java
ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
```

That's it! Keep in mind `claim` can *always* be null, so check before doing things with it!
<br><br>
### Retrieving Claims by Name

This one is also fairly simple. First let me introduce the topic of a "True Name". To stop conflicting claim names between players, all claims have their owner's UUID appended to the beginning of the name with an underscore in the backend. This means you require an owner `UUID` and the name, `String`, before you can get a claim by name.

```java
ClaimArea claim = ClaimManager.getManager().getClaimByNameAndOwner(name, ownerUUID);
```

You can also skip the UUID step if you already have it appended:

```java
ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(trueName);
```

Remember, whenever you retrieve a claim, it is `Nullable`.

### Retrieving Claims Owned by a Player
You will need a UUID (you can get such from a player instance `player.getGameProfile().getId()`)

```java
ClaimManager.getManager().getClaimsOwnedByPlayer(uuid);
```

This can also be null if the player owns no claims.

### Getting all Claims
Simple:

```java
ImmutableList<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
```

## Adding Claims
There are two methods of adding claims. One fires an event for claim addition, one does not. 
Typically, use the one that does unless you are doing operations that shouldn't be tracked by other handlers.

### Creating a Claim Instance
You will, surprisingly, need a new claim instance.

```java
ClaimArea claim = new ClaimArea(dimID, posX, posZ, sideLengthX, sideLengthZ, player)
```
There is also an overload that allows you to manually input the owner's offline and online UUIDs.
The side lengths are the lengths extending from the point AFTER x and z. This means they exclude posX and posZ.

### Adding with or without an event
The actual adding of a claim (and checking for overlaps!) is done like so:

```java
// Event
boolean added = ClaimManager.getManager().addClaim(claim);
// No Event
boolean added = ClaimManager.getManager().addClaimNoEvent(claim);
```

Both methods return false if the claim overlaps another or could not be added for any reason.

## Removing Claims

This fires a `ClaimRemovedEvent`. It returns true if the claim was in the list and wasn't canceled.

```java
boolean removed = ClaimManager.getManager().deleteClaim(claim);
```