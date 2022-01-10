package virtualindex.virtualindexcore.kpireport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class CurrenciesReport {

	private static ArrayList<Integer> ccyIdList = new ArrayList<Integer>();
	private static ArrayList<Integer> todayCcyIdList = new ArrayList<>();

	private static ArrayList<String> checkList = new ArrayList<String>();

	private static ArrayList<BigDecimal> closeList = new ArrayList<BigDecimal>();
	private static ArrayList<String> missingCcy = new ArrayList<String>();
	private static ArrayList<String> zeroCcy = new ArrayList<String>();
	private static ArrayList<String> perfCcy = new ArrayList<String>();
	private static ArrayList<BigDecimal> perfCcyVal = new ArrayList<BigDecimal>();

	private static String txt_totalStatus = "";
	private static String txt_total = "";
	private static String txt_downloaded = "";

	private static boolean totalKoState = false;
	private static String totalKoLog = "";
	private static final Logger logger = Logger.getLogger(Principal.class.getName());

	public static String generate(String date) {
		performChecks(date);

		logger.info("Start generating Currencies KPI string");
		String report = "";
		String separator = "----------------------------------------------------------";
		String newLine = System.getProperty("line.separator");
		String smallSeparator = "---";

		if (totalKoState) {
			report = newLine + separator + newLine + smallSeparator + " CURRENCIES " + smallSeparator
					+ "> KO : NO CURRENCY DOWNLOADED" + newLine + "SQL Request logs:" + newLine + totalKoLog + newLine;

			return report;
		} else {

			currenciesCheck(checkList);
			// Part 1.
			report = newLine + separator + newLine + smallSeparator + " CURRENCIES " + smallSeparator + "> " + txt_totalStatus
					+ newLine + "Total: " + txt_total + newLine + "Downloaded: " + txt_downloaded + newLine;

			// Part 2 - if missing currencies

			if (missingCcy.size() != 0) {
				report = report + "- Missing:" + newLine;
				for (String ccy : missingCcy) {
					report = report + "   " + ccy + newLine;
				}
			}

			// Part 3 - if price validation needed

			if (zeroCcy.size() != 0) {
				report = report + "- Price Validation required for null price:" + newLine;
				for (String ccy : zeroCcy) {
					report = report + "   " + ccy + " : 0" + newLine;
				}
			}

			// Part 4 - if perf validation needed
			if (perfCcy.size() != 0) {
				int itr = 0;
				report = report + "- Price Validation required for performance:" + newLine;
				for (String ccy : perfCcy) {
					report = report + "   " + ccy + " : " + perfCcyVal.get(itr) + newLine;
					itr++;
				}
			}

			logger.info("End Currencies KPI string");
			return report;
		}
	}

	private static void performChecks(String date) {
		logger.info("Start analysing Currencies");
		logger.info("   - Check 1: count data");

		ccyIdList = SqlRequests2.getAllCcyIds();

		try {
			todayCcyIdList = SqlRequests2.getStratIdFromClose("cryptsy", date); // CRYPTSY
		} catch (Exception e) {
			totalKoState = true;
			totalKoLog = e.toString();
		}
		txt_total = String.valueOf(ccyIdList.size());
		txt_downloaded = String.valueOf(todayCcyIdList.size());
		downloadCheck();
		logger.info("   - Check 1 done");
		logger.info("   - Check 2: store data effectively downloaded");
		missingCcyCheck(date);
		logger.info("   - Check 2 done");
		logger.info("   - Check 3: verify if no currency = 0");
		zeroCheck();
		logger.info("   - Check 3 done");
		logger.info("   - Check 4: verify performance");
		performanceValidationCheck(date);
		logger.info("   - Check 4 done");
		logger.info("End analysing Currencies");
	}

	private static void downloadCheck() {
		if (ccyIdList.size() == todayCcyIdList.size()) {
			checkList.add("OK");
		} else {
			checkList.add("KO");
		}
	}

	private static void missingCcyCheck(String date) {
		// Currencies downloading status

		ArrayList<Integer> ccyIdList = SqlRequests2.getAllStratsId("cryptsy");

		// Currencies missing status
		for (int itr = 0; itr < ccyIdList.size(); itr++) {			
			try {	
				closeList.add(SqlRequests2.getStratClose(ccyIdList.get(itr), date));
			} catch (Exception e) {
				missingCcy.add(SqlRequests2.getStratName(ccyIdList.get(itr)));
				logger.warning("Missing close price for "+missingCcy.get(missingCcy.size()-1));
			}

		}
	}

	private static void zeroCheck() {
		int itr = 0;
		for (BigDecimal close : closeList) {
			if (close.compareTo(new BigDecimal("0")) == 0) {
				zeroCcy.add(SqlRequests2.getStratName(todayCcyIdList.get(itr)));
			}
			itr++;
		}
	}

	private static void performanceValidationCheck(String date) {
		for (int ccyId : todayCcyIdList) {
			try {
				BigDecimal stratPerformance = SqlRequests2.getReturn(ccyId, date);

				if (stratPerformance.compareTo(new BigDecimal("0.3")) == 1
						|| stratPerformance.compareTo(new BigDecimal("-0.3")) == -1) {
					perfCcy.add(SqlRequests2.getStratName(ccyId));
					perfCcyVal.add(stratPerformance);
				}
			} catch (Exception e) {
				logger.severe("Didn't manage to catch strategy performance. Check if performance is not null for  " + ccyId);
				logger.severe(e.toString());
			}
		}
	}

	private static void currenciesCheck(ArrayList<String> checkList) {
		for (String status : checkList) {
			if (status.equals("KO")) {
				txt_totalStatus = "KO";
				return;
			}

		}

		txt_totalStatus = "OK";
	}
}
