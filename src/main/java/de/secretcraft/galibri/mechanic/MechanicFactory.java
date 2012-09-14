package de.secretcraft.galibri.mechanic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * 
 * 
 * @author sascha thiel
 */
public class MechanicFactory
{
	//---------------------------------------------------------------------------------------------
	
	public static IMechanic getMechanic(String cmdLine, GalibriPlugin plugin)
	{
		Pattern p = Pattern.compile("(?:^\\[)(.*)(?:\\]$)");
		Matcher m = p.matcher(cmdLine);
		if(m.matches()){
			String value = m.group(1).toLowerCase();
			if(value.equalsIgnoreCase("lift up") || value.equalsIgnoreCase("lift down")) return new Lift(plugin);
			else if(value.equalsIgnoreCase("gate")) return new Gate(plugin);
			else if(value.equalsIgnoreCase("bridge")) return new Bridge(plugin);
		}
		return null;
	}
	
	//---------------------------------------------------------------------------------------------
}
