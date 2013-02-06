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
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Spawner implements Runnable {
	private EnderSpawn plugin;
	private int taskID;

	public Spawner(EnderSpawn plugin) {
		this.plugin = plugin;
		taskID = -1;
	}

	public void start() {
		if (taskID >= 0)
			return;

		Timestamp currentTime 	= new Timestamp(new Date().getTime());
		Timestamp lastDeath	= new Timestamp(currentTime.getTime());

		for (String key : this.plugin.data.lastDeath.keySet()) {
			if (!this.plugin.data.lastDeath.containsKey(key))
				continue;

			Timestamp death = this.plugin.data.lastDeath.get(key);

			if (death.getTime() <= 0)
				continue;

			if (death.getTime() < lastDeath.getTime())
				lastDeath = death;
		}

		if (lastDeath.getTime() == currentTime.getTime())
			lastDeath = new Timestamp(0);

		long spawnMinutes = this.plugin.config.maxSpawnMinutes;

		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		if (currentTime.getTime() >= (lastDeath.getTime() + (spawnMinutes * 60000))) {
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

		if (ticksRemaining < 200)
			ticksRemaining = 200;

		taskID = scheduler.scheduleSyncDelayedTask(plugin, this, ticksRemaining);
	}

	public void stop() {
		if (taskID < 0)
			return;

		plugin.getServer().getScheduler().cancelTask(taskID);
		taskID = -1;
	}

	public void run() {
		List<World> worlds = plugin.getServer().getWorlds();

		for (World world : worlds) {
			String worldName = world.getName().toUpperCase().toLowerCase();
			if (!plugin.config.worlds.containsKey(worldName))
				continue;

			if (!plugin.config.xCoords.containsKey(worldName))
				continue;

			if (!plugin.config.yCoords.containsKey(worldName))
				continue;

			if (!plugin.config.zCoords.containsKey(worldName))
				continue;

			List<Player> players = new ArrayList(world.getPlayers());
			if (players.size() <= 0)
				continue;

			boolean nearbyPlayer = false;

			int spawnX = plugin.config.xCoords.get(worldName);
			int spawnY = plugin.config.yCoords.get(worldName);
			int spawnZ = plugin.config.zCoords.get(worldName);

			Location location = new Location(world, spawnX, spawnY, spawnZ);

			for (Player player : players) {
				Location playerLocation = player.getLocation();
				int x = playerLocation.getBlockX();
				int z = playerLocation.getBlockZ();
				Location relativeLocation = new Location(world, x, spawnY, z);

				if (location.distance(relativeLocation) > 160)
					continue;

				nearbyPlayer = true;
				break;
			}

			if (!nearbyPlayer)
				continue;

			List dragons = new ArrayList(world.getEntitiesByClass(EnderDragon.class));

			int maxDragons = plugin.config.worlds.get(worldName);
			if (dragons.size() >= maxDragons)
				continue;

			int count = 0;

			if (count >= maxDragons)
				continue;

			Timestamp currentTime 	= new Timestamp(new Date().getTime());
			Timestamp lastDeath	= new Timestamp(0);

			if (this.plugin.data.lastDeath.containsKey(worldName))
				lastDeath = this.plugin.data.lastDeath.get(worldName);

			long spawnMinutes = this.plugin.config.minSpawnMinutes;

			if (currentTime.getTime() >= (lastDeath.getTime() + (spawnMinutes * 60000))) {
				world.spawnCreature(location, EntityType.ENDER_DRAGON);
				this.plugin.data.lastDeath.put(worldName, new Timestamp(0));
			}
		}
		stop();
	}
}
