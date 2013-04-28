package de.secretcraft.galibri;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.secretcraft.galibri.listener.SignListener;
import de.secretcraft.galibri.mechanic.BuyPerms;
import de.secretcraft.galibri.mechanic.Gate;
import de.secretcraft.galibri.util.VaultManager;
import de.secretcraft.galibri.util.Teleporter;

/**
 * @author Sascha Thiel<br>
 * Max Heller
 */
public class GalibriPlugin extends JavaPlugin {
	private Teleporter tele = new Teleporter();

	@Override
	public void onLoad() {
		saveDefaultConfig();
		Gate.reloadConfig(this);
		BuyPerms.reloadConfig(this);
	}

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SignListener(this), this);
		tele.init();
		try {
			VaultManager.setupPermissions();
			VaultManager.setupEconomy();
		} catch(Exception e) {}
	}

	@Override
	public void onDisable() {

	}
	
	public Teleporter getTeleporter() {
		return tele;
	}
}