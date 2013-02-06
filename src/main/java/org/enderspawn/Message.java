/* 
 * Copyright 2012 James Geboski <jgeboski@gmail.com>
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
	//* NOT NEEDED
//* IMPORTS: BUKKIT
	import org.bukkit.ChatColor;
	import org.bukkit.command.CommandSender;
	import org.bukkit.entity.Player;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Message {
	/**
	 * Send an INFO message to a CommandSender.  This method is the
	 * same as calling Message.toSender() but, let's keep the INFO,
	 * WARNING, SEVERE standard.
	 * 
	 * @param sender  the CommandSender
	 * @param format  A format string
	 * @param args    Arguments corresponding to @param format
	 **/
	public static void info(CommandSender sender, String format, Object ... args) {
		toSender(sender, format, args);
	}

	/**
	 * Send an WARNING message to a CommandSender.  If the CommandSender
	 * is a player, the message is highlighted yellow.  If the
	 * CommandSender is not a Player, this is same as Message.toSender()
	 * 
	 * @param sender  the CommandSender
	 * @param format  A format string
	 * @param args    Arguments corresponding to @param format
	 **/
	public static void warning(CommandSender sender, String format, Object ... args) {
		if (sender instanceof Player)
			format = ChatColor.YELLOW + format;

		toSender(sender, format, args);
	}

	/**
	 * Send a SEVERE message to a CommandSender.  If the CommandSender
	 * is a player, the message is highlighted in red.  If the
	 * CommandSender is not a Player, this is same as Message.toSender()
	 * 
	 * @param sender  the CommandSender
	 * @param format  A format string
	 * @param args    Arguments corresponding to @param format
	 **/
	public static void severe(CommandSender sender, String format, Object ... args) {
		if (sender instanceof Player)
			format = ChatColor.RED + format;

		toSender(sender, format, args);
	}

	/**
	 * Send a message to a CommandSender with the plugin name prefixed.
	 * If the CommandSender is not a Player, any ChatColors in the
	 * message will be stripped.
	 * 
	 * @param sende:  the CommandSender
	 * @param format  A format string
	 * @param args    Arguments corresponding to @param format
	 **/
	public static void toSender(CommandSender sender, String format, Object ... args) {
		String msg = String.format(format, args);

		if (sender instanceof Player) {
			msg = String.format("%s[%s]%s %s", ChatColor.DARK_AQUA,
			EnderSpawn.pluginName, ChatColor.WHITE, msg);
		} else {
			msg = String.format("[%s] %s ", EnderSpawn.pluginName, msg);
			msg = ChatColor.stripColor(msg);
		}

		sender.sendMessage(msg);
	}
}
