package de.secretcraft.galibri.mechanic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * 
 * 
 * @author sascha thiel
 */
public class Gate extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------
	
	public final static String CONFIG_BLOCKS_PATH = "gate-blocks";
	public final static String CONFIG_SEARCH_OFFSET = "gate-search-range";
	
	//---------------------------------------------------------------------------------------------
	
	public Gate(final GalibriPlugin plugin)
	{
		super(plugin);
		permissions.put(Perm.INITIALIZE, "gate.create");
		permissions.put(Perm.DO_ACTION, "gate.use");
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(final SignChangeEvent event)
	{
		if(!super.initialize(event)) return false;
		final Player player = event.getPlayer();
		event.setLine(1, "[Gate]");
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(final Sign sign, final Player player)
	{
		if(!super.doAction(sign, player)) return false;

		List<Block> topBlocks = searchForTopBlocks(sign.getBlock());
		if(topBlocks.size() == 0){
			player.sendMessage("no gate blocks found");
			return false;
		}
		
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	
	protected List<Block> searchForTopBlocks(Block block)
	{
		List<Block> back = new ArrayList<Block>();
		final YamlConfiguration conf = (YamlConfiguration)plugin.getConfig();
		final Integer xOff = conf.getInt(CONFIG_SEARCH_OFFSET+".x");
		final Integer yOff = conf.getInt(CONFIG_SEARCH_OFFSET+".y");
		final Integer zOff = conf.getInt(CONFIG_SEARCH_OFFSET+".z");
		
		final Location search = new Location(block.getWorld(), block.getX()-xOff, block.getY()+yOff, block.getZ()-zOff);
		final World world = block.getWorld();
		
		boolean foundGateBlock = false;
		
		for(int yLoc = 0; yLoc < yOff * 2; ++yLoc){
			for(int xLoc = 0; xLoc < xOff * 2; ++xLoc){
				for(int zLoc = 0; zLoc < zOff * 2; ++zLoc){
					Block currentBlock = world.getBlockAt(search.getBlockX() + xLoc, search.getBlockY() - yLoc, search.getBlockZ() + zLoc);
					if(isGateBlock(currentBlock)) {
						foundGateBlock = true;
						back.add(currentBlock);
					}
				}
				if(foundGateBlock) break;
			}
			if(foundGateBlock) break;
		}		
		return back;
	}
	
	//---------------------------------------------------------------------------------------------
	
	protected void toggleGate(List<Block> topBlocks)
	{
		Material mat = topBlocks.get(0).getType();
		
	}
	
	//---------------------------------------------------------------------------------------------
	
	protected boolean isGateBlock(Block block)
	{
		List<Integer> blockList = plugin.getConfig().getIntegerList(CONFIG_BLOCKS_PATH);
		return blockList.contains(block.getType().getId());
	}
	
	//---------------------------------------------------------------------------------------------
}
