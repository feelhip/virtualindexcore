package virtualindex.virtualindexcore;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.controller.BacktestIndex;
import virtualindex.virtualindexcore.controller.DailyAnalysors_HISTO;
import virtualindex.virtualindexcore.controller.DailyTask;
import virtualindex.virtualindexcore.controller.FastTask;
import virtualindex.virtualindexcore.controller.ImportIndex;
import virtualindex.virtualindexcore.controller.NightlyTask;
import virtualindex.virtualindexcore.controller.StrategyDeletion;
import virtualindex.virtualindexcore.controller.Test_offline;
import virtualindex.virtualindexcore.controller.Test_online;

public class Principal {

	final static Logger logger = Logger.getLogger(Principal.class.getName());
	
	public static final String VERSION = "1.0";

	public final static String path = ApplicationPath.getApplicationPath();

	//public final static SqlRequests sqlRequest = new SqlRequests();


	public static void main(String[] args) throws Exception {

		// *** LOGGER CONFIGURATION *** //

	

		Handler fh = new FileHandler(path + "/logs/log.log", 5000000, 1000, true);
		fh.setFormatter(new LogFormatter());
		logger.addHandler(fh);

		try {
			switch (args[0]) {

			case "daily_tasks": {
				DailyTask.currenciesClose();
				DailyTask.strategiesClose();
				DailyTask.marketData();
				DailyTask.dailyAnalysors();
				DailyTask.jsons();
				DailyTask.kpiReport();
				break;
			}

			case "nightly_tasks": {
				NightlyTask.nightlyCorrelMatrix();
				break;
			}

			case "": {
				displayHelp();
				break;
			}
			case "help": {
				displayHelp();
				break;
			}
			case "test": {
				Test_online.launch();
				break;
			}
			case "daily_market_data": {
				logger.info("START MARKET DATA DOWNLOAD");
				DailyTask.marketData();
				logger.info("END MARKET DATA DOWNLOAD");
				break;
			}
			case "daily_ccy_data": {
				if (args.length == 2) {
					logger.info("START DOWNLOAD OF " + args[1]);
					FastTask.currencyClose(args[1]);
					logger.info("END DOWNLOAD OF " + args[1]);
					break;
				} else {
					logger.info("Currency ID needed");
					break;
				}

			}
			case "daily_strategies": {
				logger.info("START STRATEGIES CALCULATION");
				DailyTask.strategiesClose();
				logger.info("END STRATEGIES CALCULATION");
				break;
			}

			case "daily_analysors": {

				if (args.length == 2) {
					logger.info("START ANALYSORS CALCULATION FOR " + args[1]);
					FastTask.dailyAnalysors(args[1]);
					logger.info("END ANALYSORS CALCULATION FOR " + args[1]);
					break;
				} else {
					logger.info("Currency ID needed");
					break;
				}
			}

			case "daily_jsons": {
				DailyTask.jsons();
				break;
			}

			case "histo_analysors": {
				DailyAnalysors_HISTO analysorHisto = new DailyAnalysors_HISTO();
				int id = Integer.parseInt(args[1]);
				analysorHisto.calculate(id);
				break;
			}

			case "daily_kpi": {

				if (args.length == 2) {
					FastTask.kpiReport(args[1]);

					break;
				} else {
					DailyTask.kpiReport();

					break;
				}
			}

			case "test_offline": {
				Test_offline test = new Test_offline();
				test.launch();
				break;
			}

			case "delete_all": {
				StrategyDeletion.delete("all", Integer.parseInt(args[1]));
				break;
			}

			case "create": {
				ImportIndex index = new ImportIndex();
				index.create(args[1]);
				break;
			}

			case "backtest": {
				BacktestIndex.backtest(args[1]);
				break;
			}

			}

		} catch (Exception e) {
			logger.severe("ERROR OCCURED: there could be no argument");
			logger.severe(e.toString());
		}

	}

	private static void displayHelp() {
		System.out.println("Please add a parameter to the *.jar file:");
		System.out.println("          -> test : test if the programm is running");
		System.out
				.println("          -> daily_ccy_data : download all currencies close prices - you can pass a ccy name as argument to only download that ccy");
		System.out.println("          -> daily_market_data : download market data prices");
		System.out
				.println("          -> daily_analysors : calculate the analysors - you can pass a ccy name as argument to only compute that ccy analysors");
		System.out
				.println("          -> daily_kpi : generates the KPI; if no date, KPI of today is generated. If there is a date in argument (daily_kpi YYYY-MM-DD), then KPI of the selected date is generated");
		System.out
				.println("          -> histo_analysors : calculate the analysors for the full series. if arg = 'all', recalculate all; if arg = strategy id, calculates only for selected id");
		System.out.println("          -> delete_all : arg = index id: delete all data");
		System.out.println("          -> create : argument = file_name");
		System.out.println("          -> backtest : argument = index name");
		System.out.println("          -> build_json : create the daily JSON files with each index histo");
		System.out.println("          -> cron_tasks : execute all daily tasks");
		System.out.println("          -> night_tasks : execute all nightly tasks");
	}
}
