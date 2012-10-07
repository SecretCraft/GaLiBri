package de.secretcraft.galibri.mechanic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * This class handles all Gate signs that will be created
 * 
 * @author sascha thiel
 */
public class Gate extends AbstractMechanic
{
	// ---------------------------------------------------------------------------------------------

	public final static String CONFIG_BLOCKS_PATH = "gate.blocks";
	public final static String CONFIG_ARCHWAY_BLOCK_PATH = "gate.archway-blocks";
	public final static String CONFIG_SEARCH_OFFSET = "gate.search-range";

	// ---------------------------------------------------------------------------------------------

	public Gate(final GalibriPlugin plugin)
	{
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.gate.create");
		permissions.put(Perm.DO_ACTION, "galibri.gate.use");
	}

	// ---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(final SignChangeEvent event)
	{
		if (!super.initialize(event))
			return false;
		final Player player = event.getPlayer();
		event.setLine(1, "[Gate]");
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}

	// ---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(final Sign sign, final Player player)
	{
		if (!super.doAction(sign, player))
			return false;

		List<Block> topBlocks = searchForTopBlocks(sign.getBlock());
		if (topBlocks.size() == 0) {
			player.sendMessage("no gate blocks found");
			return false;
		}
		toggleGate(topBlocks);
		return true;
	}

	// ---------------------------------------------------------------------------------------------

	protected List<Block> searchForTopBlocks(Block block)
	{
		List<Block> back = new ArrayList<Block>();
		final YamlConfiguration conf = (YamlConfiguration) plugin.getConfig();
		final Integer xOff = conf.getInt(CONFIG_SEARCH_OFFSET + ".x");
		final Integer yOff = conf.getInt(CONFIG_SEARCH_OFFSET + ".y");
		final Integer zOff = conf.getInt(CONFIG_SEARCH_OFFSET + ".z");

		final Location search = new Location(block.getWorld(), block.getX() - xOff, block.getY() + yOff, block.getZ()
				- zOff);
		final World world = block.getWorld();

		boolean foundGateBlock = false;

		for (int yLoc = 0; yLoc < yOff * 2; ++yLoc) {
			for (int xLoc = 0; xLoc < xOff * 2; ++xLoc) {
				for (int zLoc = 0; zLoc < zOff * 2; ++zLoc) {
					Block currentBlock = world.getBlockAt(search.getBlockX() + xLoc, search.getBlockY() - yLoc,
							search.getBlockZ() + zLoc);
					if (isGateBlock(currentBlock) && isInArchway(currentBlock)) {
						foundGateBlock = true;
						back.add(currentBlock);
					}
				}
			}
			// do not search in the next area if a gate block was found
			if (foundGateBlock)
				break;
		}
		return back;
	}

	// ---------------------------------------------------------------------------------------------

	protected void toggleGate(List<Block> topBlocks)
	{
		Material gateMat = topBlocks.get(0).getType();
		Material toggleMat = null;
		for (Block block : topBlocks) {
			boolean end = false;
			while (!end) {
				block = block.getRelative(BlockFace.DOWN);
				if (block.getType() == Material.AIR) {
					if (toggleMat == null)
						toggleMat = gateMat;
					block.setType(toggleMat);
				} else if (block.getType() == gateMat) {
					if (toggleMat == null)
						toggleMat = Material.AIR;
					block.setType(toggleMat);
				} else {
					end = true;
				}
			}
		}
	}

	// ---------------------------------------------------------------------------------------------

	protected boolean isGateBlock(final Block block)
	{
		List<Integer> blockList = plugin.getConfig().getIntegerList(CONFIG_BLOCKS_PATH);
		return blockList.contains(block.getType().getId());
	}

	// ---------------------------------------------------------------------------------------------

	protected boolean isInArchway(final Block block)
	{
		Block onTopOfIt = block.getRelative(BlockFace.UP);
		List<Integer> blockList = plugin.getConfig().getIntegerList(CONFIG_ARCHWAY_BLOCK_PATH);
		return blockList.contains(onTopOfIt.getType().getId());
	}

	// ---------------------------------------------------------------------------------------------
}
