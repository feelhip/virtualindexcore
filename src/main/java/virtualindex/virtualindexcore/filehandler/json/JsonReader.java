package virtualindex.virtualindexcore.filehandler.json;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import virtualindex.virtualindexcore.ApplicationPath;
import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class JsonReader 
{
	final Logger logger = Logger.getLogger(Principal.class.getName());
	private BufferedReader reader=null;
	private JSONObject jsonObject =new JSONObject();
	
	public JsonReader(String relativePath,String fileName)
	{
		this.jsonObject = stringToJson(fileToString(relativePath,fileName));
	}
	
	public JsonReader(String webAddress) throws Exception
	{
		WebPageBuffer buffer = new WebPageBuffer();
		this.jsonObject = stringToJson (buffer.getPage(webAddress));
	}
	
	public String getString(String key) throws JSONException
	{
		logger.info("Try extracting String: "+key);
		String data = jsonObject.getString(key);
		logger.info("Data extracted successfully");
		return data;
	}
	
	public BigDecimal getBigDecimal(String key) throws JSONException
	{
		logger.info("Try extracting BigDecimal: "+key);
		BigDecimal data = new BigDecimal(jsonObject.getString(key)).setScale(12, RoundingMode.HALF_EVEN);
		logger.info("Data extracted successfully");
		return data;
	}
	
	public ArrayList<String> getStringArray (String key) throws JSONException
	{
		ArrayList<String> stringArray = new ArrayList<String>();
		logger.info("Try extracting String Array: "+key);
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		for(int x =0; x<jsonArray.length(); x++)
		{
			stringArray.add(jsonArray.getString(x));
		}
		return stringArray;
	}
	
	
	public JSONArray getJsonArray (String key) throws JSONException
	{
		
		logger.info("Try extracting String Array: "+key);
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return jsonArray;
	}
	
	
	public ArrayList<BigDecimal> getBigDecimalArray (String key) throws JSONException
	{
		ArrayList<BigDecimal> bigDecimaArray = new ArrayList<BigDecimal>();
		logger.info("Try extracting BigDecimal Array: "+key);
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		for(int x =0; x<jsonArray.length(); x++)
		{
			
			bigDecimaArray.add(new BigDecimal(jsonArray.getDouble(x)).setScale(12, RoundingMode.HALF_EVEN));
		}
		return bigDecimaArray;
	}
	
	
	
	
	private JSONObject stringToJson (String jsonString)
	{
		logger.info("Start parsing JSON string");
		JSONObject dataJson = new JSONObject();
		try 
		{
			dataJson = new JSONObject(jsonString);
		} 
		catch (JSONException e) 
		{
			logger.severe("JSON not parsed. See log file.");
			logger.severe(e.toString());
		}
		logger.info("JSON string parsed suddessfully");
		
		return dataJson;
	}
	public Hashtable<String, String> getHashTable(String key) throws JSONException {
		Hashtable<String, String> hashTable = new Hashtable<>();

		JSONObject obj = jsonObject.getJSONObject(key);

		for (int x = 0; x < obj.length(); x++) {
			hashTable.put((String) obj.names().get(x), obj.getString((String) obj.names().get(x)));
		}

		return hashTable;
	}
	
	private String fileToString(String relativePath, String fileName)
	{
		String fullPath = ApplicationPath.getApplicationPath()+"/"+relativePath+fileName+".json";
		String jsonString = "";
		try 
		{
			logger.info("Try bufferring JSON");
			this.reader = new BufferedReader(new FileReader(fullPath));
			logger.info("JSON bufferred successfully");
		} 
		catch (FileNotFoundException e) 
		{
			logger.warning("Buffering JSON failed; see logs");
			logger.warning(e.toString());
		}
		try 
		{
			logger.info("Try copying buffer in a string");
			jsonString = this.reader.readLine().toString();
			logger.info("Buffer copy successful");
		} 
		catch (IOException e) 
		{
			logger.severe("Buffer copy failed; see logs");
			logger.severe(e.toString());
		}
		
		return jsonString;
	}
	
}
