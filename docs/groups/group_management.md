# Group Management
This is a short doc on getting, adding, and removing group instances.

## Group Retrieval

### Getting Individual Groups
Getting groups is very simple. You will need a group name.

```java
GroupManage.getGroup(name);
```

### Getting All Groups
Getting all the groups is probably easier.

```java
GroupManager.getGroups();
```

### Getting Groups that have a Claim
You can retrieve a list of groups that have a claim added.

```java
GroupManager.getGroupsForClaim(claim);
```

## Group Addition and Removal

### Adding Groups

First you need a group instance:

```java
Group group = new Group(name, ownerUUID);
```

Yes, you're required an owner uuid. The name is what is used for later retrieval.
Now add it to the list:

```java
GroupManager.addGroup(group);
```

### Removing Groups

Also simple. You'll need to get a group instance.

```java
GroupManager.removeGroup(group);
```

## Renaming Groups

You can do this yourself, but there's a built in method.

```java
GroupManager.renameGroup(oldName, newName);
```

## Tagging Groups

You can set the tags of groups like so:

```java
GroupManager.setGroupTag(group, tag);
```

If the operation succeeds the method will return true. Otherwise, it means another group has that tag.