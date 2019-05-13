# Checking Permissions in Groups

## In General

You can pass UUIDs for this one too.

```java
group.hasPermission(player, memberPermission);
```

## For Claims

You can also pass a UUID instead of a player.

```java
boolean canModifyInClaim = hasPermissionInClaim(player, memberPermission, claim);
```

# Setting Permissions in Groups
Setting permissions in groups is identical to claims, because they both extend `MemberContainer`.

## Setting Member Permissions

### Adding members

Adding members is done like so:

```java
group.addMember(player, memberPermission);
```
With UUIDs:

```java
group.addMember(uuid, memberPermission,);
```

### Removing members

Removing members is done like so:

```java
group.removeMember(player, memberPermission);
```
UUIDs:

```java
group.removeMember(uuid, memberPermission);
```

All members:

```java
group.removeAllMembers();
```