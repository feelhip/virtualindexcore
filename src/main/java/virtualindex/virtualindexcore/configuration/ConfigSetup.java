package virtualindex.virtualindexcore.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Logger;

import org.json.JSONObject;

import virtualindex.virtualindexcore.Principal;

public class ConfigSetup 
{
	
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	public static String  getValue(String key)
	{
		try{
		String path = Principal.path;	
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(path+"/config/config.json"));
		String dataString = reader.readLine().toString();
		
		JSONObject dataJson = new JSONObject(dataString);
		
		String value = (String) dataJson.get(key);
		
		return value;}
		catch (Exception e)
		{
			logger.severe(e.toString());
			return null;
		}
		
	}
	
	
}
