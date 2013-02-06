== EnderSpawn ==

  Download: http://ci.libnull.so/job/EnderSpawn/
  Source: https://github.com/Glitchfinder/EnderSpawn/
  Issue Tracker: https://github.com/Glitchfinder/EnderSpawn/issues/

Enderspawn is a simple idea, with a fully fleshed out implementation.
What if you want your players to battle the Ender Dragon more than once,
without having to use a command to spawn them, and without having to
regenerate the whole world? What if you don't want the Ender Dragon to
litter or destroy the world that created it? What if you wanted a fairer
experience distribution system, one that allows for all players to earn
the experience if they were close enough to kill it?

If those are things you've wanted, then this is the plugin for you! It
allows for all of this, as well as significantly more!

== Features ==

  * Prevents Ender Dragons from destroying blocks. (Can be toggled)

  * Prevents Ender Dragons from spawning a portal. (Can be toggled)

  * Prevents Ender Dragons from spawning an egg. (Can be toggled, will
    drop as an item if portals are disabled)

  * Creates a respawn timer for the Ender Dragon. (Set in minutes,
    0-whatever, per dragon)

  * Creates a cooldown timer that can prevent players from earning exp
    from an Ender Dragon. (Set in minutes, 0-whatever, per player)

  * Sets a cap on the number of Ender Dragons spawned at any given time
    by this plugin. (Per world, 0-whatever)

  * Sets a distance from the enderdragon within which players will earn
    the full amount of experience (Calculated in 3D, all players within
    sphere earn full normal Ender Dragon exp, which takes them from
    level 0 to level 105)

  * Sets a configurable amount of experience to earn from the
    EnderDragon, which defaults to the ingame default.

  * Allows admins to ban/unban players from earning exp from the Ender
    Dragon.

  * Uses Bukkit permissions.

  * Tracks Ender Dragon respawn time between server restarts.

  * Tracks player experience cooldown between server restarts.

  * Tracks player experience ban status between server restarts.

  * Allows players with a certain permission setting to bypass the
    experience cooldown.

  * Allows players to look up experience bans.

  * Allows players to look up their current experience cooldown status.

  * Allows players to lookup each other's current experience cooldown
    status.

  * Allows players with a specific permission to reset cooldowns.

  * Displays current cooldown status upon entering the end, or logging
    in while in the end.

  * Has a fully functional reload command that loads data directly from
    the config file, and is not broken like in a significant number of
    plugins.

  * Has almost no added overhead. This plugin basically does nothing,
    and even plugins that look much more minor have more overhead.

== Commands ==

  /enderspawn reload
  /es reload
    Reloads the configuration.

  /enderspawn ban [player] [reason]
  /es ban [player] [reason]
    Bans a player from receiving experience from the enderdragon.

  /enderspawn unban [player]
  /es unban [player]
    Allows a player banned from receiving experience from the
    enderdragon to get it again.

  /enderspawn lookup [player]
  /es lookup [player]
    Allows a player to lookup ban statuses for the plugin.

  /enderspawn status <player>
  /es status <player>
    Allows a player to lookup their experience status for the plugin.

  /enderspawn reset [player]
  /es reset [player]
    Allows a player to reset experience cooldowns.

== Permissions ==

  enderspawn.*
  enderspawn.admin
    Access to everything related to the EnderSpawn plugin.

  enderspawn.ban
    Access to the /enderspawn ban and /enderspawn unban commands.

  enderspawn.reload
    Access to the /enderspawn reload command.

  enderspawn.lookup
    Access to the /enderspawn lookup command.

  enderspawn.status
    Access to the /enderspawn status command.

  enderspawn.status.other
    Access to the /enderspawn status command, to look up other players.

  enderspawn.exp
    Access to the experience dropped by the EnderDragon.

  enderspawn.unlimitedexp
    Access to the experience dropped by the EnderDragon, without the
    cooldown.

  enderspawn.reset
    Access to the /enderspawn reset command.
