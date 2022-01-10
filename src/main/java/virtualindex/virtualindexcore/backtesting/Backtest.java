package virtualindex.virtualindexcore.backtesting;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.StrategyPricer;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class Backtest 
{
	private String strategyName;
	private int id;
	private String direction;
	private BigDecimal base;	
	private String sqlStrikeDate;
	private String sqlEndDate;
	private Calendar calendarStrikeDate =new GregorianCalendar();
	private Calendar calendarEndDate =new GregorianCalendar();
	
	private ArrayList <Integer>compoIdsList;
	
	
	private DbConnection sqlQuery = new DbConnection();
	private SqlRequests req = new SqlRequests();
	private DateProcessing dateProcessing = new DateProcessing();
	
	
	
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	
	public Backtest(String strategyName) throws SQLException, ParseException
	{
		
		this.strategyName = strategyName;
		this.id = req.getStratId(strategyName);	
		this.direction = req.getIndexDirection(this.id);
		this.base = req.getStratBase(this.id);
		this.sqlStrikeDate = req.getIndexStartDate(this.id);
				
		ArrayList<ArrayList<Object>> composition = SqlRequests2.getComposition(this.id);
		
		this.compoIdsList = SqlRequests2.getComposition_ids(composition);
		
		
		
		this.sqlEndDate = getEndDate(this.compoIdsList);
		
		this.calendarStrikeDate = this.dateProcessing.sqlToCalendar(this.sqlStrikeDate, "yyyy-MM-dd");
		this.calendarEndDate = this.dateProcessing.sqlToCalendar(this.sqlEndDate, "yyyy-MM-dd");
		
	}

	private String getEndDate(ArrayList<Integer> underlyingsIdList) throws SQLException, ParseException 
	{
		String maxDate = "";
		
		Set<String> underlyingsMaxCloseDateListHash = new HashSet<String>();
		for (int id: underlyingsIdList)
		{
			maxDate = this.req.getStratLastDate(id);
			underlyingsMaxCloseDateListHash.add(this.req.getStratLastDate(id));
		}
		
		
		if(underlyingsMaxCloseDateListHash.size()==1)
		{
			String yesterday = DateProcessing.offsetSqlDate(-1, DateProcessing.todaySqlDate());
			String today = DateProcessing.todaySqlDate();
			
			if(maxDate.equals(yesterday) || maxDate.equals(today))
			{
				return maxDate;
			}
			else
			{
				logger.severe("The last date for for all of the underlyings is not today or yesterday: check if the database is updated");
				return null;
			}
			
		}
		else
		{
			logger.severe("Some underlyings are not updated. Cannot calculate backtest");
			return null;
		}
	}

	public void calculate() throws Exception
	{
		logger.info("Start pricing");
		setFirstSpot();
		completeSerie();
		logger.info("Pricing completed");
		
	}

	private void completeSerie() throws Exception 
	{
		Calendar calendarLoopedDate = new GregorianCalendar();
		calendarLoopedDate = this.calendarStrikeDate;
		calendarLoopedDate.add(Calendar.DAY_OF_MONTH, 1);
	
		StrategyPricer pricing = new StrategyPricer(this.strategyName);
		
		while( !calendarLoopedDate.after(this.calendarEndDate))
		{
			String sqlLoopedDate = this.dateProcessing.calendarToSql(calendarLoopedDate);	   
			pricing.storeClosePrice(sqlLoopedDate);			
		    calendarLoopedDate.add(Calendar.DATE, 1);  
		}
	}


	
	private void setFirstSpot() throws SQLException
	{
		sqlQuery.execute("write","INSERT INTO close (id_strategy, close_value, close_date, close_time,close_source, direction)"
				+ "VALUES('"+this.id+"','"+this.base+"','"+this.sqlStrikeDate+"','00:00:00','pricer','"+this.direction+"')");
	}
	
	
	
}
