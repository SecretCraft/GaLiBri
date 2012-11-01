package de.secretcraft.galibri.mechanic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
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
	
	protected enum Perm {
		INITIALIZE,
		DO_ACTION;
	}
	
	protected GalibriPlugin plugin;
	protected Map<Perm, String> permissions = new HashMap<Perm, String>();
	
	//---------------------------------------------------------------------------------------------
	
	protected AbstractMechanic(final GalibriPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	//---------------------------------------------------------------------------------------------
	
	protected Logger getLogger()
	{
		return plugin.getLogger();
	}
	
	//---------------------------------------------------------------------------------------------
	
	public boolean initialize(final SignChangeEvent event)
	{
		String perm = permissions.get(Perm.INITIALIZE);
		if(perm != null && !perm.isEmpty()){
			if(!event.getPlayer().hasPermission(perm)){
				// TODO: STH localize
				event.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to do that");
				event.setLine(1, "[not allowed]");
				return false;
			}
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------

	public boolean doAction(final Sign sign, final Player player)
	{
		String perm = permissions.get(Perm.DO_ACTION);
		if(perm != null && !perm.isEmpty()){
			if(!player.hasPermission(perm)){
				// TODO: STH localize
				player.sendMessage(ChatColor.RED + "You don't have permissions to do that");
				return false;
			}
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
}
