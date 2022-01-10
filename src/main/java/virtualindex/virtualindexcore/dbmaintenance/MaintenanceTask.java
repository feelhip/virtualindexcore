package virtualindex.virtualindexcore.dbmaintenance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class MaintenanceTask 

{
	private static final Logger logger =  Logger.getLogger(Principal.class.getName());
	private boolean fixingsChecker = false;
	
	/*public void computeReturns(ArrayList <String> currencies) throws SQLException
	{	
		DbConnection sqlQuery = new DbConnection();		
		for (String ccy : currencies)
		{
			logger.info("Daily returns computed for: "+ccy);
		
			ResultSet selection = sqlQuery.execute("read","SELECT close.id_strategy, close.close_value, close.close_date "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
			
			ResultSet size = sqlQuery.execute("read","SELECT COUNT(close.close_date) "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
			
			ResultSet minDate = sqlQuery.execute("read","SELECT MIN(close.close_date) "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");

		//Insert the first return (=0)
		sqlQuery.execute("write","UPDATE close SET daily_return = 0 "
				+ "WHERE close_date = '"+minDate.getString(1)+"' AND id_strategy = "+selection.getInt(1));
		
		//Loop to insert the other returns
		for (int x = 1; x<size.getInt(1);x++)
		{
			BigDecimal oldValue = selection.getBigDecimal(2);
			selection.next();
			BigDecimal newValue = selection.getBigDecimal(2);
			
			PriceReturn dailyReturn = new PriceReturn();
			BigDecimal dailyReturnValue = dailyReturn.compute(oldValue,newValue);

			sqlQuery.execute("write","UPDATE close SET daily_return = "
					+ dailyReturnValue
					+ " WHERE close_date = '"+selection.getString(3)+"' AND id_strategy = "+selection.getInt(1));
		}
		
		}
	}*/
	
	public void checkDatesIntegrity(ArrayList <String> currencies) throws SQLException, ParseException
	{
		DbConnection sqlQuery = new DbConnection();
		
		for (String ccy : currencies)
		{
		
		ResultSet selection = sqlQuery.execute("read","SELECT close.id_strategy, close.close_value, close.close_date "
				+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
				+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
		
		ResultSet size = sqlQuery.execute("read","SELECT COUNT(close.close_date) "
				+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
				+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
		
		for (int x = 1; x<size.getInt(1);x++)
		{
			DateProcessing sqlDate = new DateProcessing();
			Calendar oldDateC = new GregorianCalendar();
			Calendar newDateC = new GregorianCalendar();
			
			oldDateC=sqlDate.sqlToCalendar(selection.getString(3), "yyyy-MM-dd");
			oldDateC.add(Calendar.DAY_OF_MONTH, 1);
			Date oldDateIncremented = oldDateC.getTime();
			selection.next();
			newDateC = sqlDate.sqlToCalendar(selection.getString(3), "yyyy-MM-dd");
			Date newDate = newDateC.getTime();
			
			if (!oldDateIncremented.equals(newDate) )
			{
				System.out.println(ccy+ ": missing date before "+newDate);
			}
			else
			{}
								
		}
		}
		System.out.println("All dates have been checked");
	}
	
	public void countCloseMissing(ArrayList <String> currencies) throws SQLException, ParseException
	{
		DbConnection sqlQuery = new DbConnection();
		SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd");
		
		for (String ccy : currencies)
		{
		
		/*ResultSet selection = sqlQuery.execute("read","SELECT close.id_strategy, close.close_value, close.close_date "
				+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
				+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");*/
		
		ResultSet size = sqlQuery.execute("read","SELECT COUNT(close.close_date) "
				+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
				+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
		
		ResultSet minDate = sqlQuery.execute("read","SELECT MIN(close.close_date) "
				+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
				+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
		
		
			Date startDate = new Date();
			Date today = new Date();
			
			startDate = sDF.parse(minDate.getString(1));
		
			long diff = Math.abs(startDate.getTime() - today.getTime());
			long diffDays = diff / (24 * 60 * 60 * 1000);
			
			float realSz = size.getInt(1);
			float theoSz = diffDays;
			float missingSpots = theoSz - realSz;
			
			float threshold = 0.5f;
			float proportion =  realSz  / theoSz ;
			
			if ( missingSpots != 0 )
			{
				System.out.println(ccy+ ": less than "+threshold*100+"% of the days are populated : currency is too illiquid ("+proportion+")");
				System.out.println(missingSpots+" missing spots");
				System.out.println(" - ");
			}
			else if ( missingSpots == 0 )
			{
				logger.info(ccy+" : OK!");
			}
			else
			{logger.warning("issue: please recheck data for "+ccy);}
		}	
	}
	
	public void checkSpotsIntegrity(ArrayList <String> currencies) throws SQLException
	{
		DbConnection sqlQuery = new DbConnection();
		
		for (String ccy : currencies)
		{
			ResultSet selection = sqlQuery.execute("read","SELECT close.id_strategy, close.close_value, close.close_date,close.id_close "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
			
			ResultSet size = sqlQuery.execute("read","SELECT COUNT(close.close_date) "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
			
			for (int x = 1; x<size.getInt(1);x++)
			{
				BigDecimal spot = selection.getBigDecimal(2);
				String date = selection.getString(3);
				if (spot.compareTo(new BigDecimal("0")) ==0)
				{
					System.out.println("Spot for strategy "+ccy+" (id: "+selection.getInt(1)+") is null at date: "+date+" - id_close = "+selection.getInt(4));
				}	
				selection.next();
			}
		}
		System.out.println("All spots have been checked");
	}
	
	public void fixingsManagementLoop(ArrayList <String> currencies) throws SQLException, ParseException
	{
		int x = 0;
		while(!fixingsChecker)
		{	
			x++;
			logger.info("Loop n."+x);
			missingFixingManagement(currencies);
		}
	}
	
	
	private void missingFixingManagement(ArrayList <String> currencies) throws SQLException, ParseException
	{
		DbConnection sqlQuery = new DbConnection();
		SqlRequests req = new SqlRequests();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		for (String ccy : currencies)
		{
			logger.info("Currency verified: "+ccy);
			
			ResultSet selection = sqlQuery.execute("read","SELECT close.id_strategy, close.close_value, close.close_date "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
			
			ResultSet size = sqlQuery.execute("read","SELECT COUNT(close.close_date) "
					+ "FROM close LEFT JOIN strategies ON close.id_strategy= strategies.id_strategy "
					+ "WHERE strategies.strategy_name = '"+ccy+"' order by close.close_date ASC");
			
			
			int loopLength = size.getInt(1);
		
			for (int x = 1; x<loopLength;x++)
			{
				Calendar tmrwCal = new GregorianCalendar();
				Calendar nextDayCal = new GregorianCalendar();
				
				BigDecimal todaySpot = selection.getBigDecimal(2);
				todaySpot = todaySpot.setScale(12, RoundingMode.HALF_EVEN);
				Date today = sdf.parse(selection.getString(3));
				tmrwCal.setTime(today);
				tmrwCal.add(Calendar.DATE,1);
				Date tmrw = tmrwCal.getTime();
				
				selection.next();
				
				BigDecimal nextdaySpot = selection.getBigDecimal(2);
				nextdaySpot = nextdaySpot.setScale(12, RoundingMode.HALF_EVEN);
				Date nextDay = sdf.parse(selection.getString(3));
				nextDayCal.setTime(nextDay);
				
				if (!tmrwCal.equals(nextDayCal))
				{
					req.storeStrategyClosePrice(req.getStratId(ccy), todaySpot, sdf.format(tmrw), "00:00:00", "xbtc","cryptsy");	
					logger.info("Fixing added: "+ccy+" : "+todaySpot.toString()+" @ "+sdf.format(tmrw));
					return;
				}					
			}
		}
		this.fixingsChecker = true;
		logger.info("All dates are fixed");
		
	}
	
	public void fixingsManagementLoopMarketData(String dataName) throws SQLException, ParseException
	{
		int x = 0;
		while(!fixingsChecker)
		{	
			x++;
			logger.info("Loop n."+x);
			missingFixingManagementMarketData( dataName);
		}
	}
	
	
	public void missingFixingManagementMarketData(String dataName) throws SQLException, ParseException
	{
		DbConnection sqlQuery = new DbConnection();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		
			logger.info("Currency verified: "+dataName);
			
			ResultSet selection = sqlQuery.execute("read","SELECT data_name, data_value, value_date " +
					"FROM market_data " +
					"WHERE data_name = '"+dataName+"'  " +
					"ORDER BY market_data.value_date ASC");
			
			ResultSet size = sqlQuery.execute("read","SELECT COUNT(value_date) "
					+ "FROM market_data " +
					"WHERE data_name = '"+dataName+"'  ");
			
			
			int loopLength = size.getInt(1);
		
			for (int x = 1; x<loopLength;x++)
			{
				Calendar tmrwCal = new GregorianCalendar();
				Calendar nextDayCal = new GregorianCalendar();
				
				BigDecimal todaySpot = selection.getBigDecimal(2);
				todaySpot = todaySpot.setScale(12, RoundingMode.HALF_EVEN);
				Date today = sdf.parse(selection.getString(3));
				tmrwCal.setTime(today);
				tmrwCal.add(Calendar.DATE,1);
				Date tmrw = tmrwCal.getTime();
				
				selection.next();
				
				BigDecimal nextdaySpot = selection.getBigDecimal(2);
				nextdaySpot = nextdaySpot.setScale(12, RoundingMode.HALF_EVEN);
				Date nextDay = sdf.parse(selection.getString(3));
				nextDayCal.setTime(nextDay);
				
				if (!tmrwCal.equals(nextDayCal))
				{
					//req.insertSpot(req.getStratId(ccy), todaySpot, sdf.format(tmrw), "00:00:00", "xbtc","cryptsy");
					
					sqlQuery.execute("write", "INSERT INTO market_data (data_name, data_value, value_date) " +
							"VALUES ('"+dataName+"','"+todaySpot+"','"+sdf.format(tmrw)+"')");
					
					logger.info("Fixing added: "+dataName+" : "+todaySpot.toString()+" @ "+sdf.format(tmrw));
					return;
				}					
			}
		
		this.fixingsChecker = true;
		logger.info("All dates are fixed");
		
	}
	
	
	
}
