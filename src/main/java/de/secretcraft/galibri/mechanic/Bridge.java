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
		permissions.put(Perm.INITIALIZE, "galibri.bridge.create");
		permissions.put(Perm.DO_ACTION, "galibri.bridge.use");
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(SignChangeEvent event)
	{
		if(!super.initialize(event)) return false;
		final Player player = event.getPlayer();
		event.setLine(1, "[Bridge]");
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(Sign sign, Player player)
	{
		if(!super.doAction(sign, player)) return false;
		
		player.sendMessage("Bridge => doAction "+ player.getName());
		
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
}
