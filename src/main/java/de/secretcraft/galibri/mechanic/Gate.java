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
import org.bukkit.plugin.java.JavaPlugin;

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

	public static int xOffset = -1;
	public static int yOffset = -1;
	public static int zOffset = -1;
	
	public static void reloadConfig(final JavaPlugin plugin) {
		final YamlConfiguration conf = (YamlConfiguration) plugin.getConfig();
		xOffset = conf.getInt(CONFIG_SEARCH_OFFSET + ".x");
		yOffset = conf.getInt(CONFIG_SEARCH_OFFSET + ".y");
		zOffset = conf.getInt(CONFIG_SEARCH_OFFSET + ".z");
	}

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

	protected void toggleGate(List<Block> topBlocks)
	{
		for (Block block : topBlocks) {
			Material gateMat = block.getType();
			Material toggleMat = null;
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

	protected List<Block> searchForTopBlocks(Block block)
	{
		List<Block> back = new ArrayList<Block>();

		final Integer xOff = xOffset;
		final Integer yOff = yOffset;
		final Integer zOff = zOffset;

		final Location search = new Location(block.getWorld(), block.getX() - xOff, block.getY() + yOff, block.getZ()
				- zOff);
		final World world = block.getWorld();

		boolean foundGateBlock = false;
		
		Block start = null;

		for (int yLoc = 0; yLoc < yOff * 2; ++yLoc) {
			for (int xLoc = 0; xLoc < xOff * 2; ++xLoc) {
				for (int zLoc = 0; zLoc < zOff * 2; ++zLoc) {
					Block currentBlock = world.getBlockAt(search.getBlockX() + xLoc, search.getBlockY() - yLoc,
							search.getBlockZ() + zLoc);
					if (isGateTopBlock(currentBlock)) {
						foundGateBlock = true;
						start = currentBlock;
					}
					// do not search in the next area if a gate block was found
					if (foundGateBlock) break;
				}
				// do not search in the next area if a gate block was found
				if (foundGateBlock) break;
			}
			// do not search in the next area if a gate block was found
			if (foundGateBlock) break;
		}

		if(start == null) return back;
		
		back.add(start);
		BlockFace direction = getNextGateTopBlockDirection(start);
		
		if(direction == BlockFace.EAST) {
			Block b = start;
			for(int i=0;i<30;i++) {
				b = getNextGateTopBlock(b, BlockFace.EAST);
				if(b == null) break;
				back.add(b);
			}
			b = start;
			for(int i=0;i<30;i++) {
				b = getNextGateTopBlock(b, BlockFace.WEST);
				if(b == null) break;
				back.add(b);
			}
		}
		else if(direction == BlockFace.NORTH) {
			Block b = start;
			for(int i=0;i<30;i++) {
				b = getNextGateTopBlock(b, BlockFace.NORTH);
				if(b == null) break;
				back.add(b);
			}
			b = start;
			for(int i=0;i<30;i++) {
				b = getNextGateTopBlock(b, BlockFace.SOUTH);
				if(b == null) break;
				back.add(b);
			}
		}
		
		return back;
	}
	
	// ---------------------------------------------------------------------------------------------
	
	protected Block getNextGateTopBlock(final Block src, BlockFace dir) {
		for(int y=-1;y<=1;y++) {
			Block test = src.getRelative(dir).getRelative(0, y, 0);
			if(isGateTopBlock(test))
				return test;
		}
		return null;
	}
	
	// ---------------------------------------------------------------------------------------------
	
	protected BlockFace getNextGateTopBlockDirection(final Block src) {
		if(isGateTopBlock(src.getRelative(BlockFace.EAST)) || isGateTopBlock(src.getRelative(BlockFace.WEST)))
			return BlockFace.EAST;
		if(isGateTopBlock(src.getRelative(BlockFace.NORTH)) || isGateTopBlock(src.getRelative(BlockFace.SOUTH)))
			return BlockFace.NORTH;
		return BlockFace.SELF;
	}

	// ---------------------------------------------------------------------------------------------

	protected boolean isGateTopBlock(final Block block)
	{
		return isGateBlock(block) && isInArchway(block) &&
		(isGateBlock(block.getRelative(BlockFace.DOWN)) || block.getRelative(BlockFace.DOWN).getType() == Material.AIR);
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
		return !isGateBlock(block.getRelative(BlockFace.UP));
	}

	// ---------------------------------------------------------------------------------------------
}
