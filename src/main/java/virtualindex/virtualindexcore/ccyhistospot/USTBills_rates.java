package virtualindex.virtualindexcore.ccyhistospot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class USTBills_rates 
{
	final Logger logger = Logger.getLogger(Principal.class.getName());
	
	public void setLastRate(int maturityDays) throws Exception
	{
		SqlRequests req = new SqlRequests();
		//Start the observation in d-10
		Calendar tm10 = new GregorianCalendar();
		tm10.add(Calendar.DATE, -10);
		String stringDate = DateProcessing.calendarToSql(tm10);
		
		String seriesId = req.getFredCode(maturityDays + "d_TBill");
		
		WebPageBuffer buffer = new WebPageBuffer();
		String urlString = "http://api.stlouisfed.org/fred/series/observations?series_id="+seriesId+"&observation_start="+stringDate+"&api_key=eb853371a20d6ffab05b70f1056c4b8c&file_type=json";
		
		JSONObject jsonObj = new JSONObject (buffer.getPage(urlString));
		
		JSONArray JsonTable = jsonObj.getJSONArray("observations");
		
		for (int x = 1; x<= 10; x++)
		{
			int lastRow = JsonTable.length()-x;
			JSONObject lastDay = (JSONObject) JsonTable.get(lastRow);
			String stringClose =  (String) lastDay.get("value");
			try
			{
				BigDecimal close = new BigDecimal(stringClose);
				close = close.setScale(12, RoundingMode.HALF_EVEN);
				
				SqlRequests commonReq = new SqlRequests();
				
				commonReq.setMarketData(maturityDays+"d_TBill", close, DateProcessing.todaySqlDate());
				break;
			}
			catch (Exception e)
			{
				logger.warning("No valid data for today: downloading the previous one");
				logger.warning(e.toString());
				
			}
		}
		logger.info("Market data downloaded for: T-Bill #"+seriesId);
	}
	
	
	public void setAllRate(int maturityDays) throws Exception
	{

		SqlRequests req = new SqlRequests();
	
		String data_name = maturityDays+"d_TBill";
				
		String seriesId = req.getFredCode(data_name);
		
		WebPageBuffer buffer = new WebPageBuffer();
		String urlString = "http://api.stlouisfed.org/fred/series/observations?series_id="+seriesId+"&observation_start=2012-01-01&api_key=eb853371a20d6ffab05b70f1056c4b8c&file_type=json";
		
		JSONObject jsonObj = new JSONObject (buffer.getPage(urlString));
		
		JSONArray JsonTable = jsonObj.getJSONArray("observations");
		
	
			int dataSize = JsonTable.length();
			for (int x = 0; x< dataSize; x++)
			{
				JSONObject lastDay = (JSONObject) JsonTable.get(x);
				String stringClose =  (String) lastDay.get("value");
				String date = (String) lastDay.get("date");
				
				try
				{
				BigDecimal close = new BigDecimal(stringClose);
				close = close.setScale(12, RoundingMode.HALF_EVEN);	
				req.setMarketData(maturityDays+"d_TBill", close, date);
				}
				catch (Exception e)
				{
					logger.warning("Market data not set - check logs");
					logger.warning(e.toString());
				}
					
					
				
			}
			
		}
	
}
