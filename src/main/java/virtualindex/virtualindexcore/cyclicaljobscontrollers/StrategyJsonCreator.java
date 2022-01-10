package virtualindex.virtualindexcore.cyclicaljobscontrollers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONException;

import virtualindex.virtualindexcore.ApplicationPath;
import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.filehandler.json.JsonWriter;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class StrategyJsonCreator 
{
	final static Logger logger = Logger.getLogger(Principal.class.getName());
	private SqlRequests req = new SqlRequests();
	
	
	public void createDescriptionFile(int id, String date) throws Exception {
		
		String name = req.getStratName(id);
		logger.info("Start DESCRIPTION json for :"+name);

		ArrayList<String> compoNames = new ArrayList<String>();
		ArrayList<ArrayList<Object>> composition = SqlRequests2.getComposition(id);
		compoNames = SqlRequests2.getComposition_names(composition);

		JsonWriter writer = new JsonWriter();
		writer.addString("name", name);
		writer.addString("date", date);
		writer.addString("base", req.getStratBase(id).toString());
		writer.addString("direction", req.getStratDirection(id));
		writer.addString("ccy", req.getStratCcy(id));
		writer.addString("start_date", req.getStratStartDate(id));
		writer.addString("description", req.getStratDes(id));
		writer.addString("full_name", req.getStratFullName(id));
		writer.addString("last_date", req.getStratLastDate(id));

		writer.addString("last_close", SqlRequests2.getStratClose(id, date).toString());
		writer.addString("last_return", req.getReturn(id, date).toString());

		writer.addHashTable("analysors", req.getStratAnalysors(id, date));
		if(id != req.getStratId("BTC"))
		{
		writer.addHashTable("composition", req.getStratCompo(id));

		for (String udlName : compoNames) {
			writer.addHashTable("composition_analysors_" + udlName, req.getStratAnalysors(req.getStratId(udlName), date));
		}
		writer.addHashTable("composition_spots", req.getCompoSpots(id, date));	
		writer.addHashTable("composition_daily_returns", req.getCompoDailyReturns(id, date));	
		}
		
		writer.writeFile(ApplicationPath.getApplicationPath() + "/outputs/json/", name);
		logger.info("Finished DESCRIPTION json for :"+name);
	}

	public void createPriceTimeSeriesFile(String stratName,String path) throws SQLException, ParseException, IOException, JSONException
	{
		logger.info("Start HISTORY json for :"+stratName);
		
		int stratId = req.getStratId(stratName);
		ArrayList <BigDecimal> closeList = req.getStratClose(stratId);
		ArrayList<String> closeDateList = req.getStratCloseDate(stratId);
		ArrayList<Long> closeEpochList = new ArrayList<Long>();
		for (String date : closeDateList)
		{
			closeEpochList.add(DateProcessing.stringToEpoch(date));
		}
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(closeList, closeEpochList);
		histoWriter.writeArrayOnlyFile(ApplicationPath.getApplicationPath() + path, "histo_"+stratName);
		
		logger.info("Finished HISTORY json for :"+stratName);
	}
	
	public void createDailyReturnTimeSeriesFile(int stratId,String path) throws Exception
	{
		logger.info("Start Daily Return json for :"+stratId);
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(SqlRequests2.getDailYReturnTimeSeries (stratId));
		histoWriter.writeArrayOnlyFile(ApplicationPath.getApplicationPath() + path, "histo_daily_return_"+SqlRequests2.getStratName(stratId));
		
		logger.info("Finished Daily Return json for :"+stratId);
	}
	
	public void createAnalysorTimeSeriesFile(int stratId,String path, String analysorName) throws Exception
	{
		logger.info("Start Daily Return json for :"+stratId);
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(SqlRequests2.getAnalysorTimeSeries(stratId, analysorName));
		histoWriter.writeArrayOnlyFile(ApplicationPath.getApplicationPath() + path, "histo_analysor_"+analysorName+"_"+SqlRequests2.getStratName(stratId));
		
		logger.info("Finished Daily Return json for :"+stratId);
	}
	
	public void createCorrelationTimeSeriesFile(int id1, int id2, String path,int period) throws Exception
	{
		logger.info("Start Correlation json for :"+id1+" and "+id2);
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(SqlRequests2.getCorrelationTimeSeries(SqlRequests2.getStratName(id1), SqlRequests2.getStratName(id2), period));
		histoWriter.writeArrayOnlyFile(ApplicationPath.getApplicationPath() + path, "histo_correlation_"+period+"d_"+id1+"_"+id2);
		
		logger.info("Finished Correlation json for :"+id1+" and "+id2);
	}
	
	public void createStratsList() throws SQLException, JSONException, IOException
	{
		logger.info("Start INDICES LIST json");
		SqlRequests request = new SqlRequests();
		ArrayList<String> indicesNameList = request.getAllIndicesNames();
		
		JsonWriter listWriter = new JsonWriter();
		listWriter.addSimpleSeries(indicesNameList);
		listWriter.writeArrayOnlyFile(ApplicationPath.getApplicationPath() + "/outputs/json/", "strategies_list");
		
		logger.info("Finished INDICES LIST json");
	}
	
	public void createPriceTimeSeriesFile_web(String stratName,String path) throws SQLException, ParseException, IOException, JSONException
	{
		logger.info("Start HISTORY json for :"+stratName);
		
		int stratId = req.getStratId(stratName);
		ArrayList <BigDecimal> closeList = req.getStratClose(stratId);
		ArrayList<String> closeDateList = req.getStratCloseDate(stratId);
		ArrayList<Long> closeEpochList = new ArrayList<Long>();
		for (String date : closeDateList)
		{
			closeEpochList.add(DateProcessing.stringToEpoch(date));
		}
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(closeList, closeEpochList);
		histoWriter.writeArrayOnlyFile( path, "histo_"+stratName);
		
		logger.info("Finished HISTORY json for :"+stratName);
	}
	
	public void createDailyReturnTimeSeriesFile_web(int stratId,String path) throws Exception
	{
		logger.info("Start Daily Return json for :"+stratId);
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(SqlRequests2.getDailYReturnTimeSeries (stratId));
		histoWriter.writeArrayOnlyFile(path, "histo_daily_return_"+SqlRequests2.getStratName(stratId));
		
		logger.info("Finished Daily Return json for :"+stratId);
	}
	
	public void createAnalysorTimeSeriesFile_web(int stratId,String path, String analysorName) throws Exception
	{
		logger.info("Start analysor json for :"+stratId);
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(SqlRequests2.getAnalysorTimeSeries(stratId, analysorName));
		histoWriter.writeArrayOnlyFile(path, "histo_analysor_"+analysorName+"_"+SqlRequests2.getStratName(stratId));
		
		logger.info("Finished Daily Return json for :"+stratId);
	}
	
	public void createCorrelationTimeSeriesFile_web(int id1, int id2, String path,int period) throws Exception
	{
		logger.info("Start Correlation json for :"+id1+" and "+id2);
		JsonWriter histoWriter = new JsonWriter();
		histoWriter.addTimeSeries(SqlRequests2.getCorrelationTimeSeries(SqlRequests2.getStratName(id1), SqlRequests2.getStratName(id2), period));
		histoWriter.writeArrayOnlyFile( path, "histo_correlation_"+period+"d_"+SqlRequests2.getStratName(id1)+"_"+SqlRequests2.getStratName(id2));
		
		logger.info("Finished Correlation json for :"+id1+" and "+id2);
	}
	
}
