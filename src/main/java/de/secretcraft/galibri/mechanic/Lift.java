package de.secretcraft.galibri.mechanic;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * 
 * 
 * @author sascha thiel
 */
public class Lift extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------
	
	public static enum Direction
	{
		UP, DOWN, NONE;
	}
	
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
			default: // NOTE: STH do nothing
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
		
		player.sendMessage("if this where implemented you would be ported");
		Block currentBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		// TODO: check if the player can be ported without damage
	}
	
	//---------------------------------------------------------------------------------------------
	
	private BlockFace getDirection(final String line)
	{
		if(line.toLowerCase().contains("up")) return BlockFace.UP;
		else if(line.toLowerCase().contains("down")) return BlockFace.DOWN;
		return null;
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
}
