package virtualindex.virtualindexcore.cyclicaljobscontrollers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;


public class Checks 
{
	private final Logger logger = Logger.getLogger(Principal.class.getName());

	
	
	public boolean todayValueNotPresent(int stratId) throws SQLException
	{
	Boolean testFailed = true;
	try 
	{
		SqlRequests2.getStratClose(stratId, DateProcessing.todaySqlDate());		
		testFailed = false;		
	}
	catch (Exception e) 
	{
		logger.info("Today's price is not in database");
		logger.info(e.toString());
	}
	

	if (testFailed)
	{
		
		return true;}
		
	else
	{
		logger.warning("Today's value is already present");
		return false;
	}
		
	}

	public Boolean checkClose(BigDecimal close, String date, String time) 
	{
		return (checkCloseDate(date));

		
	}

	private boolean checkCloseDate(String dateStr) 
	{
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = sdf.format(today);
		
		if (dateStr.equalsIgnoreCase(todayStr) )
		{
			logger.info("Parsed date is today");
			return true;}
		else
		{
			logger.warning("Today's date is "+todayStr+" while the parsed date is "+dateStr);
		return false;
		}
	}
	
	public boolean checkSeriesLength(ArrayList<BigDecimal>series1,ArrayList<BigDecimal>series2 )
	{
		return (series1.size()==series2.size());

	}
	
}
