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
	import java.lang.Runnable;
	import java.lang.String;
	import java.sql.Timestamp;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.List;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.entity.EnderDragon;
	import org.bukkit.entity.EntityType;
	import org.bukkit.entity.Player;
	import org.bukkit.Location;
	import org.bukkit.scheduler.BukkitScheduler;
	import org.bukkit.World;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Spawner implements Runnable
{
	private EnderSpawn plugin;
	private int taskID;

	public Spawner(EnderSpawn plugin)
	{
		this.plugin = plugin;
		taskID = -1;
	}

	public void start()
	{
		if(taskID >= 0)
			return;

		Timestamp currentTime 	= new Timestamp(new Date().getTime());
		Timestamp lastDeath	= new Timestamp(currentTime.getTime());

		for(String key : this.plugin.data.lastDeath.keySet())
		{
			if(!this.plugin.data.lastDeath.containsKey(key))
				continue;

			Timestamp death = this.plugin.data.lastDeath.get(key);

			if(death.getTime() <= 0)
				continue;

			if(death.getTime() < lastDeath.getTime())
				lastDeath = death;
		}

		if(lastDeath.getTime() == currentTime.getTime())
			lastDeath = new Timestamp(0);

		long spawnMinutes = this.plugin.config.maxSpawnMinutes;

		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		if(currentTime.getTime() >= (lastDeath.getTime() + (spawnMinutes * 60000)))
		{
			taskID = scheduler.scheduleSyncDelayedTask(plugin, this, 200);
			return;
		}

		long timeRemaining = (lastDeath.getTime() + (spawnMinutes * 60000));
		timeRemaining -=  currentTime.getTime();
		long maxTicksRemaining = (timeRemaining / 50);

		long minSpawnMinutes = this.plugin.config.minSpawnMinutes;
		long minTicksRemaining = maxTicksRemaining - (minSpawnMinutes * 1200);
		long tickDifference = maxTicksRemaining - minTicksRemaining;

		long ticksRemaining = ((new Random()).nextLong() % tickDifference);
		ticksRemaining += minTicksRemaining;

		if(ticksRemaining < 200)
			ticksRemaining = 200;

		taskID = scheduler.scheduleSyncDelayedTask(plugin, this, ticksRemaining);
	}

	public void stop()
	{
		if(taskID < 0)
			return;

		plugin.getServer().getScheduler().cancelTask(taskID);
		taskID = -1;
	}

	public void run()
	{
		List<World> worlds = plugin.getServer().getWorlds();

		for (World world : worlds)
		{
			String worldName = world.getName().toUpperCase().toLowerCase();
			if(!plugin.config.worlds.containsKey(worldName))
				continue;

			if(!plugin.config.xCoords.containsKey(worldName))
				continue;

			if(!plugin.config.yCoords.containsKey(worldName))
				continue;

			if(!plugin.config.zCoords.containsKey(worldName))
				continue;

			List<Player> players = new ArrayList(world.getPlayers());
			if(players.size() <= 0)
				continue;

			boolean nearbyPlayer = false;

			int spawnX = plugin.config.xCoords.get(worldName);
			int spawnY = plugin.config.yCoords.get(worldName);
			int spawnZ = plugin.config.zCoords.get(worldName);

			Location location = new Location(world, spawnX, spawnY, spawnZ);
			for(Player player : players)
			{
				Location playerLocation = player.getLocation();
				int x = playerLocation.getBlockX();
				int z = playerLocation.getBlockZ();
				Location relativeLocation = new Location(world, x, spawnY, z);

				if(location.distance(relativeLocation) > 160)
					continue;

				nearbyPlayer = true;
				break;
			}

			if(!nearbyPlayer)
				continue;

			List dragons = new ArrayList(world.getEntitiesByClass(EnderDragon.class));

			int maxDragons = plugin.config.worlds.get(worldName);
			if(dragons.size() >= maxDragons)
				continue;

			int count = 0;

			if(count >= maxDragons)
				continue;

			Timestamp currentTime 	= new Timestamp(new Date().getTime());
			Timestamp lastDeath	= new Timestamp(0);

			if(this.plugin.data.lastDeath.containsKey(worldName))
				lastDeath = this.plugin.data.lastDeath.get(worldName);

			long spawnMinutes = this.plugin.config.minSpawnMinutes;

			if(currentTime.getTime() >= (lastDeath.getTime() + (spawnMinutes * 60000)))
			{
				world.spawnCreature(location, EntityType.ENDER_DRAGON);
				this.plugin.data.lastDeath.put(worldName, new Timestamp(0));
			}
		}
		stop();
	}
}
