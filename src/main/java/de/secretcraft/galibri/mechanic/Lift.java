package de.secretcraft.galibri.mechanic;

import org.bukkit.ChatColor;
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
		catch(Exception e){
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
	
	private Location getTeleportLocation(final Location location) throws Exception
	{
		Block destBlock = getFloor(location.getBlock().getRelative(BlockFace.DOWN));
		if(destBlock == null){
			// TODO: STH localize
			throw new Exception("no floor found");
		}
		if(!isFreeArea(destBlock)){
			// TODO: STH localize
			throw new Exception("there is not enough space for you");
		}
		location.setY(destBlock.getRelative(BlockFace.UP).getY());
		return location;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private boolean isFreeArea(Block block)
	{
		// NOTE: STH search for free area to port the player
		for(int i=0;i<2;++i){
			block = block.getRelative(BlockFace.UP);
			Material mat = block.getType();
			if(mat != Material.AIR && mat != Material.SIGN && mat != Material.SIGN_POST && mat != Material.WALL_SIGN) return false;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------
	
	private Block getFloor(Block block)
	{
		// NOTE: STH search for the floor 3 blocks downwards 
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
	
	private boolean isMassive(final Block block)
	{
		// NOTE: STH if something is missing please add
		Material mat = block.getType();
		return mat == Material.STONE || mat == Material.WOOD || mat == Material.SAND || mat == Material.SANDSTONE ||
				mat == Material.BEDROCK || mat == Material.BRICK || mat == Material.COBBLESTONE || mat == Material.WOOL ||
				mat == Material.DIRT || mat == Material.EMERALD_BLOCK || mat == Material.EMERALD_ORE || mat == Material.GLASS ||
				mat == Material.GLOWSTONE || mat == Material.GOLD_BLOCK || mat == Material.GOLD_ORE || mat == Material.GRASS ||
				mat == Material.GRAVEL || mat == Material.ICE || mat == Material.IRON_BLOCK || mat == Material.IRON_ORE ||
				mat == Material.LAPIS_BLOCK || mat == Material.LAPIS_ORE || mat == Material.NETHER_BRICK || mat == Material.NETHERRACK ||
				mat == Material.OBSIDIAN || mat == Material.REDSTONE_ORE || mat == Material.SNOW_BLOCK || mat == Material.SOUL_SAND ||
				mat == Material.DOUBLE_STEP;
	}
	
	//---------------------------------------------------------------------------------------------
}
