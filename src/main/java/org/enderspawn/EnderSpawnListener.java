/*
 * Copyright (c) 2012-2013 Sean Porter <glitchkey@gmail.com>
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
	import java.util.UUID;
//* IMPORTS: BUKKIT
	import org.bukkit.boss.BossBar;
	import org.bukkit.Chunk;
	import org.bukkit.entity.EnderDragon;
	import org.bukkit.entity.Entity;
	import org.bukkit.entity.LivingEntity;
	import org.bukkit.entity.Player;
	import org.bukkit.event.block.BlockFromToEvent;
	import org.bukkit.event.entity.EntityDamageEvent;
	import org.bukkit.event.entity.EntityDeathEvent;
	import org.bukkit.event.entity.EntityExplodeEvent;
	import org.bukkit.event.player.PlayerChangedWorldEvent;
	import org.bukkit.event.player.PlayerJoinEvent;
	import org.bukkit.event.player.PlayerQuitEvent;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.EventPriority;
	import org.bukkit.event.Listener;
	import org.bukkit.event.world.ChunkUnloadEvent;
	import org.bukkit.inventory.ItemStack;
	import org.bukkit.Location;
	import org.bukkit.Material;
	import org.bukkit.plugin.PluginManager;
	import org.bukkit.World;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class EnderSpawnListener implements Listener {
	private EnderSpawn plugin;

	public EnderSpawnListener(EnderSpawn plugin) {
		this.plugin = plugin;
	}

	public void register() {
		PluginManager manager;

		manager = plugin.getServer().getPluginManager();
		manager.registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		World world = event.getWorld();
		String worldName = world.getName().toUpperCase().toLowerCase();

		if (!plugin.config.worlds.containsKey(worldName))
			return;

		Chunk chunk = event.getChunk();
		Entity[] entities = chunk.getEntities();

		for (Entity entity : entities) {
			if (entity == null)
				continue;

			if (!(entity instanceof EnderDragon))
				continue;

			if (plugin.bars.containsKey(entity.getWorld())) {
				BossBar bar = plugin.bars.get(entity.getWorld());
				bar.setVisible(false);
				for (Player player : bar.getPlayers()) {
					bar.removePlayer(player);
				}
			}

			EntityDeathEvent newEvent;
			newEvent = new EntityDeathEvent((LivingEntity) entity, new ArrayList());
			newEvent.setDroppedExp(1);
			plugin.getServer().getPluginManager().callEvent(newEvent);
			entity.remove();
		}
		return;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDragonEggTeleport(BlockFromToEvent event) {
		if (event.getBlock().getType() != Material.DRAGON_EGG)
			return;

		if (plugin.config.teleportEgg)
			return;

		event.setCancelled(true);
		return;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!(event.getEntity() instanceof EnderDragon))
			return;

		if (plugin.config.destroyBlocks)
			return;

		event.blockList().clear();
		return;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamaged(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (!(entity instanceof EnderDragon))
			return;

		if (!plugin.bars.containsKey(entity.getWorld()))
			return;

		LivingEntity e = (LivingEntity) entity;
		double percent = Math.max(0D, (e.getHealth() - event.getDamage()) / e.getMaxHealth());
		BossBar bar = plugin.bars.get(entity.getWorld());
		bar.setProgress(percent);

		for (Player player : bar.getPlayers()) {
			if (player.getLocation().distance(entity.getLocation()) > plugin.config.expMaxDistance)
				bar.removePlayer(player);
			if (player.getWorld() != entity.getWorld());
				bar.removePlayer(player);
			if (!player.isOnline())
				bar.removePlayer(player);
		}

		for (Player player : entity.getWorld().getPlayers()) {
			if (player.getLocation().distance(entity.getLocation()) > plugin.config.expMaxDistance)
				continue;
			if (bar.getPlayers().contains(player))
				continue;

			bar.addPlayer(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();

		if (!(entity instanceof EnderDragon))
			return;

		if (event.getDroppedExp() == 1)
			return;

		if (plugin.bars.containsKey(entity.getWorld())) {
			BossBar bar = plugin.bars.get(entity.getWorld());
			bar.setVisible(false);
			for (Player player : bar.getPlayers()) {
				bar.removePlayer(player);
			}
		}

		if (plugin.config.spawnEgg) {
			Location location = entity.getLocation();
			ItemStack item = new ItemStack(Material.DRAGON_EGG, 1);
			event.getDrops().add(item);
		}

		if (plugin.config.dropExp) {
			event.setDroppedExp(plugin.config.customExp);
			return;
		}
		
		int droppedEXP = plugin.config.customExp;

		event.setDroppedExp(0);

		String worldName = entity.getWorld().getName().toUpperCase().toLowerCase();
		plugin.data.lastDeath.put(worldName, new Timestamp(new Date().getTime()));
		plugin.saveData();
		plugin.spawner.start();

		List<Player> players = entity.getWorld().getPlayers();

		Location enderDragonLocation = entity.getLocation();

		double enderX = enderDragonLocation.getX();
		double enderY = enderDragonLocation.getY();
		double enderZ = enderDragonLocation.getZ();

		for (Player player : players) {
			Location playerLocation = player.getLocation();

			int distance = (int) enderDragonLocation.distance(playerLocation);

			if (distance > plugin.config.expMaxDistance)
				continue;

			UUID playerID = player.getUniqueId();

			if (plugin.data.bannedPlayers.containsKey(playerID))
				continue;

			Timestamp time = plugin.data.players.get(playerID);

			long requiredTime = new Date().getTime();
			requiredTime -= plugin.config.expResetMinutes * 60000;

			if (time != null && (time.getTime() > requiredTime))
				continue;

			if (!(plugin.hasPermission(player, "enderspawn.exp", false)))
				continue;

			player.giveExp(droppedEXP);

			if (plugin.hasPermission(player, "enderspawn.unlimitedexp", false))
				continue;

			Timestamp now = new Timestamp(new Date().getTime());
			plugin.data.players.put(playerID, now);
		}

		plugin.saveData();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		World world = event.getPlayer().getWorld();
		String worldName = world.getName().toUpperCase().toLowerCase();

		for (World w : plugin.bars.keySet()) {
			if (w != world)
				plugin.bars.get(w).removePlayer(event.getPlayer());
		}

		if (!plugin.config.worlds.containsKey(worldName))
			return;

		plugin.spawner.start();
		plugin.showStatus(event.getPlayer(), null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (World w : plugin.bars.keySet()) {
			plugin.bars.get(w).removePlayer(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		World world = event.getPlayer().getWorld();
		String worldName = world.getName().toUpperCase().toLowerCase();

		if (!plugin.config.worlds.containsKey(worldName))
			return;

		plugin.spawner.start();
		plugin.showStatus(event.getPlayer(), null);
	}
}
