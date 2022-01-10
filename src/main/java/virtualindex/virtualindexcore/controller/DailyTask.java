package virtualindex.virtualindexcore.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import virtualindex.virtualindexcore.ccyhistospot.USTBills_rates;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.AnalysorsSelector;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.CurrenciesCloseDownload;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.StrategyJsonCreator;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.StrategyPricer;
import virtualindex.virtualindexcore.kpireport.KpiGenerator;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class DailyTask {
	static public void marketData() throws Exception {
		USTBills_rates bills = new USTBills_rates();
		bills.setLastRate(365);
		bills.setLastRate(180);
	}

	static public void currenciesClose() throws Exception {
		CurrenciesCloseDownload dailyImport = new CurrenciesCloseDownload();
		dailyImport.populateCloses();
	}

	static public void strategiesClose() throws Exception {
		SqlRequests request = new SqlRequests();
		ArrayList<String> indicesNameList = request.getAllIndicesNames();
		for (String indexName : indicesNameList) {
			StrategyPricer pricing = new StrategyPricer(indexName);
			pricing.storeTodayClosePrice(DateProcessing.todaySqlDate());
		}
	}

	static public void dailyAnalysors() throws SQLException, ParseException
	{
		AnalysorsSelector.calculate(SqlRequests2.getAllStratsId());
	}

	static public void kpiReport() throws Exception
	{
		KpiGenerator kpi = new KpiGenerator.Builder(DateProcessing.todaySqlDate()).currencies().marketData().analysors().build();
		kpi.sendByEmail();
		kpi.archive();
	}

	static public void jsons() throws Exception
	{
		SqlRequests req = new SqlRequests();
		ArrayList<String> indicesNameList = req.getAllIndicesNames();

		// INDICES
		for (String indexName : indicesNameList)
		{
			// TIME SERIES
			StrategyJsonCreator timeSeries = new StrategyJsonCreator();
			timeSeries.createPriceTimeSeriesFile(indexName, "/outputs/json/");

			// DESCRIPTION
			StrategyJsonCreator description = new StrategyJsonCreator();
			description.createDescriptionFile(req.getStratId(indexName), DateProcessing.todaySqlDate());
		}

		// LISTING
		StrategyJsonCreator stratsisting = new StrategyJsonCreator();
		stratsisting.createStratsList();
	}

}
