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
ClaimIt API handles claims and their management, but via what? The answer is the base mod, ClaimIt.<br>
ClaimIt includes an expansive ingame help command, accessed with `/claimit help`.


## Permissions
ClaimIt has basic Sponge/Spigot integration (yes, LuckPerms included!)<br>
With Sponge present, most of the default command permissions are allowed by default.<br><br>
**You will need to give users access to** `claimit.claim.create` **when using Sponge!**<br><br>
List of default permissions:

   * `claimit`
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
   * `claimit.command.claimit.help`
   * `claimit.command.claimit.help.topic`
   * `claimit.command.claimit.help.permission`
   * `claimit.command.claimit.help.command`
   * `claimit.command.claimit.help.userconfig`

The list of admin permissions:

   * `claimit.admin` - Required for all of the below, allows enabling admin mode via `/claimit admin`
   * `claimit.claim.manage.others` - This grants the ability to bypass protections as admin. **This is also required for most "claim.*.others" permissions**
   * `claimit.command.claimit.claim.manage.others` - View manage dialog for other's claims as admin
   * `claimit.command.claimit.claim.delete.others` - Allow deleting other's claims as admin
   * `claimit.command.claimit.claim.deleteall.others` - Allow deleting all of another's claims as admin
   * `claimit.command.claimit.claim.list.others` - Allow listing all claims for a user or all claims on a server as admin
   * `claimit.command.claimit.claim.permission.others` - Allow editing members on other's claims as admin
   * `claimit.command.claimit.claim.permission.list.others` - Allow viewing members of another's claims as admin
   * `claimit.command.claimit.claim.setname.others` - Allow setting the name of other's claims as admin
   * `claimit.command.claimit.claim.toggle.others` - Allow editing of toggles on other's claims as admin
   * `claimit.command.claimit.group.claim.others` - Allow adding claims to groups you are not a member of as admin
   * `claimit.command.claimit.group.delete.others` - Allow deleting claims you do not own as admin
   * `claimit.command.claimit.group.list.others` - Allow listing all groups on the server or all groups owned by a player as admin
   * `claimit.command.claimit.group.permission.others` - Allow editing members on groups you cannot as admin
   * `claimit.command.claimit.group.setname.others` - Allow renaming groups you don't own as admin

I'll say it again.<br>
**You will need to give users access to** `claimit.claim.create` **when using Sponge!**