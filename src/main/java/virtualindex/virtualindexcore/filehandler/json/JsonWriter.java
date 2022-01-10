package virtualindex.virtualindexcore.filehandler.json;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class JsonWriter 
{
	
	private JSONObject jsonObject = new JSONObject();
	private JSONArray jsonArray = new JSONArray();
	
	 public void addString(String key, String value) throws JSONException
	{
		this.jsonObject.put(key, value);
	}
	
	 public void addHashTable( String tableName, Hashtable< String, String> hashTable) throws JSONException
	{
		this.jsonObject.put(tableName, hashTable);
	}
	
	 
	 public void addTimeSeries(ArrayList<BigDecimal>valuesList, ArrayList<Long> datesList) throws JSONException
	 {
		 
		 for (int x = 0; x<datesList.size(); x++)
		 {
			 JSONArray jsonSubArray = new JSONArray();			 
			 jsonSubArray.put(datesList.get(x));
			 jsonSubArray.put(valuesList.get(x));
			 this.jsonArray.put(jsonSubArray);
		 }
		 
		 
	 }
	 
	 
	 public void addTimeSeries(Map<Long, BigDecimal> dataMap) throws JSONException
	 {
		 
		 
		 for (Entry<Long, BigDecimal> entry : dataMap.entrySet()) {
			 JSONArray jsonSubArray = new JSONArray();
			 jsonSubArray.put(entry.getKey());
			 jsonSubArray.put(entry.getValue());
			 this.jsonArray.put(jsonSubArray);
			} 
		 
	 }
	 
	 public void addSimpleSeries(ArrayList<String>valuesList) throws JSONException
	 {
		 
		 for (int x = 0; x<valuesList.size(); x++)
		 {
			 this.jsonArray.put(valuesList.get(x));
		 }
		 
		 
	 }
	 
	 
	 
	 public void writeFile( String path, String fileName) throws IOException
	{
		FileWriter file = new FileWriter(path+fileName+".json");
		file.write(this.jsonObject.toString());
		file.flush();
		file.close();
	}
	
	 public void writeArrayOnlyFile( String path, String fileName) throws IOException
		{
			FileWriter file = new FileWriter(path+fileName+".json");
			file.write(this.jsonArray.toString());
			file.flush();
			file.close();
		}

}
