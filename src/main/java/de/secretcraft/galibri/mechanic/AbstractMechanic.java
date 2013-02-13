package de.secretcraft.galibri.mechanic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;
import de.secretcraft.galibri.util.GaLiBriException;

/**
 * 
 * 
 * @author sascha thiel
 * @author Max Heller
 */
public abstract class AbstractMechanic {
	protected enum Perm {
		INITIALIZE, DO_ACTION;
	}

	protected GalibriPlugin plugin;
	protected Map<Perm, String> permissions = new HashMap<Perm, String>();

	protected AbstractMechanic(final GalibriPlugin plugin) {
		this.plugin = plugin;
	}

	protected Logger getLogger() {
		return plugin.getLogger();
	}
	
	public boolean initialize(final SignChangeEvent event) {
		String perm = permissions.get(Perm.INITIALIZE);
		if(perm != null && !perm.isEmpty()){
			if(!event.getPlayer().hasPermission(perm)){
				// TODO: STH localize
				event.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to do that");
				event.setLine(0, "");
				event.setLine(1, "[Not allowed]");
				event.setLine(2, "");
				event.setLine(3, "");
				return false;
			}
		}
		return true;
	}
	
	public boolean doAction(final Sign sign, final Player player) {
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
	
	protected Location getTeleportLocation(final Location location)
			throws GaLiBriException {
		Block destBlock = getFloor(location.getBlock().getRelative(BlockFace.DOWN));
		location.setY(destBlock.getRelative(BlockFace.UP).getY());
		location.setX(location.getBlockX() + 0.5);
		location.setZ(location.getBlockZ() + 0.5);
		return location;
	}
	
	private boolean isFreeArea(Block block) {
		// NOTE: STH search for free area to port the player
		for (int i = 0; i < 2; ++i) {
			block = block.getRelative(BlockFace.UP);
			if (isMassive(block))
				return false;
		}
		return true;
	}
	
	private Block getFloor(Block block) throws GaLiBriException {
		// NOTE: STH search for the floor 4 blocks downwards 
		boolean foundFloor = false;
		for (int i = 0; i < 4; ++i) {
			if (isMassive(block)) {
				foundFloor = true;
				if (isFreeArea(block))
					return block;
			}
			block = block.getRelative(BlockFace.DOWN);
		}
		if(foundFloor)
			throw new GaLiBriException("There is not enough space for you"); // TODO: localize
		else
			throw new GaLiBriException("No floor found");					 // TODO: localize
	}
	
	public boolean isMassive(final Block block) {
		// NOTE: STH if something is missing please add
		Material m = block.getType();
		return !(m == Material.AIR || m == Material.STONE_BUTTON
				|| m == Material.SIGN_POST || m == Material.TORCH
				|| m == Material.TRAP_DOOR || m == Material.REDSTONE_TORCH_ON
				|| m == Material.REDSTONE_TORCH_OFF || m == Material.VINE
				|| m == Material.REDSTONE_WIRE || m == Material.LADDER
				|| m == Material.WALL_SIGN || m == Material.CROPS
				|| block.isLiquid() || m == Material.LEVER
				|| m == Material.LONG_GRASS || m == Material.SNOW
				|| m == Material.SUGAR_CANE_BLOCK || m == Material.WOOD_PLATE
				|| m == Material.STONE_PLATE || m == Material.WOOD_BUTTON
				|| m == Material.RAILS || m == Material.DETECTOR_RAIL
				|| m == Material.POWERED_RAIL || m == Material.TRIPWIRE_HOOK
				|| m == Material.ITEM_FRAME || m == Material.PAINTING
				|| m == Material.SAPLING || m == Material.TRIPWIRE
				|| m == Material.YELLOW_FLOWER || m == Material.RED_ROSE
				|| m == Material.RED_MUSHROOM || m == Material.NETHER_WARTS
				|| m == Material.NETHER_STALK || m == Material.BROWN_MUSHROOM
				|| m == Material.PUMPKIN_STEM || m == Material.MELON_STEM);
	}
}
