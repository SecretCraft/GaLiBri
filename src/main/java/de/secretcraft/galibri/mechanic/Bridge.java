package de.secretcraft.galibri.mechanic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

/**
 * This class handles all Bridge signs that will be created
 * 
 * @author sascha thiel
 */
public class Bridge extends AbstractMechanic
{
	// ---------------------------------------------------------------------------------------------

	protected enum SetFacingCode {
		WALL_SIGN, DIRECTION, EVERYTHING_OK;
	}

	public final static String CONFIG_BLOCKS_PATH = "bridge.blocks";
	public final static String CONFIG_BRIDGE_MAX_SIZE_PATH = "bridge.max-size";

	// ---------------------------------------------------------------------------------------------

	public Bridge(GalibriPlugin plugin)
	{
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.bridge.create");
		permissions.put(Perm.DO_ACTION, "galibri.bridge.use");
	}

	// ---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(SignChangeEvent event)
	{
		if (!super.initialize(event))
			return false;
		final Player player = event.getPlayer();
		Sign sign = (Sign) event.getBlock().getState();
		switch (setFacingDirection(sign)) {
		case DIRECTION:
			// TODO: STH localize
			player.sendMessage("Could you please set a proper direction?");
			event.setLine(1, "[direction?]");
			return false;
		case WALL_SIGN:
			// TODO: STH localize
			player.sendMessage("Bridges can not be on a wall");
			event.setLine(1, "[not possible]");
			return false;
		default:
		}
		event.setLine(1, "[Bridge]");
		event.setLine(3, "0");
		player.sendMessage(event.getLine(1) + " sign created");
		return true;
	}

	// ---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(Sign sign, Player player)
	{
		if (!super.doAction(sign, player))
			return false;

		int maxLength = plugin.getConfig().getInt(CONFIG_BRIDGE_MAX_SIZE_PATH + ".length");
		int maxWidth = plugin.getConfig().getInt(CONFIG_BRIDGE_MAX_SIZE_PATH + ".width");
		Sign partnerSign = searchForPartnerSign(sign);
		if (partnerSign == null) {
			// TODO: STH localize
			player.sendMessage("no partner sign found (max " + String.valueOf(maxLength) + " Blocks)");
			return false;
		}
		List<Block> startBlocks = getStartBlocks(sign, maxWidth);
		if (startBlocks == null) {
			player.sendMessage("no bridge blocks found");
			return false;
		}
		int blocksAvailable = Integer.valueOf(sign.getLine(3));
		if(blocksAvailable != Integer.valueOf(partnerSign.getLine(3))) {
			setSignLine(sign, "0");
			setSignLine(partnerSign, "0");
			blocksAvailable = 0;
		}
		int length = (int) Math.abs(sign.getLocation().distance(partnerSign.getLocation())) - 1;
		int neededBlocks = length * startBlocks.size();
		BlockFace face = ((org.bukkit.material.Sign) sign.getData()).getFacing().getOppositeFace();
		List<Block> bridgeBlocks = null;
		if (blocksAvailable == 0) {
			bridgeBlocks = getBridgeBlocks(startBlocks, startBlocks.get(0).getType(), face, length);
			if(neededBlocks != bridgeBlocks.size()){
				// TODO: STH localize
				player.sendMessage("Please build a proper bridge");
				return false;
			}
			setBlockMaterial(bridgeBlocks, Material.AIR);
			setSignLine(sign, Integer.toString(bridgeBlocks.size()));
			setSignLine(partnerSign, Integer.toString(bridgeBlocks.size()));
		} else {
			bridgeBlocks = getBridgeBlocks(startBlocks, Material.AIR, face, length);
			if(neededBlocks != bridgeBlocks.size()){
				player.sendMessage("can not build the bridge");
				return false;
			}
			setBlockMaterial(bridgeBlocks, startBlocks.get(0).getType());
			setSignLine(sign, "0");
			setSignLine(partnerSign, "0");
		}
		return true;
	}
	
	// ---------------------------------------------------------------------------------------------
	
	protected void setSignLine(Sign sign, String lineText)
	{
		sign.setLine(3, lineText);
		sign.update();
	}

	// ---------------------------------------------------------------------------------------------

	private SetFacingCode setFacingDirection(final BlockState signState)
	{
		org.bukkit.material.Sign signData = (org.bukkit.material.Sign) signState.getData();
		if (signData.isWallSign())
			return SetFacingCode.WALL_SIGN;
		switch (signData.getFacing()) {
		case EAST_NORTH_EAST:
		case EAST_SOUTH_EAST:
			signData.setFacingDirection(BlockFace.EAST);
			signState.update();
		case EAST:
			break;
		case NORTH_NORTH_EAST:
		case NORTH_NORTH_WEST:
			signData.setFacingDirection(BlockFace.NORTH);
			signState.update();
		case NORTH:
			break;
		case SOUTH_SOUTH_EAST:
		case SOUTH_SOUTH_WEST:
			signData.setFacingDirection(BlockFace.SOUTH);
			signState.update();
		case SOUTH:
			break;
		case WEST_NORTH_WEST:
		case WEST_SOUTH_WEST:
			signData.setFacingDirection(BlockFace.WEST);
			signState.update();
		case WEST:
			break;
		default:
			return SetFacingCode.DIRECTION;
		}
		return SetFacingCode.EVERYTHING_OK;
	}

	// ---------------------------------------------------------------------------------------------

	private Sign searchForPartnerSign(final Sign sign)
	{
		Sign partnerSign = null;
		org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
		Block searchBlock = sign.getBlock();
		int maxLength = plugin.getConfig().getInt(CONFIG_BRIDGE_MAX_SIZE_PATH + ".length");
		for (int i = 0; i < maxLength; ++i) {
			searchBlock = searchBlock.getRelative(signData.getFacing().getOppositeFace());
			Material mat = searchBlock.getType();
			if (mat == Material.SIGN || mat == Material.SIGN_POST) {
				if (((Sign) searchBlock.getState()).getLine(1).equalsIgnoreCase("[Bridge]")) {
					partnerSign = (Sign) searchBlock.getState();
					break;
				}
			}
		}
		return partnerSign;
	}

	// ---------------------------------------------------------------------------------------------

	protected List<Block> getStartBlocks(final Sign sign, final int maxWidth)
	{
		final List<Block> blocks = new ArrayList<Block>();

		Block blockUnderSign = sign.getBlock().getRelative(BlockFace.DOWN);
		Block blockA = blockUnderSign;
		Block blockB = blockUnderSign;
		if (isBridgeBlock(blockUnderSign))
			blocks.add(blockUnderSign);
		else
			return null;

		List<BlockFace> faces = getCrossedFaces((org.bukkit.material.Sign) sign.getData());
		if (faces == null)
			return null;

		for (int i = 0; i < maxWidth / 2; ++i) {
			blockA = blockA.getRelative(faces.get(0));
			blockB = blockB.getRelative(faces.get(1));

			if (isBridgeBlock(blockA))
				blocks.add(blockA);
			if (isBridgeBlock(blockB))
				blocks.add(blockB);
		}

		return blocks;
	}

	// ---------------------------------------------------------------------------------------------

	protected List<BlockFace> getCrossedFaces(org.bukkit.material.Sign signData)
	{
		List<BlockFace> faces = new ArrayList<BlockFace>();
		switch (signData.getFacing().getOppositeFace()) {
		case NORTH:
		case SOUTH:
			faces.add(BlockFace.WEST);
			faces.add(BlockFace.EAST);
			break;
		case EAST:
		case WEST:
			faces.add(BlockFace.NORTH);
			faces.add(BlockFace.SOUTH);
			break;
		default:
			return null;
		}
		return faces;
	}

	// ---------------------------------------------------------------------------------------------

	protected boolean isBridgeBlock(Block block)
	{
		List<Integer> blockList = plugin.getConfig().getIntegerList(CONFIG_BLOCKS_PATH);
		return blockList.contains(block.getType().getId());
	}
	
	// ---------------------------------------------------------------------------------------------
	
	protected List<Block> getBridgeBlocks(final List<Block> startBlocks, final Material mat, final BlockFace face, final int length)
	{
		List<Block> blocks = new ArrayList<Block>();
		for (Block currentBlock : startBlocks) {
			for (int i = 0; i < length; ++i) {
				currentBlock = currentBlock.getRelative(face);
				if(currentBlock.getType() == mat) blocks.add(currentBlock);
			}
		}
		return blocks;
	}

	// ---------------------------------------------------------------------------------------------

	protected void setBlockMaterial(final List<Block> blocks, final Material mat)
	{
		for (Block currentBlock : blocks) {
			currentBlock.setType(mat);
		}
	}

	// ---------------------------------------------------------------------------------------------
}
