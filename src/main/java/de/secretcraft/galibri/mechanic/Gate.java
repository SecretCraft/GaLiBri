package de.secretcraft.galibri.mechanic;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * 
 * 
 * @author sascha thiel
 */
public class Gate implements IMechanic
{
	//---------------------------------------------------------------------------------------------
	
	private GalibriPlugin plugin;
	
	//---------------------------------------------------------------------------------------------
	
	public Gate(GalibriPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public void initialize(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		player.sendMessage("Gate => initialize "+ player.getName());
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public void doAction(Sign sign, Player player) 
	{
		player.sendMessage("Gate => doAction "+ player.getName());
	}
	
	//---------------------------------------------------------------------------------------------
}
