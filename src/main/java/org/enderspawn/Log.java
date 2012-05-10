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
	import java.util.logging.Logger;
//* IMPORTS: BUKKIT
	import org.bukkit.ChatColor;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Log
{
    protected static final Logger log = Logger.getLogger("Minecraft");
    
    private static String format(String msg, Object ... args)
    {
        msg = ChatColor.stripColor(String.format(msg, args));
        msg = String.format("[%s] %s", EnderSpawn.pluginName, msg);
        return msg;
    }
    
    /**
     * Log an INFO message prefixed with the plugin name
     * 
     * @param format  A format string
     * @param args    Arguments corresponding to @param format
     **/
    public static void info(String format, Object ... args)
    {
        log.info(format(format, args));
    }
    
    /**
     * Log a WARNING message prefixed with the plugin name
     * 
     * @param format  A format string
     * @param args    Arguments corresponding to @param format
     **/
    public static void warning(String format, Object ... args)
    {
        log.warning(format(format, args));
    }
    
    /**
     * Log a SEVERE message prefixed with the plugin name
     * 
     * @param format  A format string
     * @param args    Arguments corresponding to @param format
     **/
    public static void severe(String format, Object ... args)
    {
        log.severe(format(format, args));
    }
}