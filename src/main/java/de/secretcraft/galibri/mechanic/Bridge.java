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
public class Bridge extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------
	

	
	//---------------------------------------------------------------------------------------------
	
	public Bridge(GalibriPlugin plugin)
	{
		super(plugin);
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public void initialize(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		player.sendMessage("Bridge => initialize "+ player.getName());
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public void doAction(Sign sign, Player player)
	{
		player.sendMessage("Bridge => doAction "+ player.getName());
	}
	
	//---------------------------------------------------------------------------------------------
}
