package de.secretcraft.galibri.mechanic;

import java.util.logging.Logger;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * 
 * 
 * @author sascha thiel
 */
public abstract class AbstractMechanic
{
	//---------------------------------------------------------------------------------------------
	
	protected GalibriPlugin plugin;
	
	//---------------------------------------------------------------------------------------------
	
	public AbstractMechanic(GalibriPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	//---------------------------------------------------------------------------------------------
	
	protected Logger getLogger()
	{
		return plugin.getLogger();
	}
	
	//---------------------------------------------------------------------------------------------
	// abstract
	//---------------------------------------------------------------------------------------------
	
	public abstract void initialize(SignChangeEvent event);

	public abstract void doAction(Sign sign, Player player);
	
	//---------------------------------------------------------------------------------------------
}
