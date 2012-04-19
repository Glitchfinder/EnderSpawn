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

import java.io.File;
import java.lang.String;
import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderSpawn extends JavaPlugin
{
	public static String pluginName = "EnderSpawn";
	
	public	Configuration		config;
	public	Spawner				spawner;
	private	EnderSpawnListener	listener;
	private	EnderSpawnCommand	command;
	
	public void onLoad()
	{
		File configurationFile	= new File(getDataFolder(), "config.yml");
		
		config					= new Configuration(configurationFile);
		listener				= new EnderSpawnListener(this);
		spawner					= new Spawner(this);
		command					= new EnderSpawnCommand(this);
	}
	
	public void onEnable()
	{
		config.load();
		listener.register();
		spawner.start();
		
		getCommand("enderspawn").setExecutor(command);
		
		Log.info("%s enabled.", getDescription().getVersion());
	}
	
	public void onDisable()
	{
		spawner.stop();
		config.save();
		
		Log.info("%s disabled.", getDescription().getVersion());
	}
	
	public void reload()
	{
		spawner.stop();
		config.load();
		spawner.start();
		
		Log.info("%s reloaded.", getDescription().getVersion());
	}
	
	public boolean hasPermission(CommandSender sender, String perm)
	{
		return hasPermission(sender, perm, true);
	}
	
	public boolean hasPermission(Player sender, String perm)
	{
		return hasPermission(sender, perm, true);
	}
	
	public boolean hasPermission(CommandSender sender, String perm, boolean message)
	{
		if(!(sender instanceof Player))
			return true;
		
		return hasPermission((Player) sender, perm, message);
	}
	
	public boolean hasPermission(Player sender, String perm, boolean message)
	{		
		if(sender.hasPermission(perm))
			return true;
		
		if(message)
			Message.severe((CommandSender) sender, "You don't have permission to do that!");
		
		return false;
	}
	
	public boolean status(CommandSender sender, boolean message, long playerTime)
	{		
		if(!(hasPermission(sender, "enderspawn.status", message)))
			return true;
		
		if(!(hasPermission(sender, "enderspawn.exp", false)))
			return true;
		
		long currentTime = new Date().getTime();
		long timeRemaining = (playerTime + (config.expResetMinutes * 60000)) - currentTime;
		
		if(timeRemaining <= 0)
		{
			Message.info(sender, "You can receive experience from the Ender Dragon.");
			return true;
		}
		
		long hours		= timeRemaining / 3600000;
		long minutes	= (timeRemaining % 3600000) / 60000;
		long seconds	= ((timeRemaining % 3600000) % 60000) / 1000;
		
		if(hours < 1 && minutes < 1 && seconds < 1)
		{
			Message.info(sender, "You can receive experience from the Ender Dragon.");
			return true;
		}
		else if(hours < 1 && minutes < 1 && seconds >= 1)
		{
			Message.info(sender, "You have %d seconds until you can receive experience from the Ender Dragon.", seconds);
			return true;
		}
		else if(hours < 1 && minutes >= 1)
		{
			String returnMessage = "You have %d minutes and %d seconds ";
			returnMessage += "until you can receive experience from the Ender Dragon.";
			Message.info(sender, returnMessage, minutes, seconds);
			return true;
		}
		else if(hours >= 1)
		{
			String returnMessage = "You have %d hours, %d minutes, and %d seconds ";
			returnMessage += "until you can receive experience from the Ender Dragon.";
			Message.info(sender, returnMessage, hours, minutes, seconds);
			return true;
		}
		
		return true;
	}
	
	public boolean status(Player sender, boolean message, long playerTime)
	{		
		return status((CommandSender) sender, message, playerTime);
	}
}
