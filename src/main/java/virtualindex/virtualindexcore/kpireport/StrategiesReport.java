package virtualindex.virtualindexcore.kpireport;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class StrategiesReport 
{
	private SqlRequests req; 
	private ArrayList <Integer> indicesIdList = new ArrayList<Integer>();
	private ArrayList <Integer> todayIndicesIdList = new ArrayList<>();
	
	private ArrayList<String>checkList = new ArrayList<String>();
	private String date;
	private ArrayList<BigDecimal> indicesCloseList = new ArrayList<BigDecimal>();
	private ArrayList<String> missingIndices = new ArrayList<String>();
	private ArrayList<String> zeroCcy = new ArrayList<String>();
	private ArrayList<String> perfCcy = new ArrayList<String>();
	private ArrayList<BigDecimal> perfCcyVal = new ArrayList<BigDecimal>();
	
	private String txt_totalStatus = "" ;
	private String txt_total = "" ;
	private String txt_calculated = "" ;
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	
	public StrategiesReport(String date, SqlRequests req) throws SQLException // exchange = cryptsy
	{
		this.req = req;
		this.date = date;
		this.indicesIdList= this.req.getAllIndicesId();
		this.todayIndicesIdList= getTodayCalculatedIndicesId();
		this.txt_total = String.valueOf(indicesIdList.size());
		this.txt_calculated = String.valueOf(todayIndicesIdList.size());
		this.downloadCheck();
		this.missingCcyCheck();
		this.zeroCheck();
		this.performanceValidationCheck();
	}
	
	public String generate() throws SQLException
	{
		String report = "";
		String separator = "----------------------------------------------------------";
		String newLine = System.getProperty("line.separator");
		String smallSeparator = "---";
		
		
		
		
		currenciesCheck(checkList);
		//Part 1.
		report= separator+newLine+smallSeparator+" INDICES "+smallSeparator+"> "+txt_totalStatus+newLine
				+"Total: "+txt_total+newLine+"Computed: "+txt_calculated+newLine;
		
		//Part 2 - if missing currencies
		
		if(missingIndices.size()!= 0)
		{
			report = report +"- Missing:"+newLine;
			for(String ccy: missingIndices)
			{
				report = report + "   "+ccy+newLine;
			}
		}
		
		//Part 3 - if price validation needed
		
		if(zeroCcy.size()!= 0)
		{
			report = report +"- Price Validation required for null price:"+newLine;
			for(String ccy: zeroCcy)
			{
				report = report + "   "+ccy+" : 0"+newLine;
			}
		}
		
		//Part 4 - if perf validation needed
		if(perfCcy.size()!= 0)
		{
			int itr = 0;
			report = report +"- Price Validation required for performance:"+newLine;
			for(String ccy: zeroCcy)
			{
				report = report + "   "+ccy+" : "+perfCcyVal.get(itr)+newLine;
				itr ++;
			}
		}
		
		
		return report;
	}
	
	
	
	private void downloadCheck() throws SQLException
	{
		if (indicesIdList.size() == todayIndicesIdList.size())
		{
			checkList.add("OK");
		}
		else
		{
			checkList.add("KO");
		}
	}
	
	private void missingCcyCheck() throws SQLException
	{
	

		//Currencies missing status
		for (int itr = 0; itr < indicesIdList.size(); itr ++)
		{
			try 
			{
				this.indicesCloseList.add(SqlRequests2.getStratClose(indicesIdList.get(itr), this.date));
			}
			catch (Exception e)
			{
				missingIndices.add(req.getStratName(indicesIdList.get(itr)));
				logger.warning(e.toString());
			}
		}
	}
	
	private void zeroCheck() throws SQLException
	{
		int itr = 0;
		for (BigDecimal close :this.indicesCloseList)
		{
			if(close == new BigDecimal("0"))
			{
				this.zeroCcy.add(req.getStratName(this.todayIndicesIdList.get(itr)));
			}
			itr++;
		}
	}
	
	private void performanceValidationCheck() throws SQLException 
	{
		for (int indexId :this.todayIndicesIdList)
		{
			BigDecimal stratPerformance = req.getReturn(indexId, date);
			
			if(stratPerformance.compareTo(new BigDecimal("0.1")) ==1 || stratPerformance.compareTo(new BigDecimal("-0.1")) ==-1)
			{
				perfCcy.add(req.getStratName(indexId));
				perfCcyVal.add(stratPerformance);
			}
		}
	}
	

	
	private ArrayList <Integer>  getTodayCalculatedIndicesId() throws SQLException
	{
		ArrayList <Integer> indicesList = new ArrayList<Integer>();
		DbConnection custReq = new DbConnection();
		ResultSet rs = custReq.execute("read", "SELECT close.id_strategy FROM close " +
				"INNER JOIN strategies ON close.id_strategy = strategies.id_strategy " +
				"WHERE close.close_date = '"+date+"' " +
				"AND strategies.instr_type = 'index' ORDER BY close.id_strategy ASC");	
		do
		{
			indicesList.add(rs.getInt(1));
			
		}
		while (rs.next());
		
		return indicesList;	
	}
	

	
	private void currenciesCheck(ArrayList<String>checkList)
	{
		for (String status : checkList)
		{
			if (status.equals("KO"))
			{
				txt_totalStatus = "KO";
				return;
			}
			
		}
		
		txt_totalStatus = "OK";
	}
}
