package de.secretcraft.galibri.mechanic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import de.secretcraft.galibri.GalibriPlugin;
import de.secretcraft.galibri.util.VaultManager;

public class BuyPerms extends AbstractMechanic {
	private static Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public static void reloadConfig(final JavaPlugin plugin) {
		final YamlConfiguration conf = (YamlConfiguration) plugin.getConfig();
		ConfigurationSection mappings = conf.getConfigurationSection("buyperms.mappings");
		for(String key : mappings.getKeys(false)) {
			map.put(key.substring(0, Math.min(15, key.length())), mappings.getStringList(key));
		}
	}

	public BuyPerms(GalibriPlugin plugin) {
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.buyperms.create");
		permissions.put(Perm.DO_ACTION, "galibri.buyperms.use");
	}

	@Override
	public boolean initialize(final SignChangeEvent event) {
		if (!super.initialize(event)) {
			return false;
		}
		final Player player = event.getPlayer();
		if(map.get(event.getLine(1)) == null) {
			player.sendMessage(ChatColor.RED + " " + event.getLine(1) + " is not a valid mapping");
			event.setLine(0, "[Error]");
			event.setLine(1, "");
			event.setLine(2, "");
			event.setLine(3, "");
		} else {
			event.setLine(0, "[BuyPerms]");
			player.sendMessage(event.getLine(0) + " sign created");
		}
		return true;
	}
	
	@Override
	public boolean doAction(Sign sign, Player player) {
		if(!super.doAction(sign, player)) {
			return false;
		}
		
		String lastLine = sign.getLine(3);
		if(lastLine.length() > 2 && lastLine.startsWith("[") && lastLine.endsWith("]")) {
			String extraPerm = Perm.DO_ACTION + "." + lastLine.substring(1, lastLine.length() - 1).toLowerCase();
			plugin.debug(player, "Checking for extraperm " + extraPerm);
			if(!player.hasPermission(permissions.get(extraPerm))) {
				player.sendMessage(ChatColor.RED + "You don't have permissions to do that");
				return false;
			}
			plugin.debug(player, "You have the extraperm");
		}
		
		List<String> perms = map.get(sign.getLine(1));
		if(perms == null) {
			player.sendMessage("Sorry, but this sign is broken. The permission '" + sign.getLine(1) + "' doesn't exist.");
			return false;
		}
		
		for(String perm : perms) {
			if(player.hasPermission(perm)) {
				player.sendMessage("You already have the permission '" + sign.getLine(1) + "'");
				return false;
			}
		}
		
		try {
			double money = VaultManager.economy.getBalance(player.getName());
			double cost = Double.valueOf(sign.getLine(2));
			if(money < cost) {
				player.sendMessage("You don't have enought money to buy this permission.");
				return false;
			} else {
				for(String perm : perms) {
					PermissionsEx.getUser(player).addPermission(perm);
				}
				VaultManager.economy.withdrawPlayer(player.getName(), cost);
				player.sendMessage("You just bought the permission " + sign.getLine(1));
				return true;
			}
		} catch(NullPointerException npe) {
			player.sendMessage("Uhh sorry. Please contact an admin, this plugin seems to be broken.");
			return false;
		} catch(NumberFormatException nfe) {
			player.sendMessage("Sorry, but this sign is broken.");
			return false;
		}
	}
}
