package virtualindex.virtualindexcore.kpireport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.computer.PriceReturn;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class MarketDataReport {

	private static ArrayList<String> marketDataList = new ArrayList<>();
	private static ArrayList<String> todayMarketDataList = new ArrayList<>();
	private static ArrayList<String> nullMarketDataList = new ArrayList<>();
	private static ArrayList<String> highPerfMarketDataList = new ArrayList<>();
	private static ArrayList<BigDecimal> highPerfMarketDataPerfList = new ArrayList<BigDecimal>();
	private static int marketDataNb;
	private static int todayMarketDataNb;
	private static String downloadStatus = "";
	private static String marketDateStatus = "OK";
	private static boolean totalKoState = false;
	private static String totalKoLog = "";
	private static final Logger logger = Logger.getLogger(Principal.class.getName());

	private static void perfomrChecks(String date) throws Exception {

		logger.info("Start analysing Market Data");

		try {
			todayMarketDataList = SqlRequests2.getDateMarketDataList(date);
		} catch (Exception e) {
			totalKoState = true;
			totalKoLog = e.toString();
		}
		marketDataList = SqlRequests2.getMarketDataList();
		// Check 1 - count market data downloaded today
		logger.info("   - Check 1: count data");
		countData();
		logger.info("   - Check 1 done");
		// Check 2 - check if no market data = 0
		logger.info("   - Check 2: verify if no market data = 0");
		checkNullData(date);
		logger.info("   - Check 2 done");
		// Check 3 - perf </> +/- 10%
		logger.info("   - Check 3: verify performance");
		checkPerfData(date);
		logger.info("   - Check 3 done");
		logger.info("End analysing market data");

	}

	public static String generate(String date) throws Exception {
		perfomrChecks(date);
		logger.info("Start generating Market Data KPI string");
		String report = "";
		String separator = "----------------------------------------------------------";
		String newLine = System.getProperty("line.separator");
		String smallSeparator = "---";

		if (totalKoState) {
			report = separator + newLine + smallSeparator + " MARKET DATA " + smallSeparator + "> KO : NO MARKET DATA DOWNLOADED"
					+ newLine + "SQL Request logs:" + newLine + totalKoLog + newLine;

			return report;
		} else {
			// Part 1.
			report = separator + newLine + smallSeparator + " MARKET DATA " + smallSeparator + "> " + marketDateStatus + newLine
					+ downloadStatus + "Nb of data downloaded / to download: " + todayMarketDataNb + " / " + marketDataNb
					+ newLine;
			// Part 2.
			if (nullMarketDataList.size() > 0) {
				report = report + "Market Data with null value: " + newLine;
				for (String dataName : nullMarketDataList) {
					report = report + "   " + dataName + " is null" + newLine;
				}
			}
			// Part 3.
			if (highPerfMarketDataList.size() > 0) {
				report = report + "Market Data with absolute performance > 10%: " + newLine;
				int itr = 0;
				for (String dataName : highPerfMarketDataList) {
					report = report + "   " + dataName + " 1-d perf: " + highPerfMarketDataPerfList.get(itr) + newLine;
					itr++;
				}
			}

			logger.info("End generating Market Data KPI string");
			return report;
		}
	}

	// check 1
	private static void countData() {
		String newLine = System.getProperty("line.separator");
		marketDataNb = marketDataList.size();
		todayMarketDataNb = todayMarketDataList.size();
		if (marketDataNb != todayMarketDataNb) {
			downloadStatus = "MISSING DATA" + newLine;
			marketDateStatus = "KO";
		}
	}

	// check 2

	private static void checkNullData(String date) {
		for (String dataName : todayMarketDataList) {
			if (SqlRequests2.getMarketData(dataName, date).compareTo(new BigDecimal("0")) == 0) {
				nullMarketDataList.add(dataName);
				marketDateStatus = "KO";
			}
		}
	}

	// check 3
	private static void checkPerfData(String date) throws Exception {

		for (String dataName : todayMarketDataList) {
			BigDecimal dataPerf = PriceReturn.compute(SqlRequests2.getMarketData(dataName, DateProcessing.offsetSqlDate(-1, date)),
					SqlRequests2.getMarketData(dataName, date));
			if (dataPerf.abs().compareTo(new BigDecimal("0.1")) == 1) {
				highPerfMarketDataList.add(dataName);
				highPerfMarketDataPerfList.add(dataPerf);
			}
		}
	}

}
