/*
* Copyright (c) 2012 Sean Porter <glitchkey@gmail.com>
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge,
* publish, distribute, sublicense, and/or sell copies of the Software,
* and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.enderspawn;

import java.lang.String;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderSpawnCommand implements CommandExecutor
{
	private EnderSpawn plugin;
	
	public EnderSpawnCommand(EnderSpawn plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length < 1)
			return false;
		
		if(args[0].equalsIgnoreCase("reload"))
		{
			return reload(sender);
		}
		else if(args[0].equalsIgnoreCase("ban"))
		{
			return ban(sender, args);
		}
		else if(args[0].equalsIgnoreCase("unban"))
		{
			return unban(sender, args);
		}
		else if(args[0].equalsIgnoreCase("lookup"))
		{
			return lookup(sender, args);
		}
		else if(args[0].equalsIgnoreCase("status"))
		{
			return status(sender);
		}
		
		return false;
	}
	
	private boolean reload(CommandSender sender)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.reload")))
			return true;
		
		if(sender instanceof Player)
			Log.info(((Player)sender).getName() + ": /enderspawn reload");
		
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
			Log.info(((Player)sender).getName() + ": /enderspawn ban " + args[1] + " " + reason);
		
		plugin.config.bannedPlayers.put(args[1], reason);
		Message.info(sender, "Banned " + args[1] + " from receiving Ender Dragon experience: " + reason);
		
		return true;
	}
	
	private boolean unban(CommandSender sender, String[] args)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.ban")))
			return true;
		
		if(args.length < 2)
			return false;
		
		if(sender instanceof Player)
			Log.info(((Player)sender).getName() + ": /enderspawn unban " + args[1]);
		
		plugin.config.bannedPlayers.remove(args[1]);
		Message.info(sender, "Allowed " + args[1] + " to receive Ender Dragon experience.");
		
		return true;
	}
	
	private boolean lookup(CommandSender sender, String[] args)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.lookup")))
			return true;
		
		if(args.length < 2)
			return false;
		
		if(sender instanceof Player)
			Log.info(((Player)sender).getName() + ": /enderspawn lookup " + args[1]);
		
		String reason = plugin.config.bannedPlayers.get(args[1]);
		
		if(reason == null)
		{
			Message.info(sender, args[1] + " is not banned from receiving Ender Dragon experience.");
			return true;
		}
		
		Message.info(sender, args[1] + " is banned for: " + reason);
		return true;
	}
	
	private boolean status(CommandSender sender)
	{
		if(!(plugin.hasPermission(sender, "enderspawn.status")))
			return true;
		
		if(!(plugin.hasPermission(sender, "enderspawn.exp", false)))
			Message.info(sender, "You are not allowed to receive Ender Dragon experience.");
		
		if(sender instanceof Player)
			Log.info(((Player)sender).getName() + ": /enderspawn status");
		
		String playerName = ((Player) sender).getName();
		
		if(plugin.config.bannedPlayers.get(playerName) != null)
		{
			Message.info(sender, "You are not allowed to receive Ender Dragon experience.");
			return true;
		}
		
		long playerTime = 0;
		
		if(plugin.config.players.get(playerName) != null)
			playerTime = plugin.config.players.get(playerName).getTime();
		
		return plugin.status(sender, false, playerTime);
	}
}