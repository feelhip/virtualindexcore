package virtualindex.virtualindexcore;

import java.math.BigDecimal;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.ccylivespot.CryptsyLivePrice;
import virtualindex.virtualindexcore.computer.PriceReturn;
import virtualindex.virtualindexcore.controller.DailyTask;
import virtualindex.virtualindexcore.controller.Test_online;
import virtualindex.virtualindexcore.cyclicaljobscontrollers.AnalysorsSelector;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class ClassTester {

	final static Logger logger = Logger.getLogger(Principal.class.getName());
	
	public static void main(String[] args) throws Exception 
	{
		/*--- LOGGER CONFIG - DO NOT REMOVE ---*/
		Handler fh = new FileHandler(ApplicationPath.getApplicationPath() + "/logs/log.log", 5000000, 1000, true);
		fh.setFormatter(new LogFormatter());
		logger.addHandler(fh);
		/*--- LOGGER CONFIG - DO NOT REMOVE ---*/
		
		AnalysorsSelector.calculateCorrelMatrix(SqlRequests2.getAllStratNames());
	}





}
		/*Test_online.launch();
		DailyTask.currenciesClose();
		DailyTask.kpiReport();*/
		/*
		ArrayList<ArrayList<Object>> composition = SqlRequests2.getComposition(265);
		ArrayList<Object> weights = composition.get(2);
		
		ArrayList<Integer> test = SqlRequests2.getComposition_ids(SqlRequests2.getComposition(265));
		
		System.out.println(composition.toString());*/
		
		/*
		KpiWriter2 correlKpi = new KpiWriter2.Builder(DateProcessing.todaySqlDate()).correlations().build();
		correlKpi.sendByEmail();*/
		

		
		
		
	/*
SqlRequests request= new SqlRequests();
		
		ArrayList<String> indicesNameList = request.getAllIndicesNames();
		indicesNameList.add("BTC");
		for (String indexName: indicesNameList)
		{	
	
		//ADD THE JSON WRITING BELOW
		StrategyJsonCreator strat = new StrategyJsonCreator();
		strat.create(request.getStratId(indexName), DateProcessing.todaySqlDate());
		}
		
		*/
		//DailyAnalysors_HISTO histoAnalysor = new DailyAnalysors_HISTO();
		//histoAnalysor.calculate(18);
		
		/*ArrayList<String> list = new ArrayList<>();
		list.add("BTC");
		
		MaintenanceTask maintenance = new MaintenanceTask();
		maintenance.checkSpotsIntegrity(list);*/
		
		/*
		BitstampHistoImport bitstampImport = new BitstampHistoImport();
		bitstampImport.storeJson();
		*/
		
		
		//--- INDEX CREATION
		//ImportIndex.create("test4");
		//DeleteStrategy.delete("all", 266);
		//BacktestIndex.backtest("TOPTEST_6");
		
		//--- BITSTAMP LIVE PRICE
		/*BitstampLive bitstampLive = new BitstampLive();
		System.out.println("Mid: "+bitstampLive.getMidPx());
		System.out.println("Bid: "+bitstampLive.getBidPx());
		System.out.println("Ask: "+bitstampLive.getAskPx());
		System.out.println("VWAP: "+bitstampLive.getVWAP());*/
		
		

