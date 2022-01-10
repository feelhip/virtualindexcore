package virtualindex.virtualindexcore.controller;

import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.backtesting.Backtest;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class BacktestIndex 
{
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	
	public static void backtest(String indexName) throws Exception
	{
		SqlRequests req = new SqlRequests();
		
		logger.info("Beginning of the price backtesting for strategy "+indexName);
		Backtest backtest = new Backtest(indexName);
		backtest.calculate();
		logger.info("End of the price backtesting for strategy "+ indexName);
		
		logger.info("Beginning of the analysors backtesting for strategy "+ indexName);
		DailyAnalysors_HISTO histoAnalysor = new DailyAnalysors_HISTO();
		histoAnalysor.calculate(req.getStratId(indexName));
		logger.info("End of the analysors backtesting for strategy "+ indexName);
		
	}
}
