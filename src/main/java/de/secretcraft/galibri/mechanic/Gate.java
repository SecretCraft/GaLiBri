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
public class Gate extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------
	
	
	
	//---------------------------------------------------------------------------------------------
	
	public Gate(GalibriPlugin plugin)
	{
		super(plugin);
		permissions.put(Perm.INITIALIZE, "gate.create");
		permissions.put(Perm.DO_ACTION, "gate.use");
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(SignChangeEvent event)
	{
		if(!super.initialize(event)) return false;
		final Player player = event.getPlayer();
		event.setLine(1, "[Gate]");
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(Sign sign, Player player) 
	{
		if(!super.doAction(sign, player)) return false;
		
		player.sendMessage("Gate => doAction "+ player.getName());
		
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
}
