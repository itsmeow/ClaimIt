# Checking for Claims in Groups

One claim:

```java
group.hasClaim(claim);
```

All claims:

```java
group.getClaims();
```

# Setting Groups in Claims

## Adding Claims

```java
group.addClaim(claim);
```

## Removing Claims

One claim:

```java
group.removeClaim(claim);
```
All claims:

```java
group.removeAllClaims()
```