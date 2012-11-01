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
	
	public static AbstractMechanic getMechanic(String[] values, GalibriPlugin plugin)
	{
		if (values[1].toLowerCase().contains("lift")) return new Lift(plugin);
		else if (values[1].toLowerCase().contains("gate")) return new Gate(plugin);
		else if (values[1].toLowerCase().contains("bridge")) return new Bridge(plugin);
		else if (values[0].toLowerCase().contains("portal")) return new Portal(plugin);
		
		return null;
	}
	
	//---------------------------------------------------------------------------------------------
}
