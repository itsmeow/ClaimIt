# Setting Permissions in Claims
Setting permissions in claims is about as easy as getting them.

## Setting Member Permissions

### Adding members

Adding members is done like so:

```java
claim.addMember(memberPermission, player);
```
You can also use UUIDs:

```java
claim.addMember(memberPermission, uuid);
```

### Removing members

Removing members is done similarly to adding them:

```java
claim.removeMember(memberPermission, player);
```
UUIDs:

```java
claim.removeMember(memberPermission, uuid);
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