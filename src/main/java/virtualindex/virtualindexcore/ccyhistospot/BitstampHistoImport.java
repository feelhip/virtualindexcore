package virtualindex.virtualindexcore.ccyhistospot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;

import virtualindex.virtualindexcore.filehandler.json.JsonReader;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class BitstampHistoImport 
{
	public JsonReader jsonReader;
	
	public BitstampHistoImport() throws Exception{
	
	  this.jsonReader = new JsonReader("https://www.quandl.com/api/v1/datasets/BCHARTS/BITSTAMPUSD.json");
	}
	
	public void storeJson() throws JSONException, SQLException
	{
		JSONArray jsonArray = this.jsonReader.getJsonArray("data");
		SqlRequests req = new SqlRequests();
		//for(int x =0; x<jsonArray.length(); x++)
		for(int  x=jsonArray.length()-1; x >=0; x--)
		{
			JSONArray jsonArray2 = (JSONArray) jsonArray.get(x);
			String date = (String) jsonArray2.get(0);
			BigDecimal close = new BigDecimal((Double) jsonArray2.get(4)).setScale(12, RoundingMode.HALF_EVEN);
			
			req.storeStrategyClosePrice(18,  close,  date,"00:00:00", "btcx", "bitstamp");

		}
	}
	
	
}
