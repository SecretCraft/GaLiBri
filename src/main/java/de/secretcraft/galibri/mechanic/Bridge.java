package de.secretcraft.galibri.mechanic;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;
import de.secretcraft.galibri.mechanic.AbstractMechanic.Perm;

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
		permissions.put(Perm.INITIALIZE, "bridge.create");
		permissions.put(Perm.DO_ACTION, "bridge.use");
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(SignChangeEvent event)
	{
		if(!super.initialize(event)) return false;
		
		Player player = event.getPlayer();
		player.sendMessage("Bridge => initialize "+ player.getName());
		
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
