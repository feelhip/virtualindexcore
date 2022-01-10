package virtualindex.virtualindexcore.createindex;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONException;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.filehandler.json.JsonReader;

public class ImportJsonStrat 
{

	
	final Logger logger = Logger.getLogger(Principal.class.getName());
	
	private JsonReader jsonReader;
	
	
	public ImportJsonStrat(String fileName)
	{
		this.jsonReader = new JsonReader("inputs/", fileName);
	}
	
	
	
	
	public String getName() throws JSONException 
	{
		return jsonReader.getString("name");
	}

	public String getFullName() throws JSONException 
	{
		return jsonReader.getString("fullname");
	}

	public String getDirection() throws JSONException 
	{
		return jsonReader.getString("direction");
	}
	
	public String getDescription() throws JSONException 
	{
		return jsonReader.getString("description");
	}

	public ArrayList<String> getCompoNames() throws JSONException 
	{
		return jsonReader.getStringArray("composition");
	}

	public ArrayList<BigDecimal> getCompoWeights() throws JSONException {
		return jsonReader.getBigDecimalArray("weights");
	}

	public BigDecimal getBase() throws JSONException 
	{
		return jsonReader.getBigDecimal("base");
	}
	
	public String getCurrency() throws JSONException 
	{
		return jsonReader.getString("ccy");
	}
	
	public String getStartDate() throws JSONException 
	{
		return jsonReader.getString("start_date");
	}
	

	
}
