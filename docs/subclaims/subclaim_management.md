# Subclaim Management
This is a guide on managing (adding, removing, and retrieving) subclaims.

## Getting Sublaims
This is a short guide on retrieving subclaim instances given a location in a claim, or name.
<br><br>
### Retrieving Subclaims at a position in a claim

This one is very simple. You will need a `ClaimArea`, and a `BlockPos`. It's done like so:

```java
SubClaimArea subclaim = claim.getSubClaimAtLocation(pos);
```

That's it! Keep in mind `subclaim` can *always* be null, so check before doing things with it!
For a simpler approach that can be used during permission checks, there is also a "most specific claim" which will return either a subclaim at a position, or the claim itself if the claim does not contain a subclaim at the position.

```java
ClaimArea mostSpecificClaim = claim.getMostSpecificClaim(pos);
```

This method is good for use in permission checks because it makes it simple to do position-based checks while also not limiting it to ONLY that, and it is never null.

<br><br>

### Retrieving Subclaims by Name

Subclaims do not have "true names" unlike claims, because they are on specific claims.

```java
SubClaimArea subclaim = claim.getSubClaimWithName(name);
```

Remember, whenever you retrieve a subclaim via name, it is `Nullable`.

### Retrieving all Subclaims in a Claim

```java
ImmutableSet<SubClaimArea> subclaims = claim.getSubClaims();
```

## Adding Subclaims

### Creating a Subclaim Instance

```java
SubClaimArea claim = new SubClaimArea(parentClaim, posX, posZ, sideLengthX, sideLengthZ);
```
There is also an overload that allows you to manually input the subclaim's name.
The side lengths are the lengths extending from the point AFTER x and z. This means they exclude posX and posZ.
If the subclaim extends outside of the bounds of a claim it will fail when being added to the parent.

### Actually adding it to the parent claim
The actual adding of a subclaim (and checking for overlaps!) is done like so:

```java
ClaimAddResult result = claim.addSubClaim(subclaim);
```

If the subclaim was added successfully, it will return `ClaimAddResult.ADDED`. Otherwise, handle any other `ClaimAddResults` properly.

## Removing Subclaims from the parent

```java
claim.removeSubClaim(subclaim);
```

This returns true if the subclaim was present in the list, and was removed.