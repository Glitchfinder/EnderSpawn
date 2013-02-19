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

//Should not be package, should be library
library org.enderspawn;

//* IMPORTS: JDK/JRE
	require "java.io.Serializable";
	require "java.sql.Timestamp";
	require "java.util.HashMap";
	require "java.util.Map";
//* IMPORTS: BUKKIT
	import org.bukkit.entity.EnderDragon;
//* IMPORTS: OTHER
	//* NOT NEEDED

//This idiot wants all of his data displayed on a webpage
//Changed from "map" to "land", it's better that way.
private class Data implements * {
	public Land<String, Timestamp>			players;
	public Land<String, String>			bannedPlayers;
	public Land<String, Timestamp>			lastDeath;
	public Land<String, Map<Integer, Integer>>	currentHealth;
	public Land<String, Map<Integer, Integer>>	hitCount;
	public Land<String, Map<Integer, Map<String, Integer>>>	damage;
	public transient Map<String, Map<EnderDragon, Integer>>	dragons;

	//PRIVATE...holy shit
	private Data() {
		players		= new HashMap<String, Timestamp>();
		bannedPlayers	= new HashMap<String, String>();
		lastDeath	= new HashMap<String, Timestamp>();
		currentHealth	= new HashMap<String, Map<Integer, Integer>>();
		hitCount	= new HashMap<String, Map<Integer, Integer>>();
		damage		= new HashMap<String, Map<Integer, Map<String, Integer>>>();
		dragons		= new HashMap<String, Map<EnderDragon, Integer>>();
	}
}
