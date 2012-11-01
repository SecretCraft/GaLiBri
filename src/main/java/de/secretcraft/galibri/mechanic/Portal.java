package de.secretcraft.galibri.mechanic;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.secretcraft.galibri.GalibriPlugin;

public class Portal extends AbstractMechanic
{
	//---------------------------------------------------------------------------------------------

	//---------------------------------------------------------------------------------------------
	public Portal(GalibriPlugin plugin) 
	{
		super(plugin);
		permissions.put(Perm.INITIALIZE, "galibri.portal.create");
		permissions.put(Perm.DO_ACTION, "galibri.portal.use");
	}

	//---------------------------------------------------------------------------------------------
	@Override
	public boolean initialize(final SignChangeEvent event)
	{
		if (!super.initialize(event))
			return false;

		final Player player = event.getPlayer();
		
		if (validateSign(event.getLine(1)))
		{
			event.setLine(0, "[Portal]");
			player.sendMessage(event.getLine(0) + " Sign created.");	
		}
		else
		{
			event.setLine(0, "[Error]");
			player.sendMessage(event.getLine(0) + " Invalid coordinates.");
		}
		
		return true;
	}
	
	// ---------------------------------------------------------------------------------------------
	@Override
	public boolean doAction(final Sign sign, final Player player)
	{
		if (validateSign(sign.getLine(1)))
		{
			try
			{
				String[] coordinates = sign.getLine(1).split(":");

				// now teleport player
				// 0 .. x
				// 1 .. z
				// 2 .. y = height!
				Location newPlayerLocation = new Location(player.getWorld(),
									  Double.parseDouble(coordinates[0]),
									  Double.parseDouble(coordinates[2]),
									  Double.parseDouble(coordinates[1])
								);
				newPlayerLocation = getTeleportLocation(newPlayerLocation);
				player.teleport(newPlayerLocation);
			}
			catch(Exception e)
			{
				player.sendMessage(sign.getLine(0) + " " + e.getMessage());
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	private boolean validateSign(String coordinateLine)
	{
		boolean returnValue = true;
		
		String[] coordinates = coordinateLine.split(":");
		if (coordinates.length == 3)
		{
			returnValue &= validateCoordinates(coordinates[0],
											   coordinates[1],
											   coordinates[2]);
		}
		else
		{
			returnValue = false;
		}
		
		return returnValue;
	}	

	// ---------------------------------------------------------------------------------------------
	private boolean validateCoordinates(String x, String y, String z)
	{
		boolean returnValue = true;
		returnValue &= isDouble(x);
		returnValue &= isDouble(y);
		returnValue &= isDouble(z);
		return returnValue;
	}
		
	//---------------------------------------------------------------------------------------------
	public boolean isDouble(String input)  
	{
		try  
		{  
			Double.parseDouble(input);  
			return true;
		}  
		catch (Exception ex)  
		{  
			return false;  
		}  
	}
	
	//---------------------------------------------------------------------------------------------

}
