<h1 align="center">ClaimIt</h1>
<img align="center" style="display:block;max-width:100px;max-height:100px;" src="https://media.forgecdn.net/avatars/207/69/636963311039618129.png" />
<p style="text-align: center;">
<a href='https://claimit.readthedocs.io/en/latest/?badge=latest'><img src='https://readthedocs.org/projects/claimit/badge/?version=latest' alt='Documentation Status' /></a>
</p>
<h5 align="center">A mod for claiming land in Forge 1.12</h5>

### Why is this mod special?
This mod is built with the idea of creating easy compatibility that allows mods to integrate properly with the claiming system to avoid the need for banning items.
It also includes an in-depth but configurable permission system to allow full control over who can do what, and what is allowed where, as well as in-depth administration tools.

### Do my users need to download ClaimIt?
No. It's server side only, but loads on integrated servers (singleplayer/LAN) just fine.

### I have questions/need support! Where can I get help?
The best places to get help are the ingame help command, this article, and my [Discord server](https://discord.gg/zrjXjP5).

### API
ClaimIt has an expansive API for integration and addition. You can view its documentation via clicking the docs badge or looking at the `docs/` directory.

### Using the base mod
ClaimIt is the base mod, which the API is completely independent of. ClaimIt handles protections and hooks/commands.<br>
ClaimIt includes an expansive ingame help command, accessed with `/claimit help`.
You can also view all of the sections below for a general outline of commands, permissions, features, and configuration.

## Member Permissions
Member permissions are a subclass of permission that can be assigned to a player in a group or claim. Different member permissions grant different permissions and abilities. You can give players these permissions with `/claimit claim permission <add/remove> <permission> <player>`. It also supports wildcards (`*`) and multiple arguments. Example: `/ci claim permission add modify,use Player23,Player43` or `/ci claim permission remove * *`

 - `modify`: Allows modification of blocks - placing and breaking.
 - `use`: Allows use of items (flint and steel, buckets, etc), interactions with blocks (chests, furnaces, doors, levers), and some interactions with entities (shearing sheep, milking cows, etc, which also require `entity`).
 - `entity`: Allows damaging entities and interactions with entities (shearing sheep, milking cows, etc, which also require `use`)
 - `pvp`: Allows damaging other players. Can be disabled by server admin in the config.
 - `manage_perms`: Allows use of toggle commands as well as adding or removing members in a claim

## Toggle Permissions
Toggle permissions are a subclass of permission that can be enabled or disabled per claim, and are stored per claim. They can control certain global protections for claims, such as explosions. They can be modified with `/claimit claim toggle [toggle name]`

 - `living_modify` (Default OFF): Turning on allows zombies to break doors, enderman to take blocks, and other entity interactions that break blocks.
 - `drop_item` (Default ON): Turning on allows players to drop items.
 - `pickup_item` (Default ON): Turning on allows players to pick up items.
 - `allow_projectiles` (Default OFF): Turning on allows projectiles to impact - this may make animals vulnerable to enchantments like Flame, since they ignore damage events!
 - `entity_spawn` (Default OFF): Turning on allows mobs and animals to spawn.
 - `pressure_plate` (Default ON): Disabling blocks pressure plates for all reasons except players with USE. Having on still prevents players without USE from using them, but allows mobs to use them. Be aware dropping items to put plates down works while this is on.
 - `explosion` (Default OFF): Enabling allows explosions to damage entities and blocks inside the claim.
 - `fire_create` (Default OFF): Enabling allows fire to be created and spread inside/on the border of the claim.
 - `fire_create_on_obsidian` (Default ON): Allows creation of fire on obsidian blocks, mostly for nether portals.
 - `allow_fake_player_bypass` (Default ON): Grants fake players permissions in the claim. This applies to things like BC Quarries or "Block Breakers"

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
   * `/claimit claim setname <name>`
   * `/claimit claim toggle [togglename] [claimname]`
   
### Subclaim Commands
   * `/claimit subclaim delete [claimname) (subclaimname]`
   * `/claimit subclaim deleteall [claimname]`
   * `/claimit subclaim info [claimname) (subclaimname]`
   * `/claimit subclaim list [claimname]`
   * `/claimit subclaim permission <add/remove> <permission> <username> [claimname) (subclaimname]`
   * `/claimit subclaim permission list [claimname) (subclaimname]`
   * `/claimit subclaim setname <name>`
   * `/claimit subclaim toggle [toggle name] [claim name) (subclaim name]`

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
   * `/claimit claimblocks setallowed <player> <amount>`
   * `/claimit claimblocks addallowed <player> <amount>`

## Permissions
ClaimIt has basic Sponge/Spigot integration (yes, LuckPerms included!)<br>
With Sponge present, most of the default command permissions are allowed by default.<br><br>
**You will need to give users access to** `claimit.claim.create` and `claimit.subclaim.create` **when using Sponge!**<br><br>
List of non default non admin permissions (Sponge only):

   * `claimit.claim.create` - Allows creating claims
   * `claimit.subclaim.create` - Allows creating subclaims

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
   * `claimit.command.claimit.subclaim`
   * `claimit.command.claimit.subclaim.delete`
   * `claimit.command.claimit.subclaim.deleteall`
   * `claimit.command.claimit.subclaim.info`
   * `claimit.command.claimit.subclaim.list`
   * `claimit.command.claimit.subclaim.permission`
   * `claimit.command.claimit.subclaim.permission.list`
   * `claimit.command.claimit.subclaim.setname`
   * `claimit.command.claimit.subclaim.toggle`

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
   * `claimit.command.claimit.subclaim.delete.others` - Allow deleting other's subclaims as admin
   * `claimit.command.claimit.subclaim.deleteall.others` - Allow deleting all of a claim's subclaims as admin
   * `claimit.command.claimit.subclaim.info.others` - Allow getting info for subclaims in claims via true name as admin. Note you can sitll view info at a given location inside a claim.
   * `claimit.command.claimit.subclaim.permission.others` - Allow editing members on any subclaim as admin
   * `claimit.command.claimit.subclaim.permission.list.others` - Allow viewing members on any subclaim as admin
   * `claimit.command.claimit.subclaim.setname.others` - Allow setting the name of any subclaim as admin
   * `claimit.command.claimit.subclaim.toggle.others` - Allow editing of toggles on any subclaim as admin.

I'll say it again.<br>
**You will need to give users access to** `claimit.claim.create` and `claimit.subclaim.create` **when using Sponge!**

### Administration
ClaimIt provides an abundance of administration tools, namely the ability to bypass protections and modify claim toggles, members, etc. Read the admin permissions above for a general idea of what you'll need.
In order to use ANY admin features you will need admin enabled. To do so, run `/claimit admin`. So long as this is enabled and you have `claimit.claim.manage.others`, you can bypass protections.
Setting block limits is very easy, and can be done via the `/claimit claimblocks (setallowed/addallowed) (player) (amount)` command, so long as you have permission.

### Configuration
ClaimIt provides many configurable features. Currently, there are three configs, all of which are in the config folder.

#### API Configuration 1 (claimit_api.cfg)
This configuration allows forcing values to toggles in all claims. This means you can for example force `pickup_item` to allowed (true) in all claims. Here's an example configuration for that:

```json
claim_permissions {
    pickup_item {
        # Set true to force pickup_item in claims to the value of 'force_pickup_item_value' [default: false]
        B:do_force_pickup_item_value=true

        # Set to whatever value you want this to be if 'do_force_pickup_item_value' is true [default: true]
        B:force_pickup_item_value=true
    }
}
```

Setting `do_force_toggle_name_value` to true will ensure that all claims have `toggle_name` set to whatever the value of `force_toggle_name_value` is.

#### API Configuration 2 (claimitapi-2.cfg)

Currently this configuration contains only one value.

```json
general {
    # Enable or disable the entire subclaim system
    B:enable_subclaims=true
}
```
Setting `enable_subclaims` to false will remove any existing subclaims and destroy the data, and prevent creation and interaction with subclaims, including `/claimit subclaim` and any subcommands.

#### ClaimIt Configuration (claimit.cfg)
This is the most useful configuration for most users. This allows setting a variety of values.

```json
general {
    # The amount of claim blocks to be rewarded to players every "claim_blocks_accrual_period" ticks
    I:claim_blocks_accrual_amount=0

    # The period, in ticks (1/20 of a second), at which "claim_blocks_accrual_amount" rewards will be seperated by. 0 to disable.
    # Min: 0
    # Max: 2147483647
    I:claim_blocks_accrual_period=0

    # Put here the item ID that you wish to use for claiming.
    S:claim_create_item=minecraft:shears

    # Should match the display name of the claiming item, this is what is shown to users in the base command menu.
    S:claim_create_item_display=Shears

    # The default maximum area a claim can be for non-admins, in square blocks. Default 40,000 sq blocks = 200 blocks x 200 blocks. This can be increased and decreased via the claimblocks command.
    # Min: 4
    # Max: 2147483647
    I:default_claim_max_area=40000
    B:enable_subclaims=true

    # Disables the ability to have any PVP in claims.
    B:forceNoPVPInClaim=false

    # Sets the maximum time borders can be shown with /claimit showborders. Please note each second is around 12 to 30 packets from the server to each player in order to show borders, therefore it is limited.
    # Min: 1
    # Max: 2147483647
    I:max_show_borders_seconds=30

    # Deletes chunks that do not have claims present when enabled. After all region data has been pruned, this option does nothing until the server is restarted. DO NOT USE THIS WITHOUT BACKUPS OR AN UNDERSTANDING OF WHAT YOU ARE DOING. THIS WILL DELETE ANYTHING THAT IS NOT WITHIN A CHUNK THAT HAS A CLAIM AND RETURN IT TO THE DEFAULT GENERATION. I AM NOT RESPONSIBLE FOR ANY LOSS OF DATA. DO NOT ASK ME IF YOU CAN UNDO THIS, YOU CANNOT.
    B:prune_unclaimed_chunks=false

    # Sets the cooldown in seconds between each use of show borders
    # Min: 0
    # Max: 2147483647
    I:show_borders_cooldown=60
}
```

`claim_blocks_accrual_amount` represents the amount of claimblocks that will be accrued per `claim_blocks_accrual_period`, given that both is greater than 0.
<br>`claim_blocks_accrual_period` is a number represented in ticks (1/20 of a second) in which claimblocks will be rewarded, based on the time in which the user joined, meaning the exact award time is different for each player.
<br>`claim_create_item` is the item ID that will be used for creating claims and subclaims. By default it is `minecraft:shears`, but it can be any valid item.
<br>`claim_create_display` is a piece of text that will be displayed as the claiming item when a user runs `/claimit`. It does not nessecarily have to match the actual item name, but it generally should so your users know what to do.
<br>`default_max_claim_area` is the amount of claimblocks in which users will start with.
<br>`forceNoPVPInClaim` is a boolean that when set to true will make all claims block PVP, regardless of any settings or memberships.
<br>`max_show_borders_seconds` is a number, in seconds, representing the length of time which `/claimit showborders` will display borders for. This is set at 30 for network performance reasons (each claim is ~12 to 30 packets per second).
<br>`prune_unclaimed_chunks` is a boolean that when enabled will regenerate (remove constructions/modifications) of any chunks that do not contain a claim. This is a HIGHLY destructive operation that fires exactly once after the server starts with this enabled. After completion, it should promptly be set back to false. I am NOT responsible for data loss from this option. Do not take this one lightly.
<br>`show_borders_cooldown` is a number, in seconds, represnting how long you must wait between using `/ci showborders`. Keep in mind the shorter this is the more network performance problems you may have.
