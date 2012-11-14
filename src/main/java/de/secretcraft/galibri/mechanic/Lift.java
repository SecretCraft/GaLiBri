package de.secretcraft.galibri.mechanic;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GaLiBriException;
import de.secretcraft.galibri.GalibriPlugin;

/**
 * This class handles all Lift signs that will be created
 * 
 * @author sascha thiel
 */
public class Lift extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------

	
	
	//---------------------------------------------------------------------------------------------
	
	public Lift(final GalibriPlugin plugin)
	{
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.lift.create");
		permissions.put(Perm.DO_ACTION, "galibri.lift.use");
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(final SignChangeEvent event)
	{
		if(!super.initialize(event)) return false;
		final Player player = event.getPlayer();
		final BlockFace direct = getDirection(event.getLine(1));
		switch(direct) {
			case UP: event.setLine(1, "[Lift Up]");
			break;
			case DOWN: event.setLine(1, "[Lift Down]");
			break;
			default: event.setLine(1, "[Lift]");
		}
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(final Sign sign, final Player player)
	{
		if(!super.doAction(sign, player)) return false;
		final BlockFace face = getDirection(sign.getLine(1));
		if(face == BlockFace.SELF) {
			// TODO: STH localize
			player.sendMessage(ChatColor.RED + "You can't depart from this sign");
			return false;
		}
		Sign destSign = searchForSign(face, sign);
		if(destSign == null){
			// TODO: STH localize
			player.sendMessage("no destination sign found");
			return false;
		}
		
		try{
			Location location = player.getLocation();
			location.setY(destSign.getY());
			location = getTeleportLocation(location);
			player.teleport(location);
		}
		catch(GaLiBriException e){
			player.sendMessage(e.getMessage());
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------

	private BlockFace getDirection(final String line)
	{
		if(line.toLowerCase().contains("up")) return BlockFace.UP;
		else if(line.toLowerCase().contains("down")) return BlockFace.DOWN;
		return BlockFace.SELF;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private boolean isLiftSign(final Sign sign)
	{
		return sign.getLine(1).toLowerCase().contains("lift");
	}
	
	//---------------------------------------------------------------------------------------------
	
	private Sign searchForSign(final BlockFace face, final Sign startSign)
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
