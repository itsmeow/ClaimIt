<h1 align="center">ClaimIt</h1>
<p style="text-align: center;">
<a href='https://claimit.readthedocs.io/en/latest/?badge=latest'><img src='https://readthedocs.org/projects/claimit/badge/?version=latest' alt='Documentation Status' /></a>
</p>
<h5 align="center">A mod for claiming land in Forge 1.12</h5>

### Why is this mod special?
This mod is built with the idea of creating easy compatibility that allows mods to integrate properly with the claiming system to avoid the need for banning items.

### API
ClaimIt has an expansive API for integration and addition. You can view its documentation via clicking the docs badge or looking at the `docs/` directory.

### Using the base mod
ClaimIt is the base mod, which the API is completely independent of. ClaimIt handles protections and hooks/commands.<br>
ClaimIt includes an expansive ingame help command, accessed with `/claimit help`.

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
