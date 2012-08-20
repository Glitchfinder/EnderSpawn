/*
 * Copyright (c) 2012 Sean Porter <glitchkey@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.enderspawn;

//* IMPORTS: JDK/JRE
	import java.lang.String;
//* IMPORTS: BUKKIT
	import org.bukkit.command.Command;
	import org.bukkit.command.CommandExecutor;
	import org.bukkit.command.CommandSender;
	import org.bukkit.entity.Player;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class EnderSpawnCommand implements CommandExecutor
{
	private EnderSpawn plugin;

	public EnderSpawnCommand(EnderSpawn plugin)
	{
		this.plugin = plugin;
	}

	public boolean onCommand(
		CommandSender sender,
		Command command,
		String label,
		String[] args)
	{
		if(args.length < 1)
			return false;

		if(args[0].equalsIgnoreCase("reload"))
			return reload(sender);
		else if(args[0].equalsIgnoreCase("ban"))
			return ban(sender, args);
		else if(args[0].equalsIgnoreCase("unban"))
			return unban(sender, args);
		else if(args[0].equalsIgnoreCase("lookup"))
			return lookup(sender, args);
		else if(args[0].equalsIgnoreCase("status"))
			return status(sender, args);
		else if(args[0].equalsIgnoreCase("reset"))
			return reset(sender, args);

		return false;
	}

	private boolean reload(CommandSender sender)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.reload")))
			return true;

		if(sender instanceof Player)
			plugin.log.info(((Player)sender).getName() + ": /enderspawn reload");

		plugin.reload();
		Message.info(sender, "EnderSpawn was successfully reloaded.");

		return true;
	}

	private boolean ban(CommandSender sender, String[] args)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.ban")))
			return true;

		if(args.length < 3)
			return false;

		String reason = args[2];

		if(args.length > 3)
		{
			for(int i = 3; i < args.length; i++)
			{
				reason = reason + " " + args[i];
			}
		}

		if(sender instanceof Player)
		{
			String message = ((Player)sender).getName() + ": /enderspawn ban ";
			plugin.log.info(message + args[1] + " " + reason);
		}

		String player = args[1].toUpperCase().toLowerCase();
		plugin.data.bannedPlayers.put(player, reason);
		plugin.saveData();

		String message = "Banned " + args[1];
		message += " from receiving Ender Dragon experience: " + reason;

		Message.info(sender, message);

		return true;
	}

	private boolean unban(CommandSender sender, String[] args)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.ban")))
			return true;

		if(args.length < 2)
			return false;

		if(sender instanceof Player)
		{
			String message = ((Player)sender).getName() + ": /enderspawn unban ";
			plugin.log.info(message + args[1]);
		}

		String player = args[1].toUpperCase().toLowerCase();
		plugin.data.bannedPlayers.remove(player);
		plugin.saveData();

		String message = "Allowed " + args[1];
		message += " to receive Ender Dragon experience.";
		Message.info(sender, message);

		return true;
	}

	private boolean lookup(CommandSender sender, String[] args)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.lookup")))
			return true;

		if(args.length < 2)
			return false;

		if(sender instanceof Player)
		{
			String message = ((Player)sender).getName() + ": /enderspawn lookup ";
			plugin.log.info(message + args[1]);
		}

		String player = args[1].toUpperCase().toLowerCase();
		String reason = plugin.data.bannedPlayers.get(player);

		if(reason == null)
		{
			String message = args[1];
			message += " is not banned from receiving Ender Dragon experience.";
			Message.info(sender, message);
			return true;
		}

		Message.info(sender, args[1] + " is banned for: " + reason);
		return true;
	}

	private boolean status(CommandSender sender, String[] args)
	{
		if(args.length >= 2)
			return statusOther(sender, args);

		if(!(plugin.hasPermission(sender, "enderspawn.status")))
			return true;

		if(!(plugin.hasPermission(sender, "enderspawn.exp", false)))
		{
			String message = "You are not allowed to receive ";
			message += "Ender Dragon experience.";
			Message.info(sender, message);
		}

		if(sender instanceof Player)
			plugin.log.info(((Player)sender).getName() + ": /enderspawn status");

		return plugin.showStatus((Player) sender, null);
	}

	private boolean statusOther(CommandSender sender, String[] args)
	{
		if(args.length < 2)
			return false;

		if(!(plugin.hasPermission(sender, "enderspawn.status.other")))
			return true;

		if(sender instanceof Player)
		{
			String message = ((Player)sender).getName() + ": /enderspawn status ";
			plugin.log.info(message + args[1]);
		}

		return plugin.showStatus((Player) sender, args[1]);
	}

	private boolean reset(CommandSender sender, String[] args)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.reset")))
			return true;

		if(args.length < 2)
			return false;

		if(sender instanceof Player)
		{
			String message = ((Player)sender).getName() + ": /enderspawn reset ";
			plugin.log.info(message + args[1]);
		}

		String player = args[1].toUpperCase().toLowerCase();
		plugin.data.players.remove(player);
		plugin.saveData();

		String message = "Allowed " + args[1];
		message += " to receive Ender Dragon experience.";
		Message.info(sender, message);

		return true;
	}
}
