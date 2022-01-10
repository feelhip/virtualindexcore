package virtualindex.virtualindexcore.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;

import virtualindex.virtualindexcore.cyclicaljobscontrollers.AnalysorsSelector;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.CurrenciesCloseDownload;
import virtualindex.virtualindexcore.kpireport.KpiGenerator;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class FastTask {
	
	static public void currencyClose(String ccy) {
		CurrenciesCloseDownload dailyImport = new CurrenciesCloseDownload();
		dailyImport.populateCloses(ccy);
	}
	
	static public void dailyAnalysors (String ccy) throws SQLException, ParseException
	{
		SqlRequests request = new SqlRequests();
		ArrayList<Integer> ccyIdList = new ArrayList<>();
		ccyIdList.add(request.getStratId(ccy));
		AnalysorsSelector.calculate(ccyIdList);
	}

	
	static public void kpiReport(String date) throws SQLException, IOException, JSONException, ParseException
	{
		KpiGenerator correlKpi = new KpiGenerator.Builder(date).correlations().build();
		correlKpi.sendByEmail();
		correlKpi.archive();
	}
	
	
}
