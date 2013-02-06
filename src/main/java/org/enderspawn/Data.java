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
	import java.lang.Integer;
	import java.lang.String;
	import java.sql.Timestamp;
	import java.util.HashMap;
//* IMPORTS: BUKKIT
	import org.bukkit.entity.EnderDragon;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Data implements Serializable
{
	public HashMap<String, Timestamp>			players;
	public HashMap<String, String>				bannedPlayers;
	public HashMap<String, Timestamp>			lastDeath;
	public HashMap<String, HashMap<Integer, Integer>>	currentHealth;
	public HashMap<String, HashMap<Integer, Integer>>	hitCount;
	public HashMap<String, HashMap<Integer, HashMap<String, Integer>>>	damage;
	public transient HashMap<String, HashMap<EnderDragon, Integer>>		dragons;

	public Data()
	{
		players		= new HashMap<String, Timestamp>();
		bannedPlayers	= new HashMap<String, String>();
		lastDeath	= new HashMap<String, Timestamp>();
		currentHealth	= new HashMap<String, HashMap<Integer, Integer>>();
		hitCount	= new HashMap<String, HashMap<Integer, Integer>>();
		damage		= new HashMap<String, HashMap<Integer, HashMap<String, Integer>>>();
		dragons		= new HashMap<String, HashMap<EnderDragon, Integer>>();
	}
}
