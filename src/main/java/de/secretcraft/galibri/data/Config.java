package de.secretcraft.galibri.data;

/**
 * 
 * 
 * @author sascha thiel
 */
public class Config
{
	//---------------------------------------------------------------------------------------------
	
	private static Config instance;
	
	//---------------------------------------------------------------------------------------------
	
	private Config() { }
	
	//---------------------------------------------------------------------------------------------
	
	public static Config getInstance()
	{
		if(instance == null) instance = new Config();
		return instance;
	}
	
	//---------------------------------------------------------------------------------------------
}
