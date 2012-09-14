package de.secretcraft.galibri.mechanic;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * 
 * 
 * @author sascha thiel
 */
public interface IMechanic
{
	//---------------------------------------------------------------------------------------------
	
	public abstract void initialize(SignChangeEvent event);
	
	public abstract void doAction(Sign sign, Player player);
	
	//---------------------------------------------------------------------------------------------
}