package virtualindex.virtualindexcore.ccyhistospot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class CryptsyHistoImport 
{

	private DbConnection sqlConnection = new DbConnection();
	private int ccyId;
	private String ccyName = "";

	final Logger logger = Logger.getLogger(Principal.class.getName());
	private DbConnection query = new DbConnection();
	private String ccyCode;
	
	public CryptsyHistoImport(String ccyName) throws SQLException
	{
		this.ccyName = ccyName;
		ResultSet result = sqlConnection.execute("read", "SELECT id_strategy FROM strategies WHERE strategy_name ='"+ccyName+"'");
		this.ccyId=result.getInt(1) ;
		
	}
	
	
	public void displayJSON() throws Exception
	{

		JSONArray dataJson = new JSONArray();
		dataJson = importJson();
		
		for (int x = 0; x<dataJson.length(); x++)
		{	
			JSONArray dayArray = (JSONArray) dataJson.get(x);
			Object test = dayArray.get(4);
			String close =  test.toString();		
			BigDecimal closeDecimal = new BigDecimal(close);
			//the precision in cryptsy in 8 digits
			closeDecimal = closeDecimal.setScale(8, RoundingMode.HALF_EVEN);			
			Long dateTime = (Long) dayArray.get(0);
			Date date = new Date(dateTime);
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			String sqlDate = formatDate.format(date);
			SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm:ss");
			String sqlTime = formatTime.format(date);
			System.out.println(this.ccyName 
					+ " (id #"+this.ccyId+") - close: "
					+closeDecimal+" - "
					+sqlDate+" - "
					+sqlTime);	
		}	
		logger.info("DISPLAY OF THE CLOSE VALUES FINISHED FOR "+this.ccyName);
	}
	
	public void copyToDb(String stringStartDate) throws Exception
	{	
		JSONArray dataJson = new JSONArray();
		dataJson = importJson();
			
		for (int x = 0; x<dataJson.length(); x++)
		{
			JSONArray dayArray = (JSONArray) dataJson.get(x);
			
			Double close = (Double) dayArray.get(4);
			BigDecimal closeDecimal = new BigDecimal(close);
			
			Long dateTime = (Long) dayArray.get(0);

			Date date = new Date(dateTime);
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			String sqlDate = formatDate.format(date);

			SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm:ss");
			String sqlTime = formatTime.format(date);

			Date dateStartDate = formatDate.parse(stringStartDate);
			
			if(dateStartDate.compareTo(date)<=0)
			{
				storeSpot(closeDecimal, sqlDate, sqlTime);
				logger.info("Spot added @ "+sqlDate+" for "+ccyName);
			}
			
			

		}
		logger.info("IMPORT IN THE DATABASE SUCCESSFULL FOR "+this.ccyName);
	}
	
	private void storeSpot(BigDecimal spot, String date, String time) throws SQLException
	{
		sqlConnection.execute("write", "INSERT INTO close (id_strategy, close_value, close_date, close_time, close_source, direction) "
				+ "VALUES('"+this.ccyId+"','"+spot+"','"+date+"','"+time+"','cryptsy','xbtc')");	
		logger.info("Spot stored");
	}
	
	private JSONArray importJson() throws Exception
	{
		ResultSet selection = query.execute("read","SELECT cryptsy_name, cryptsy_code FROM strategies WHERE strategy_name ='"
		+this.ccyName+"'");
		this.ccyCode = selection.getString(2);
		String urlString = "https://www.cryptsy.com/chart.php?marketid="
		+this.ccyCode+"&start=1069344860000&end=1914035186787&callback=chart";		
		WebPageBuffer bufferWebPage = new WebPageBuffer();
		return new JSONArray(bufferWebPage.getPage(urlString));
	}
}
