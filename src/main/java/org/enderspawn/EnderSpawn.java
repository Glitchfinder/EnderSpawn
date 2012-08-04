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

//* IMPORTS: JDK/JRE
	import java.io.File;
	import java.lang.String;
	import java.util.Date;
//* IMPORTS: BUKKIT
	import org.bukkit.command.CommandSender;
	import org.bukkit.entity.Player;
	import org.bukkit.plugin.java.JavaPlugin;
	import java.util.logging.Logger;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class EnderSpawn extends JavaPlugin
{
	public static String pluginName = "EnderSpawn";

	public	Configuration		config;
	public	Spawner			spawner;
	private	EnderSpawnListener	listener;
	private	EnderSpawnCommand	command;
	public	Logger			log;

	public void onLoad()
	{
		File configurationFile = new File(getDataFolder(), "config.yml");

		log		= this.getLogger();
		config		= new Configuration(configurationFile, log);
		listener	= new EnderSpawnListener(this);
		spawner		= new Spawner(this);
		command		= new EnderSpawnCommand(this);
	}

	public void onEnable()
	{
		config.load();
		listener.register();
		spawner.start();

		getCommand("enderspawn").setExecutor(command);
	}
	
	public void onDisable()
	{
		spawner.stop();
		config.save();
	}

	public void reload()
	{
		spawner.stop();
		config.load();
		spawner.start();

		log.info(getDescription().getVersion() + "%s reloaded.");
	}

	public boolean hasPermission(CommandSender sender, String perm)
	{
		return hasPermission(sender, perm, true);
	}

	public boolean hasPermission(Player sender, String perm)
	{
		return hasPermission(sender, perm, true);
	}

	public boolean hasPermission(CommandSender sender, String perm, boolean reply)
	{
		if(!(sender instanceof Player))
			return true;
		
		return hasPermission((Player) sender, perm, reply);
	}

	public boolean hasPermission(Player sender, String perm, boolean reply)
	{		
		if(sender.hasPermission(perm))
			return true;

		if(reply)
		{
			String message = "You don't have permission to do that!";
			Message.severe((CommandSender) sender, message);
		}

		return false;
	}

	public boolean status(CommandSender sender, long time, String name)
	{
		String pronoun = "You";
		String pronoun2 = "you";
		boolean other = false;

		if(name != null)
		{
			pronoun = name;
			pronoun2 = "they";
			other = true;
		}

		String playerName = (name == null) ? sender.getName() : name;

		if(config.bannedPlayers.get(playerName) != null)
		{
			String message = pronoun + (other ? " is " : " are ");
			message += "not allowed to receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}

		if(!(hasPermission(sender, "enderspawn.exp", false)))
		{
			String message = pronoun + (other ? " is " : " are ");
			message += "not allowed to receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}

		long currentTime = new Date().getTime();
		long timeRemaining = (time + (config.expResetMinutes * 60000));
		timeRemaining -= currentTime;

		if(timeRemaining <= 0)
		{
			String message = pronoun + " can receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}

		long hours	= timeRemaining / 3600000;
		long minutes	= (timeRemaining % 3600000) / 60000;
		long seconds	= ((timeRemaining % 3600000) % 60000) / 1000;

		if(hours < 1 && minutes < 1 && seconds < 1)
		{
			String message = pronoun + " can receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}
		else if(hours < 1 && minutes < 1 && seconds >= 1)
		{
			String message = pronoun + (other ? " has " : " have ");
			message += "%d seconds until " + pronoun2 + " can receive experience from ";
			message += "the Ender Dragon.";
			Message.info(sender, message, seconds);
			return true;
		}
		else if(hours < 1 && minutes >= 1)
		{
			String message = pronoun + (other ? " has " : " have ");
			message += "%d minutes and %d seconds until " + pronoun2 + " can receive ";
			message += "experience from the Ender Dragon.";
			Message.info(sender, message, minutes, seconds);
			return true;
		}
		else if(hours >= 1)
		{
			String message = pronoun + (other ? " has " : " have ");
			message += "%d hours, %d minutes, and %d seconds until " + pronoun2;
			message += " can receive experience from the Ender Dragon.";
			Message.info(sender, message, hours, minutes, seconds);
			return true;
		}

		return true;
	}

	public boolean status(Player sender, long time, String name)
	{		
		return status((CommandSender) sender, time, name);
	}

	public boolean showStatus(Player player, String name)
	{
		String playerName = (name == null) ? player.getName() : name;

		long time = 0;

		if(config.players.get(playerName) != null)
			time = config.players.get(playerName).getTime();

		return status(player, time, name);
	}
}
