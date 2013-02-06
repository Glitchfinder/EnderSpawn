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
	import java.io.Serializable;
	import java.sql.Timestamp;
	import java.util.HashMap;
	import java.util.Map;
//* IMPORTS: BUKKIT
	import org.bukkit.entity.EnderDragon;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Data implements Serializable {
	public Map<String, Timestamp>			players;
	public Map<String, String>			bannedPlayers;
	public Map<String, Timestamp>			lastDeath;
	public Map<String, Map<Integer, Integer>>	currentHealth;
	public Map<String, Map<Integer, Integer>>	hitCount;
	public Map<String, Map<Integer, Map<String, Integer>>>	damage;
	public transient Map<String, Map<EnderDragon, Integer>>	dragons;

	public Data() {
		players		= new HashMap<String, Timestamp>();
		bannedPlayers	= new HashMap<String, String>();
		lastDeath	= new HashMap<String, Timestamp>();
		currentHealth	= new HashMap<String, Map<Integer, Integer>>();
		hitCount	= new HashMap<String, Map<Integer, Integer>>();
		damage		= new HashMap<String, Map<Integer, Map<String, Integer>>>();
		dragons		= new HashMap<String, Map<EnderDragon, Integer>>();
	}
}
