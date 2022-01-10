package virtualindex.virtualindexcore.cyclicaljobscontrollers;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.ccylivespot.CcyDownloadCenter;
import virtualindex.virtualindexcore.computer.PriceReturn;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class CcyLivePricing 
{
	private DbConnection query = new DbConnection();
	
	final Logger logger = Logger.getLogger(Principal.class.getName());
	
	//With specific price sources
	public void updateDb(ArrayList <String> ccyList, ArrayList <String> sourceList) throws Exception
	{
				
		CcyDownloadCenter ccyDownloadCenter = new CcyDownloadCenter();
		
		int iterator = 0;
		Checks check = new Checks();
			
		for (String ccy : ccyList)
		{
			int stratId = SqlRequests2.getStratId(ccy);
			if (check.todayValueNotPresent(stratId))
			{
				BigDecimal spot = ccyDownloadCenter.getMid(ccy, sourceList.get(iterator)); // use MID price as close value
				
				// --> WITH calculation of the daily return
				
				BigDecimal dailyReturnValue = dailyReturn(stratId, spot);
				
				query.execute("write", "INSERT INTO CLOSE (id_strategy, close_value, close_date, daily_return, close_time, close_source) "
					+ "VALUES ('"+stratId+"','"+spot+"',CURDATE(),'"+dailyReturnValue+"',CURTIME(),'"+sourceList.get(iterator)+"')");
				 
				// --> WITHOUT calculation of the daily return
			/*
				query.execute("write", "INSERT INTO CLOSE (id_strategy, close_value, close_date, close_time, close_source) "
					+ "VALUES ('"+getCcyId(ccy)+"','"+spot+"',CURDATE(),CURTIME(),'"+sourceList.get(iterator)+"')");
				*/
				System.out.println("Spot Inserted for: "+ccy);
			
			}
			else
			{
				System.out.println("Today's close is already updated - please check database");
			}
		}
	}
	//With default price sources
	public void updateDb(ArrayList <String> ccyList) throws Exception
	{
				
		CcyDownloadCenter ccyDownloadCenter = new CcyDownloadCenter();
		
		
		Checks check = new Checks();
			
		for (String ccy : ccyList)
		{
			int stratId = SqlRequests2.getStratId(ccy);
			if (check.todayValueNotPresent(SqlRequests2.getStratId(ccy)))
			{
				BigDecimal spot = ccyDownloadCenter.getMid(ccy, SqlRequests2.getCcyDefaultSoure( ccy));
				
				// --> WITH calculation of the daily return
				
				BigDecimal dailyReturnValue = dailyReturn(stratId, spot);
				
				query.execute("write", "INSERT INTO CLOSE (id_strategy, close_value, close_date, daily_return, close_time, close_source) "
					+ "VALUES ('"+stratId+"','"+spot+"',CURDATE(),'"+dailyReturnValue+"',CURTIME(),'"+SqlRequests2.getCcyDefaultSoure( ccy)+"')");
				 
				// --> WITHOUT calculation of the daily return
			/*
				query.execute("write", "INSERT INTO CLOSE (id_strategy, close_value, close_date, close_time, close_source) "
					+ "VALUES ('"+getCcyId(ccy)+"','"+spot+"',CURDATE(),CURTIME(),'"+getCcyDefaultSoure(ccy)+"')");
				*/
				System.out.println("Spot Inserted for: "+ccy);
			
			}
			else
			{
				System.out.println("Today's close is already updated - please check database");
			}
		}
	}
	
	
	public  ArrayList<BigDecimal> getLivePrice(ArrayList <String> ccyList, ArrayList <String> sourceList, String side) throws Exception
	{
		logger.info("Compute live price");
		CcyDownloadCenter ccyDownloadCenter = new CcyDownloadCenter();
		ArrayList<BigDecimal>pricesList = new ArrayList<BigDecimal>();
		int iterator = 0;
		for (String ccy : ccyList)
		{
			BigDecimal livePrice = new BigDecimal("0");
			switch (side)
			{
			case "ask":
			{
				 livePrice = ccyDownloadCenter.getAsk(ccy, sourceList.get(iterator));
				break;
			}
			case "bid":
			{
				 livePrice = ccyDownloadCenter.getBid(ccy, sourceList.get(iterator));
				break;
			}
			case "mid":
			{
				 livePrice = ccyDownloadCenter.getMid(ccy, sourceList.get(iterator));
				break;
			}

			}
			
			logger.info(ccy+" : "+livePrice.toString()+" - source : "+sourceList.get(iterator));
			pricesList.add(livePrice);
			
		}
		
		return pricesList;
	}
	
	public ArrayList<BigDecimal> getLivePrice(ArrayList <String> ccyList, String side) throws Exception
	{
		CcyDownloadCenter ccyDownloadCenter = new CcyDownloadCenter();
		ArrayList<BigDecimal>pricesList = new ArrayList<BigDecimal>();
		for (String ccy : ccyList)
		{
			BigDecimal livePrice = new BigDecimal("0");
			switch (side)
			{
			case "ask":
			{
				livePrice = ccyDownloadCenter.getAsk(ccy, SqlRequests2.getCcyDefaultSoure( ccy));
				logger.info(ccy+" : "+livePrice.toString()+" - source : "+SqlRequests2.getCcyDefaultSoure( ccy));
				pricesList.add(livePrice);	
				break;
			}
			case "bid":
			{
				livePrice = ccyDownloadCenter.getBid(ccy, SqlRequests2.getCcyDefaultSoure( ccy));
				logger.info(ccy+" : "+livePrice.toString()+" - source : "+SqlRequests2.getCcyDefaultSoure( ccy));
				pricesList.add(livePrice);
				break;
			}
			case "mid":
			{
				livePrice = ccyDownloadCenter.getMid(ccy, SqlRequests2.getCcyDefaultSoure( ccy));
				logger.info(ccy+" : "+livePrice.toString()+" - source : "+SqlRequests2.getCcyDefaultSoure( ccy));
				pricesList.add(livePrice);
				break;
			}

			}		
		}
		
		return pricesList;
	}
	

	
	

	

	private BigDecimal dailyReturn(int idStrategy, BigDecimal spot) throws Exception
	{
		
		Calendar day = new GregorianCalendar();
		day.add(Calendar.DAY_OF_MONTH, -1);
		String yesterday = DateProcessing.calendarToSql(day);
		
		
		BigDecimal previousVal = SqlRequests2.getStratClose(idStrategy, yesterday);

		return PriceReturn.compute(previousVal, spot);
	}
}
