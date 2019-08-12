<h1 align="center">ClaimIt</h1>
<p style="text-align: center;">
<a href='https://claimit.readthedocs.io/en/latest/?badge=latest'><img src='https://readthedocs.org/projects/claimit/badge/?version=latest' alt='Documentation Status' /></a>
</p>
<h5 align="center">A mod for claiming land in Forge 1.12</h5>

### Why is this mod special?
This mod is built with the idea of creating easy compatibility that allows mods to integrate properly with the claiming system to avoid the need for banning items.

### Do my users need to download ClaimIt?
No. It's server side only, but loads on integrated servers (singleplayer/LAN) just fine.

### I have questions/need support! Where can I get help?
Your options are the ingame help command, this article, and my [Discord server](https://discord.gg/zrjXjP5).

### API
ClaimIt has an expansive API for integration and addition. You can view its documentation via clicking the docs badge or looking at the `docs/` directory.

### Using the base mod
ClaimIt is the base mod, which the API is completely independent of. ClaimIt handles protections and hooks/commands.<br>
ClaimIt includes an expansive ingame help command, accessed with `/claimit help`.

## Member Permissions
Member permissions are a subclass of permission that can be assigned to a player in a group or claim. Different member permissions grant different permissions and abilities.

 - `modify`: Allows modification of blocks - placing and breaking.
 - `use`: Allows use of items (flint and steel, buckets, etc), interactions with blocks (chests, furnaces, doors, levers), and some interactions with entities (shearing sheep, milking cows, etc, which also require `entity`).
 - `entity`: Allows damaging entities and interactions with entities (shearing sheep, milking cows, etc, which also require `use`)
 - `pvp`: Allows damaging other players. Can be disabled by server admin in the config.
 - `manage_perms`: Allows use of toggle commands as well as adding or removing members in a claim

## Toggle Permissions
Toggle permissions are a subclass of permission that can be enabled or disabled per claim, and are stored per claim. They can control certain global protections for claims, such as explosions.

 - `living_modify` <sub>(Default OFF)</sub>: Turning on allows zombies to break doors, enderman to take blocks, and other entity interactions that break blocks.
 - `drop_item` <sub>(Default ON)</sub>: Turning on allows players to drop items.
 - `pickup_item` <sub>(Default ON)</sub>: Turning on allows players to pick up items.
 - `allow_projectiles` <sub>(Default OFF)</sub>: Turning on allows projectiles to impact - this may make animals vulnerable to enchantments like Flame, since they ignore damage events!
 - `entity_spawn` <sub>(Default OFF)</sub>: Turning on allows mobs and animals to spawn.
 - `pressure_plate` <sub>(Default ON)</sub>: Disabling blocks pressure plates for all reasons except players with USE. Having on still prevents players without USE from using them, but allows mobs to use them. Be aware dropping items to put plates down works while this is on.
 - `explosion` <sub>(Default OFF)</sub>: Enabling allows explosions to damage entities and blocks inside the claim.
 - `fire_create` <sub>(Default OFF)</sub>: Enabling allows fire to be created and spread inside/on the border of the claim.
 - `fire_create_on_obsidian` <sub>(Default ON)</sub>: Allows creation of fire on obsidian blocks, mostly for nether portals.
 - `allow_fake_player_bypass` <sub>(Default ON)</sub>: Grants fake players permissions in the claim. This applies to things like BC Quarries or "Block Breakers"

## Member Toggle Permissions
There can also be toggle perms sharing names with member perms. Toggling these "member toggles" to ON makes that member permission publicly available. E.g. toggling the `modify` toggle will allow anyone to place and break blocks in that claim. Not all member permissions have a toggle.

## Commands
ClaimIt includes an abundance of commands both for player use and admin use.

### Base Commands

   * `/claimit admin`
   * `/claimit cancel`
   * `/claimit claim [subcommand]`
   * `/claimit claimblocks [subcommand]`
   * `/claimit config [config) (value]`
   * `/claimit confirm`
   * `/claimit group [subcommand]`
   * `/claimit help [subcommand]`

### Claim Commands

   * `/claimit claim delete [claimname]`
   * `/claimit claim deleteall`
   * `/claimit claim info [claimname]`
   * `/claimit claim list`
   * `/claimit claim manage [claimname] [member] [playername]`
   * `/claimit claim permission <add/remove> <permission> <playername> [claimname]`
   * `/claimit claim permission list [claimname]`
   * `/claimit claim setname [newname]`
   * `/claimit claim toggle [togglename] [claimname]`

### Group Commands

   * `/claimit group claim <add/remove> <groupname> [claimname]`
   * `/claimit group create <groupname>`
   * `/claimit group delete <groupname>`
   * `/claimit group info <groupname>`
   * `/claimit group list`
   * `/claimit group permission <add/remove> <permission> <playername> <groupname>`
   * `/claimit group setname <groupname> <newname>`

### Help Commands

   * `/claimit help command <command string>`
   * `/claimit help permission <member/toglge> [permission]`
   * `/claimit help topic [topicname]`
   * `/claimit help userconfig [configname]`

### Claim Blocks
   * `/claimit claimblocks view`

## Permissions
ClaimIt has basic Sponge/Spigot integration (yes, LuckPerms included!)<br>
With Sponge present, most of the default command permissions are allowed by default.<br><br>
**You will need to give users access to** `claimit.claim.create` **when using Sponge!**<br><br>
List of default permissions:

   * `claimit.command`
   * `claimit.command.claimit`
   * `claimit.command.claimit.claim`
   * `claimit.command.claimit.claim.info`
   * `claimit.command.claimit.claim.delete`
   * `claimit.command.claimit.claim.deleteall`
   * `claimit.command.claimit.claim.list`
   * `claimit.command.claimit.claim.manage`
   * `claimit.command.claimit.claim.permission`
   * `claimit.command.claimit.claim.permission.list`
   * `claimit.command.claimit.claim.setname`
   * `claimit.command.claimit.claim.toggle`
   * `claimit.command.claimit.group`
   * `claimit.command.claimit.group.create`
   * `claimit.command.claimit.group.claim`
   * `claimit.command.claimit.group.delete`
   * `claimit.command.claimit.group.info`
   * `claimit.command.claimit.group.list`
   * `claimit.command.claimit.group.permission`
   * `claimit.command.claimit.group.setname`
   * `claimit.command.claimit.config`
   * `claimit.command.claimit.cancel`
   * `claimit.command.claimit.confirm`
   * `claimit.command.claimit.claimblocks`
   * `claimit.command.claimit.claimblocks.view`
   * `claimit.command.claimit.help`
   * `claimit.command.claimit.help.topic`
   * `claimit.command.claimit.help.permission`
   * `claimit.command.claimit.help.command`
   * `claimit.command.claimit.help.userconfig`

The list of non default admin permissions:

   * `claimit.admin` - Required for all of the below, allows enabling admin mode via `/claimit admin`
   * `claimit.claim.manage.others` - This grants the ability to bypass protections as admin. **This is also required for most "claim.*.others" permissions**
   
The list of default admin permissions (these require one of the above, but are default. You can set these to false to block a specific one):
   * `claimit.command.claimit.claim.manage.others` - View manage dialog for other's claims as admin
   * `claimit.command.claimit.claim.delete.others` - Allow deleting other's claims as admin
   * `claimit.command.claimit.claim.deleteall.others` - Allow deleting all of another's claims as admin
   * `claimit.command.claimit.claim.list.others` - Allow listing all claims for a user or all claims on a server as admin
   * `claimit.command.claimit.claim.permission.others` - Allow editing members on other's claims as admin
   * `claimit.command.claimit.claim.permission.list.others` - Allow viewing members of another's claims as admin
   * `claimit.command.claimit.claim.setname.others` - Allow setting the name of other's claims as admin
   * `claimit.command.claimit.claim.toggle.others` - Allow editing of toggles on other's claims as admin
   * `claimit.command.claimit.claim.info.others` - Allow getting info for claims via true name as admin. Note you can still view info at a given location.
   * `claimit.command.claimit.group.claim.others` - Allow adding claims to groups you are not a member of as admin
   * `claimit.command.claimit.group.delete.others` - Allow deleting claims you do not own as admin
   * `claimit.command.claimit.group.list.others` - Allow listing all groups on the server or all groups owned by a player as admin
   * `claimit.command.claimit.group.permission.others` - Allow editing members on groups you cannot as admin
   * `claimit.command.claimit.group.setname.others` - Allow renaming groups you don't own as admin
   * `claimit.command.claimit.claimblocks.setallowed` - Allow setting maximum claim blocks as admin
   * `claimit.command.claimit.claimblocks.addallowed` - Allow adding to maximum claim blocks as admin
   * `claimit.command.claimit.claimblocks.view.others` - Allow viewing claim blocks of other players as admin

I'll say it again.<br>
**You will need to give users access to** `claimit.claim.create` **when using Sponge!**

### Administration
ClaimIt provides an abundance of administration tools, namely the ability to bypass protections and modify claim toggles, members, etc. Read the admin permissions above for a general idea of what you'll need.
In order to use ANY admin features you will need admin enabled. To do so, run `/claimit admin`. So long as this is enabled and you have `claimit.claim.manage.others`, you can bypass protections.
Setting block limits is very easy, and can be done via the `/claimit claimblocks` command, so long as you have permission.
