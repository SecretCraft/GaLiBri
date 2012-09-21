package de.secretcraft.galibri.listener;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.secretcraft.galibri.GalibriPlugin;
import de.secretcraft.galibri.mechanic.AbstractMechanic;
import de.secretcraft.galibri.mechanic.MechanicFactory;

/**
 * 
 * 
 * @author sascha thiel
 */
public class SignListener implements Listener
{
	//---------------------------------------------------------------------------------------------
	
	private GalibriPlugin plugin;
	
	//---------------------------------------------------------------------------------------------
	
	public SignListener(GalibriPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	//---------------------------------------------------------------------------------------------
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event)
	{
		AbstractMechanic mech = MechanicFactory.getMechanic(event.getLine(1), plugin);
		if(mech != null){
			mech.initialize(event);
		}
	}
	
	//---------------------------------------------------------------------------------------------
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Material mat = event.getClickedBlock().getType();
		Action action = event.getAction();
		
		if(!(mat == Material.SIGN || mat == Material.SIGN_POST || mat == Material.WALL_SIGN)) return;
		if(action != Action.RIGHT_CLICK_BLOCK) return;
		
		Sign sign = (Sign) event.getClickedBlock().getState();
		
		AbstractMechanic mech = MechanicFactory.getMechanic(sign.getLine(1), plugin);
		if(mech != null){
			mech.doAction(sign, event.getPlayer());
		}
	}
	
	//---------------------------------------------------------------------------------------------
}
