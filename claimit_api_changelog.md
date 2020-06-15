1.2.1
-
 - Internal improvements

1.2.0
-
 - Toggle permissions now have mutable defaults accessed through `getDefault()` and `setDefault(value)`
 - Groups now have tags accessible via `group.getTag()` and publicly mutable via `GroupManager.setGroupTag(group, tag)`
 - Added config sync

1.1.1
-
 - Improved error handling while deserializing claims

1.1.0
-
 - Added `SubClaimArea`, and handling methods for it in `ClaimArea`
 - Created second config using annotations
 - Added some debugging messages throughout code

1.0.1
-
 - Moved `userconfig` package from ClaimIt to the API.
 - Added fake player checks
 - Fixed claim name validation
 - Forces now set the default value of toggles
 - Moved user config data to api data.