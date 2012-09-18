package de.secretcraft.galibri.mechanic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * This class handles all Lift sign that will be created
 * 
 * @author sascha thiel
 */
public class Lift extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------

	
	
	//---------------------------------------------------------------------------------------------
	
	public Lift(GalibriPlugin plugin)
	{
		super(plugin);
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public void initialize(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		BlockFace direct = getDirection(event.getLine(1));
		switch(direct) {
			case UP: event.setLine(1, "[Lift Up]");
			break;
			case DOWN: event.setLine(1, "[Lift Down]");
			break;
			default: event.setLine(1, "[Lift]");
		}
		player.sendMessage(event.getLine(1) + " sign created");
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public void doAction(Sign sign, Player player)
	{
		BlockFace face = getDirection(sign.getLine(1));
		if(face == null) {
			// TODO: STH localize
			player.sendMessage("You can't departure from this sign");
			return;
		}
		Sign destSign = searchForSign(face, sign);
		if(destSign == null){
			// TODO: STH localize
			player.sendMessage("no destination sign found");
			return;
		}
		
		try{
			Location location = getTeleportLocation(player.getLocation(), sign.getY() - destSign.getY());
			player.teleport(location);
		}
		catch(Exception e){
			player.sendMessage(e.getMessage());
		}
	}
	
	//---------------------------------------------------------------------------------------------

	private BlockFace getDirection(final String line)
	{
		if(line.toLowerCase().contains("up")) return BlockFace.UP;
		else if(line.toLowerCase().contains("down")) return BlockFace.DOWN;
		return BlockFace.SELF;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private boolean isLiftSign(Sign sign)
	{
		return sign.getLine(1).toLowerCase().contains("lift");
	}
	
	//---------------------------------------------------------------------------------------------
	
	private Sign searchForSign(BlockFace face, Sign startSign)
	{
		Block destBlock = startSign.getBlock();
		Sign destSign = null;
		boolean end = false;
		while(!end){
			destBlock = destBlock.getRelative(face);
			BlockState state = destBlock.getState();
			if(state instanceof Sign){
				if(isLiftSign((Sign) state)){
					destSign = (Sign) state;
					end = true;
					continue;
				}
			} else if(destBlock.getY() <= 0 || destBlock.getY() >= destBlock.getWorld().getMaxHeight()){
				end = true;
				continue;
			}
		}
		return destSign;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private Location getTeleportLocation(Location currentLocation, int yOffset) throws Exception
	{
		Block destBlock = getFloor(currentLocation.getWorld().getBlockAt(currentLocation.add(0, yOffset, 0)).getRelative(BlockFace.DOWN));
		if(destBlock == null){
			throw new Exception("no floor was found");
		}
		if(!isFreeArea(destBlock)){
			throw new Exception("there is not enough space for you");
		}
		return new Location(currentLocation.getWorld(), currentLocation.getX(), destBlock.getRelative(BlockFace.UP).getY(), currentLocation.getZ());
	}
	
	//---------------------------------------------------------------------------------------------
	
	private boolean isFreeArea(Block block)
	{
		for(int i=0;i<2;++i){
			block = block.getRelative(BlockFace.UP);
			if(block.getType() != Material.AIR) return false;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private Block getFloor(Block block)
	{
		Block back = null;
		for(int i = 0; i<3; ++i){
			if(isMassive(block)){
				back = block;
				break;
			}
			block = block.getRelative(BlockFace.DOWN);
		}
		return back;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private boolean isMassive(Block block)
	{
		// TODO: STH fill up with massive materials
		Material mat = block.getType();
		return mat == Material.STONE || mat == Material.WOOD || mat == Material.SAND || mat == Material.SANDSTONE ||
				mat == Material.BEDROCK || mat == Material.BRICK;
	}
	
	//---------------------------------------------------------------------------------------------
}
