package virtualindex.virtualindexcore.kpireport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class AnalysorsReport {

	private static String analysorsNbStatus = "";
	private static ArrayList<String> anlaysorKoStratName = new ArrayList<>();
	private static ArrayList<Integer> anlaysorKoStratIdTodayNb = new ArrayList<>();
	private static ArrayList<Integer> anlaysorKoStratIdYesterdayNb = new ArrayList<>();

	private static int todayAnalysorsNb;
	private static int yesterdayAnalysorsNb;
	private static boolean totalKoState = false;
	private static final Logger logger = Logger.getLogger(Principal.class.getName());

	private static void performChecks(String date) throws SQLException {
		logger.info("Start analysing Analysors");

		logger.info("   - Check 1: Start counting todays's total analysors");

		todayAnalysorsNb = SqlRequests2.countAnalysors(date);
		if (todayAnalysorsNb == 0) {
			totalKoState = true;
		}
		logger.info("   - Check 1 done");
		logger.info("   - Check 2: Start counting yesterday's total analysors");
		yesterdayAnalysorsNb = SqlRequests2.countAnalysors(DateProcessing.offsetSqlDate(-1, date));
		logger.info("   - Check 2 done");
		logger.info("   - Check 3: Start counting todays's  analysors for each currency");
		countAnalysors(date);
		logger.info("   - Check 3 done");
		logger.info("End analysing Analysors");
	}

	public static String generate(String date) throws SQLException {
		performChecks(date);
		logger.info("Start generating Analysors KPI string");
		String report = "";
		String separator = "----------------------------------------------------------";
		String newLine = System.getProperty("line.separator");
		String smallSeparator = "---";

		if (totalKoState || todayAnalysorsNb == 0) {
			report = separator + newLine + smallSeparator + " ANALYSORS " + smallSeparator + "> KO : NO ANALYSOR COMPUTED"
					+ newLine;

			return report;
		} else {
			// Part 1.
			report = separator + newLine + smallSeparator + " ANALYSORS " + smallSeparator + "> " + analysorsNbStatus + newLine
					+ "Today: " + todayAnalysorsNb + newLine + "Yesterday: " + yesterdayAnalysorsNb + newLine;
			// Part 2. -> if status = KO
			if (analysorsNbStatus.equals("KO")) {
				report = report + "Details: " + newLine;
				int itr = 0;
				for (String stratName : anlaysorKoStratName) {
					report = report + "   " + stratName + ": " + "Today: " + anlaysorKoStratIdTodayNb.get(itr) + " / Yesterday: "
							+ anlaysorKoStratIdYesterdayNb.get(itr) + newLine;

					itr++;
				}
				report = report + newLine;
			}

			logger.info("End generating Analysors KPI string");
			return report;
		}
	}

	private static void countAnalysors(String date) throws SQLException {
		analysorsNbStatus = "OK";
		for (int stratId : SqlRequests2.getAllStratsId()) {
			logger.info("      - Currency analysed: " + stratId);
			logger.info("            - Start count strat's today analysors");
			int stratTodayAnalysorsNb = SqlRequests2.countAnalysors(date, stratId);
			logger.info("            - End count strat's today analysors");
			logger.info("            - Start count strat's yesterday analysors");
			int stratYesterdayAnalysorsNb = SqlRequests2.countAnalysors(DateProcessing.offsetSqlDate(-1, date), stratId);
			logger.info("            - End count strat's yesterday analysors");
			if (stratYesterdayAnalysorsNb > stratTodayAnalysorsNb) {
				logger.info("     - stratYesterdayAnalysorsNb > stratTodayAnalysorsNb ");

				anlaysorKoStratName.add(SqlRequests2.getStratName(stratId));

				anlaysorKoStratIdTodayNb.add(stratTodayAnalysorsNb);

				anlaysorKoStratIdYesterdayNb.add(stratYesterdayAnalysorsNb);

				analysorsNbStatus = "KO";
			}
		}
	}
}
