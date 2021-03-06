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
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.InputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;
	import java.io.OutputStream;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.logging.Logger;
	import java.util.Map;
	import java.util.UUID;
//* IMPORTS: BUKKIT
	import org.bukkit.boss.BossBar;
	import org.bukkit.Bukkit;
	import org.bukkit.command.CommandSender;
	import org.bukkit.entity.Player;
	import org.bukkit.OfflinePlayer;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.World;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class EnderSpawn extends JavaPlugin {
	public static final String pluginName = "EnderSpawn";

	public	Configuration		config;
	public	Data			data;
	public	Spawner			spawner;
	private	EnderSpawnListener	listener;
	private	EnderSpawnCommand	command;
	public	Logger			log;
	public	Map<World, BossBar>	bars;

	public void onLoad() {
		this.log	= this.getLogger();
		this.data	= new Data();
		loadData();

		bars = new HashMap<World, BossBar>();
		copyConfig("config.yml");
		File configurationFile = new File(getDataFolder(), "config.yml");
		this.config	= new Configuration(configurationFile, log, this);

		this.listener	= new EnderSpawnListener(this);
		this.spawner	= new Spawner(this);
		this.command	= new EnderSpawnCommand(this);
	}

	public void onEnable() {
		this.config.load();
		this.listener.register();
		this.spawner.start();

		getCommand("enderspawn").setExecutor(command);
	}
	
	public void onDisable() {
		this.spawner.stop();
		saveData(false);
	}

	public void reload() {
		this.spawner.stop();
		saveData(false);
		this.config.load();
		this.spawner.start();

		this.log.info(getDescription().getVersion() + " reloaded.");
	}

	public boolean hasPermission(CommandSender sender, String perm) {
		return hasPermission(sender, perm, true);
	}

	public boolean hasPermission(Player sender, String perm) {
		return hasPermission(sender, perm, true);
	}

	public boolean hasPermission(CommandSender sender, String perm, boolean reply) {
		if (!(sender instanceof Player))
			return true;
		
		return hasPermission((Player) sender, perm, reply);
	}

	public boolean hasPermission(Player sender, String perm, boolean reply) {		
		if (sender.hasPermission(perm))
			return true;

		if (reply) {
			String message = "You don't have permission to do that!";
			Message.severe((CommandSender) sender, message);
		}

		return false;
	}

	public boolean status(CommandSender sender, long time, String name) {
		String pronoun = "You";
		String pronoun2 = "you";
		boolean other = false;

		if (name != null) {
			pronoun = name;
			pronoun2 = "they";
			other = true;
		}

		String playerName = (name == null) ? sender.getName() : name;
		OfflinePlayer check = Bukkit.getOfflinePlayer(playerName);

		if(!check.hasPlayedBefore()) {
			String message = "The player '" + playerName;
			message += "' does not seem to exist.";
			Message.info(sender, message);
			return false;
		}

		UUID id = check.getUniqueId();

		if (data.bannedPlayers.get(id) != null) {
			String message = pronoun + (other ? " is " : " are ");
			message += "not allowed to receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}

		if (!(hasPermission(sender, "enderspawn.exp", false))) {
			String message = pronoun + (other ? " is " : " are ");
			message += "not allowed to receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}

		long currentTime = new Date().getTime();
		long timeRemaining = (time + (config.expResetMinutes * 60000));
		timeRemaining -= currentTime;

		if (timeRemaining <= 0) {
			String message = pronoun + " can receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}

		long hours	= timeRemaining / 3600000;
		long minutes	= (timeRemaining % 3600000) / 60000;
		long seconds	= ((timeRemaining % 3600000) % 60000) / 1000;

		if (hours < 1 && minutes < 1 && seconds < 1) {
			String message = pronoun + " can receive experience from the Ender Dragon.";
			Message.info(sender, message);
			return true;
		}
		else if (hours < 1 && minutes < 1 && seconds >= 1) {
			String message = pronoun + (other ? " has " : " have ");
			message += "%d seconds until " + pronoun2 + " can receive experience from ";
			message += "the Ender Dragon.";
			Message.info(sender, message, seconds);
			return true;
		}
		else if (hours < 1 && minutes >= 1) {
			String message = pronoun + (other ? " has " : " have ");
			message += "%d minutes and %d seconds until " + pronoun2 + " can receive ";
			message += "experience from the Ender Dragon.";
			Message.info(sender, message, minutes, seconds);
			return true;
		}
		else if (hours >= 1) {
			String message = pronoun + (other ? " has " : " have ");
			message += "%d hours, %d minutes, and %d seconds until " + pronoun2;
			message += " can receive experience from the Ender Dragon.";
			Message.info(sender, message, hours, minutes, seconds);
			return true;
		}

		return true;
	}

	public boolean status(Player sender, long time, String name) {		
		return status((CommandSender) sender, time, name);
	}

	public boolean showStatus(Player player, String name) {
		String playerName = (name == null) ? player.getName() : name;
		String caselessPlayerName = playerName.toUpperCase().toLowerCase();
		OfflinePlayer check = Bukkit.getOfflinePlayer(caselessPlayerName);

		if(!check.hasPlayedBefore()) {
			String message = "The player '" + playerName;
			message += "' does not seem to exist.";
			Message.info(player, message);
			return false;
		}

		UUID id = check.getUniqueId();

		long time = 0;

		if (data.players.get(id) != null)
			time = data.players.get(id).getTime();

		return status(player, time, name);
	}

	public void loadData() {
		File dataFile		= null;
		FileInputStream fis	= null;
		ObjectInputStream in	= null;

		try {
			dataFile = new File(getDataFolder(), "Data.bin");

			if (!dataFile.exists())
				return;

			fis	= new FileInputStream(dataFile);
			in	= new ObjectInputStream(fis);
			data	= (Data) in.readObject();
			log.info("Successfully loaded all data.");
		}
		catch (Exception e) {
			this.log.info("Unable to read the data file. It may be corrupt.");
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (Exception e) {}
			}

			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {}
			}
		}

		saveData();
	}

	public void saveData(boolean silent) {
		File dataFile		= null;
		FileOutputStream fos	= null;
		ObjectOutputStream out	= null;

		try {
			dataFile	= new File(getDataFolder(), "Data.bin");
			fos		= new FileOutputStream(dataFile);
			out		= new ObjectOutputStream(fos);
			out.writeObject(data);

			if (!silent)
				this.log.info("Successfully saved all data.");
		}
		catch (Exception e) {
			log.info("Unable to save the data file. It may be corrupt.");
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				}
				catch (Exception e) {}
			}

			if (out != null) {
				try {
					out.close();
				}
				catch (Exception e) {}
			}
		}
	}

	public void saveData() {
		this.saveData(true);
	}

	public boolean copyConfig(String filename) {
		File sourceFile		= null;
		File destinationFile	= null;
		InputStream is		= null;
		OutputStream out	= null;

		try {
			if (!getDataFolder().exists())
				getDataFolder().mkdirs();

			destinationFile = new File(getDataFolder(), filename);

			if (!destinationFile.createNewFile())
				return false;

			is		= getClass().getResourceAsStream("/" +  filename);
			out		= new FileOutputStream(destinationFile);
			byte buffer[]	= new byte[1024];
			int length;

			while((length = is.read(buffer)) > 0)
				out.write(buffer, 0, length);

			return true;
		}
		catch (Exception e) {
			log.warning("Unable to copy " + filename + " to the plugin directory.");
			return false;
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {}
			}

			if (out != null) {
				try {
					out.close();
				}
				catch (Exception e) {}
			}
		}
	}
}
