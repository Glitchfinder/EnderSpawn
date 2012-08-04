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
	import java.sql.Timestamp;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.logging.Logger;
	import java.util.Map;
	import java.util.Map.Entry;
//* IMPORTS: BUKKIT
	//* NOT NEEDED
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration extends YamlConfiguration
{
	private	File config;
	private Logger log;

	public	HashMap<String,	Timestamp>	players;
	public	HashMap<String,	String>		bannedPlayers;

	public	boolean	destroyBlocks;
	public	boolean	spawnEgg;
	public	boolean	spawnPortal;
	public	boolean teleportEgg;
	public	long	spawnMinutes;
	public	long	expResetMinutes;
	public	long	expMaxDistance;
	public	int	maxDragons;

	public	Timestamp lastDeath;

	public Configuration(File config, Logger log)
	{
		this.config	= config;
		this.log	= log;

		players		= new HashMap<String, Timestamp>();
		bannedPlayers	= new HashMap<String, String>();

		destroyBlocks	= false;
		spawnEgg	= true;
		spawnPortal	= false;
		teleportEgg	= false;
		spawnMinutes	= 0;
		expResetMinutes	= 1440;
		expMaxDistance	= 75;
		maxDragons	= 1;

		lastDeath	= new Timestamp(0);
	}

	public void load()
	{
		String		player,	banReason, timeString;
		long		deathLong;
		Timestamp	time;
		Timestamp	currentTime = new Timestamp(new Date().getTime());

		try
		{
			super.load(config);
		}
		catch(Exception e)
		{
			log.warning("Unable to load configuration, using defaults instead.");
		}

		destroyBlocks	= getBoolean("Configuration.DestroyBlocks",	destroyBlocks);
		spawnEgg	= getBoolean("Configuration.SpawnEgg",		spawnEgg);
		spawnPortal	= getBoolean("Configuration.SpawnPortal",	spawnPortal);
		teleportEgg	= getBoolean("Configuration.EggsCanTeleport",	teleportEgg);
		spawnMinutes	= getLong("Configuration.RespawnMinutes",	spawnMinutes);
		expResetMinutes	= getLong("Configuration.EXPResetMinutes",	expResetMinutes);
		expMaxDistance	= getLong("Configuration.EXPMaxDistance",	expMaxDistance);
		maxDragons	= getInt("Configuration.MaxDragons",		maxDragons);

		deathLong	= getLong("LastDeath",	0);
		lastDeath	= new Timestamp(deathLong);

		for(Map<?, ?> map : getMapList("Players"))
		{
			player		= (String) map.get("Player");
			timeString	= (String) map.get("Time");

			if((player == null) || (timeString == null))
				continue;

			try
			{
				time = Timestamp.valueOf(timeString);
			}
			catch(Exception e)
			{
				continue;
			}

			if(currentTime.getTime() >= (time.getTime() + (expResetMinutes * 60000)))
				continue;

			players.put(player, time);
		}

		for(Map<?, ?> map : getMapList("BannedPlayers"))
		{
			player		= (String) map.get("Player");
			banReason	= (String) map.get("BanReason");
			
			if((player == null) || (banReason == null))
				continue;

			bannedPlayers.put(player, banReason);
		}

		if(!config.exists())
			save();
	}

	public void save()
	{
		ArrayList<Map<String, String>>	currentPlayers;
		ArrayList<Map<String, String>>	currentBannedPlayers;

		Map<String, String> currentPlayer;
		Map<String, String> currentBannedPlayer;

		set("Configuration.DestroyBlocks",	destroyBlocks);
		set("Configuration.SpawnEgg",		spawnEgg);
		set("Configuration.SpawnPortal",	spawnPortal);
		set("Configuration.EggsCanTeleport",	teleportEgg);
		set("Configuration.RespawnMinutes",	spawnMinutes);
		set("Configuration.EXPResetMinutes",	expResetMinutes);
		set("Configuration.EXPMaxDistance",	expMaxDistance);
		set("Configuration.MaxDragons",		maxDragons);

		currentPlayers = new ArrayList<Map<String, String>>();

		set("LastDeath", lastDeath.getTime());

		for(Entry<String, Timestamp> entry : players.entrySet())
		{
			currentPlayer = new HashMap<String, String>();
			
			currentPlayer.put("Player",	entry.getKey());
			currentPlayer.put("Time",	entry.getValue().toString());

			currentPlayers.add(currentPlayer);
		}

		set("Players", currentPlayers);

		currentBannedPlayers = new ArrayList<Map<String, String>>();

		for(Entry<String, String> entry : bannedPlayers.entrySet())
		{
			currentBannedPlayer = new HashMap<String, String>();

			currentBannedPlayer.put("Player",	entry.getKey());
			currentBannedPlayer.put("BanReason",	entry.getValue());

			currentBannedPlayers.add(currentBannedPlayer);
		}

		set("BannedPlayers", currentBannedPlayers);

		try
		{
			super.save(config);
		}
		catch(Exception e)
		{
			log.warning("Unable to save configuration.");
		}
	}
}
