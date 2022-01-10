package virtualindex.virtualindexcore.ccyhistospot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class CryptsyTodayClose 
{
	
	private String ccyCode;
	private String ccyName;
	private DbConnection query = new DbConnection();
	private BigDecimal closeDecimal;
	private String sqlDate ;
	private String sqlTime;
	final Logger logger = Logger.getLogger(Principal.class.getName());
	
	public CryptsyTodayClose(String ccy) throws Exception
	{
		this.ccyName = ccy;
		try {
			getData();
		} catch (SQLException | JSONException | InterruptedException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void getData() throws Exception
	{
		ResultSet selection = query.execute("read","SELECT cryptsy_name, cryptsy_code FROM strategies WHERE strategy_name ='"+this.ccyName+"'");
		this.ccyName = selection.getString(1);
		this.ccyCode = selection.getString(2);
		
		
		
		String urlString = "https://www.cryptsy.com/chart.php?marketid="+this.ccyCode+"&start=1069344860000&end=1914035186787&callback=chart";
		
		WebPageBuffer bufferWebPage = new WebPageBuffer();
		
		JSONArray dataJson = new JSONArray(bufferWebPage.getPage(urlString));
		
		int lastRow = dataJson.length()-1;
		
		JSONArray dayArray = (JSONArray) dataJson.get(lastRow);
		Double close = (Double) dayArray.get(4);
	
		this.closeDecimal = new BigDecimal(close);
		//the precision in cryptsy in 8 digits
		this.closeDecimal = this.closeDecimal.setScale(8, RoundingMode.HALF_EVEN);
		
		Long dateTime = (Long) dayArray.get(0);

		Date date = new Date(dateTime);
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		this.sqlDate = formatDate.format(date);

		SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm:ss");
		this.sqlTime = formatTime.format(date);
	}
	
	public BigDecimal getClose() throws SQLException, JSONException, InterruptedException, IOException
	{
		logger.info(this.ccyName +" Close: "+this.closeDecimal.toString()+" Timestamp: "+this.sqlDate+" - "+this.sqlTime);
		return this.closeDecimal;	
	}
	
	public String getDate()
	
	{
		return this.sqlDate;
		
	}
	
	public String getTime()
	
	{
		return this.sqlTime;
		
	}
}
