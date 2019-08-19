1.1.0
-
 - Added sub-claims (can disable in API config 2)
 - Added chunk pruning via config
 - Fixed alias for /ci appearing multiple times
 - Claim owners can now still use true names to reference their claims in various places
 - Added wildcards (`*`) and multi-parsing to permission command. Ex: `/ci claim permission add modify,use Player23,Player43` or `/ci claim permission remove * *`
 - Fixed crash with direct player attacks caused indirectly by another entity (happens with Twilight Forest ghast fireballs for some reason?)
 - Fixed being unable to add claims to groups when you owned the group
 - Added clickable "View Subclaims" in claim info

1.0.1
-
 - Fixed bug where non-ops couldn't make claims without sponge installed (ok, I'm really sorry for this one, I swear, I thought it worked)
 - Fixed bug where claims could have duplicate names
 - Added a toggle for fake player permission bypass (on by default)
 - Added a toggle for creating fire on obsidian (on by default, for nether portals)
 - Fixed server config forces not being applied until a toggle is set with the command, it now forces on EVERY check
 - Added claim blocks playtime accrual system (see claimit.cfg)
 - Moved user config data to api data. Sorry for any data loss, but I don't think claim border display options are important right now.