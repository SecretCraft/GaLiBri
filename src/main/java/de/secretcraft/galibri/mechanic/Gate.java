package de.secretcraft.galibri.mechanic;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * This class handles all Gate signs that will be created
 * 
 * @author sascha thiel
 * @author Max Heller
 */
public class Gate extends AbstractMechanic
{
	public final static String CONFIG_BLOCKS_PATH = "gate.blocks";
	public final static String CONFIG_ARCHWAY_BLOCK_PATH = "gate.archway-blocks";
	public final static String CONFIG_SEARCH_OFFSET = "gate.search-range";
	
	public static Vector[] offset = {new Vector(0, 0, 0)};
	
	public static void reloadConfig(final JavaPlugin plugin) {
		final YamlConfiguration conf = (YamlConfiguration) plugin.getConfig();
		int xOffset = conf.getInt(CONFIG_SEARCH_OFFSET + ".x");
		int yOffset = conf.getInt(CONFIG_SEARCH_OFFSET + ".y");
		int zOffset = conf.getInt(CONFIG_SEARCH_OFFSET + ".z");
		
		int i = 0; // fill the offset array
		offset = new Vector[(xOffset * 2 + 1) * (zOffset * 2 + 1) * (yOffset * 2 + 1)];
		for(int y = -yOffset; y <= yOffset; y++) {
			for(int x = -xOffset; x <= xOffset; x++) {
				for(int z = -zOffset; z <= xOffset; z++) {
					offset[i++] = new Vector(x, y, z);
				}
			}
		}
		
		// sort the offset array with selection sort for the smallest distance
		for(i = 0; i < offset.length; i++) {
			int smallestIndex = i;
			// Actually the squared distance
			int smallestDist = offset[i].getBlockX() * offset[i].getBlockX() +
							   offset[i].getBlockY() * offset[i].getBlockY() +
							   offset[i].getBlockZ() * offset[i].getBlockZ();
			
			for(int j = i + 1; j < offset.length; j++) {
				// Actually the squared distance
				int dist = offset[j].getBlockX() * offset[j].getBlockX() +
						   offset[j].getBlockY() * offset[j].getBlockY() +
						   offset[j].getBlockZ() * offset[j].getBlockZ();
				if(dist < smallestDist) {
					smallestIndex = j;
					smallestDist = dist;
				}
			}
			
			if(smallestIndex != i) { // then swap them
				Vector tmp = offset[i];
				offset[i] = offset[smallestIndex];
				offset[smallestIndex] = tmp;
			}
		}
	}

	public Gate(final GalibriPlugin plugin) {
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.gate.create");
		permissions.put(Perm.DO_ACTION, "galibri.gate.use");
	}

	@Override
	public boolean initialize(final SignChangeEvent event) {
		if (!super.initialize(event))
			return false;
		final Player player = event.getPlayer();
		event.setLine(1, "[Gate]");
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}

	@Override
	public boolean doAction(final Sign sign, final Player player) {
		if (!super.doAction(sign, player))
			return false;

		Block searchBlock = sign.getBlock()
				.getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace())
				.getRelative(BlockFace.UP);
		
		List<Block> topBlocks = searchForTopBlocks(searchBlock);
		if (topBlocks.size() == 0) {
			player.sendMessage("No gate blocks found"); //TODO: localize
			return false;
		}
		toggleGate(topBlocks);
		return true;
	}

	protected void toggleGate(List<Block> topBlocks) {
		for (Block block : topBlocks) {
			Material gateMat = block.getType();
			Material toggleMat = null;
			for(;;) {
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
					break;
				}
			}
		}
	}

	protected List<Block> searchForTopBlocks(Block block) {
		List<Block> back = new LinkedList<Block>();
		
		Block start = null;

		for(Vector v : offset) {
			Block currentBlock = block.getRelative(v.getBlockX(), v.getBlockY(), v.getBlockZ());
			if (isGateTopBlock(currentBlock)) {
				start = currentBlock;
				break;
			}
		}

		if(start == null)
			return back;
		
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
	
	protected Block getNextGateTopBlock(final Block src, BlockFace dir) {
		for(int y=-1;y<=1;y++) {
			Block test = src.getRelative(dir).getRelative(0, y, 0);
			if(isGateTopBlock(test))
				return test;
		}
		return null;
	}
	
	protected BlockFace getNextGateTopBlockDirection(final Block src) {
		for(int y = -1; y <= 1; y++) {
			Block block = src.getRelative(0, y, 0);
			if(isGateTopBlock(block.getRelative(BlockFace.EAST)) || isGateTopBlock(block.getRelative(BlockFace.WEST)))
				return BlockFace.EAST;
			if(isGateTopBlock(block.getRelative(BlockFace.NORTH)) || isGateTopBlock(block.getRelative(BlockFace.SOUTH)))
				return BlockFace.NORTH;
		}
		return BlockFace.SELF;
	}

	protected boolean isGateTopBlock(final Block block) {
		return isGateBlock(block) && isInArchway(block)
				&& (block.getType() == block.getRelative(BlockFace.DOWN).getType()
				     || block.getRelative(BlockFace.DOWN).getType() == Material.AIR);
	}

	protected boolean isGateBlock(final Block block) {
		List<Integer> bl = plugin.getConfig().getIntegerList(CONFIG_BLOCKS_PATH);
		return bl.contains(block.getType().getId());
	}

	protected boolean isInArchway(final Block block) {
		return !isGateBlock(block.getRelative(BlockFace.UP))
				&& isMassive(block.getRelative(BlockFace.UP));
	}
}
