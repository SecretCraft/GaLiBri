package de.secretcraft.galibri.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * If the plugin "Multiverse-Core" is installed on the server, this
 * class will load its teleport method dynamically. Else it will
 * use the standart Bukkit <code>player.teleport(location)</code> method.
 * 
 * @author Max Heller
 */
public class Teleporter {
	boolean useDefaultTeleport = true;
	Plugin mvPlugin = null;
	Method getSafeTTeleporter = null;
	Method safelyTeleport = null;

	/**
	 * Loads the methods:<br>
	 * * <code>getSafeTTeleporter</code><br>
	 * * <code>safelyTeleport</code><br>
	 * from the Multiverse-Core plugin.
	 */
	public void init() {
		try {
			mvPlugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
			if (mvPlugin == null) {
				return;
			}
			getSafeTTeleporter = mvPlugin.getClass().getMethod("getSafeTTeleporter");
			safelyTeleport = getSafeTTeleporter.invoke(mvPlugin).getClass()
							.getMethod("safelyTeleport");
			useDefaultTeleport = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Teleports the <code>player</code> to the <code>location</code>. Either uses
	 * Multiverse-Core for the teleport, or <code>player.teleport(location)</code>
	 * if the first did not work;
	 * 
	 * @param player
	 * @param location
	 * @throws GaLiBriException if the teleport fails.
	 */
	public void teleport(Player player, Location location) throws GaLiBriException {
		if (useDefaultTeleport) {
			if (!player.teleport(location))
				throw new GaLiBriException("failed to teleport"); // TODO: localize
			return;
		}
		try {
			Object saveTTeleporter = getSafeTTeleporter.invoke(mvPlugin);
			Object teleResult = safelyTeleport.invoke(saveTTeleporter,
					Bukkit.getConsoleSender(), player, location, true);
			if (!teleResult.toString().equals("SUCCESS")) {
				throw new GaLiBriException("teleport not successfull: "
						+ teleResult); // TODO: localize
			}
			return;
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {}
		this.useDefaultTeleport = true;
		this.teleport(player, location);
	}
}
