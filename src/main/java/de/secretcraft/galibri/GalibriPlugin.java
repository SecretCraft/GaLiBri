package de.secretcraft.galibri;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.secretcraft.galibri.listener.SignListener;
import de.secretcraft.galibri.mechanic.Gate;

/**
 * 
 * 
 * @author sascha thiel
 */
public class GalibriPlugin extends JavaPlugin
{	
	//---------------------------------------------------------------------------------------------
	
	
	
	//---------------------------------------------------------------------------------------------
	
	@Override
	public void onLoad()
	{
		saveDefaultConfig();
		Gate.reloadConfig(this);
	}
	
	//---------------------------------------------------------------------------------------------	
	
	@Override
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SignListener(this), this);
	}
	
	//---------------------------------------------------------------------------------------------

	@Override
	public void onDisable()
	{
		
	}

	//---------------------------------------------------------------------------------------------
}