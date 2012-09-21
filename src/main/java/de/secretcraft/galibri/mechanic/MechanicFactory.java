package de.secretcraft.galibri.mechanic;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * Creates new mechanics depends on the second line of the sign
 * 
 * @author sascha thiel
 */
public class MechanicFactory
{
	//---------------------------------------------------------------------------------------------
	
	public static AbstractMechanic getMechanic(String cmdLine, GalibriPlugin plugin)
	{
		String value = cmdLine.toLowerCase();
		if(value.contains("lift")) return new Lift(plugin);
		else if(value.contains("gate")) return new Gate(plugin);
		else if(value.contains("bridge")) return new Bridge(plugin);
		return null;
	}
	
	//---------------------------------------------------------------------------------------------
}
