package de.secretcraft.galibri.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;
import de.secretcraft.galibri.util.GaLiBriException;

public class Portal extends AbstractMechanic {
	public Portal(GalibriPlugin plugin) {
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.portal.create");
		permissions.put(Perm.DO_ACTION, "galibri.portal.use");
	}

	@Override
	public boolean initialize(final SignChangeEvent event) {
		if (!super.initialize(event))
			return false;

		final Player player = event.getPlayer();

		if (validateSign(event.getLine(1))) {
			try {
				String s = event.getLine(0);
				event.setLine(0, "[Portal:" + s.substring(s.indexOf(':') + 1,
							  s.indexOf(']')).trim().toLowerCase() + "]");
			} catch (IndexOutOfBoundsException e) {
				event.setLine(0, "[Portal]");
			}

			player.sendMessage(event.getLine(0) + " Sign created.");
		} else {
			event.setLine(0, "[Error]");
			player.sendMessage(event.getLine(0) + " Invalid coordinates.");
		}

		return true;
	}

	@Override
	public boolean doAction(final Sign sign, final Player player) {
		if (!super.doAction(sign, player))
			return false;

		if (validateSign(sign.getLine(1))) {
			try {
				World targetWorld = null;
				String[] coordinates = sign.getLine(1).split(":");

				if (sign.getLine(2).startsWith("W=")) {
					String name = sign.getLine(2).substring(2).trim();
					targetWorld = Bukkit.getWorld(name);
					if (targetWorld == null)
						throw new GaLiBriException("\"" + name + "\" not found"); // TODO: localize
				}

				if (targetWorld == null) {
					targetWorld = player.getWorld();
				}

				// now teleport player
				// 0 .. x
				// 1 .. z
				// 2 .. y = height!
				Location newPlayerLocation = new Location(targetWorld,
						Double.parseDouble(coordinates[0]) + 0.5,
						Double.parseDouble(coordinates[2]),
						Double.parseDouble(coordinates[1]) + 0.5,
						getYawnValue(sign.getLine(0)), player.getLocation().getPitch());
				
				newPlayerLocation = getTeleportLocation(newPlayerLocation);

				plugin.getTeleporter().teleport(player, newPlayerLocation);
			} catch (GaLiBriException e) {
				player.sendMessage("[Portal] " + e.getMessage());
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean validateSign(String coordinateLine) {
		String[] coordinates = coordinateLine.split(":");
		return (coordinates.length == 3) ? validateCoordinates(coordinates[0],
				coordinates[1], coordinates[2]) : false;
	}

	private boolean validateCoordinates(String x, String z, String y) {
		try {
			Double.parseDouble(x);
			Double.parseDouble(z);
			return Double.parseDouble(y) >= 0D;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public float getYawnValue(String line) {
		try {
			String str = line.substring(line.indexOf(':') + 1, line.indexOf(']')).trim().toLowerCase();
			if (str.equals("north") || str.equals("norden")) {
				return 180F;
			}
			if (str.equals("east") || str.equals("osten")) {
				return 270F;
			}
			if (str.equals("south") || str.equals("süden") || str.equals("sueden")) {
				return 0F;
			}
			if (str.equals("west") || str.equals("westen")) {
				return 90F;
			}
			return Float.parseFloat(str);
		} catch (IndexOutOfBoundsException e) {
			return 180F;
		} catch (NumberFormatException e) {
			return 180F;
		}
	}
}
