package virtualindex.virtualindexcore.cyclicaljobscontrollers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.analysors.AnalysorsPublisher;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.OpenSqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class AnalysorsSelector extends Thread {
	

	private final static Logger logger = Logger.getLogger(Principal.class.getName());



	public static void compute() throws SQLException, ParseException {
		calculate(SqlRequests2.getAllStratsId());
	}

	public static void compute(String ccy) throws SQLException, ParseException {
		ArrayList<Integer> ccyIdList = new ArrayList<>();
		SqlRequests req = new SqlRequests();
		ccyIdList.add(req.getStratId(ccy));
		calculate(ccyIdList);
	}

	public static void calculate(ArrayList<Integer> ccyIdList) throws SQLException, ParseException {
		AnalysorsPublisher dailyAnalysor = new AnalysorsPublisher();
		SqlRequests req = new SqlRequests();
		String today = DateProcessing.todaySqlDate();
		// We need to calculate the daily returns in priority as other analysors
		// are based on it.
		for (int id : ccyIdList) {

			// DAILY RETURNS
			dailyAnalysor.dailyReturns(today, id);
		}

		int[] periodsList = { 7, 15, 30, 60, 90, 180, 365 };

		for (int id : ccyIdList) {

			for (int period : periodsList) {
				dailyAnalysor.slidingReturns(period + "d_Return", today, period, id, req);
				dailyAnalysor.slidingVol(period + "d_StDev", today, period, id, req);
				dailyAnalysor.avgReturn(id, today, period, req);
			}

			// YEAR TO DATE
			// RETURN
			dailyAnalysor.ytdReturn(today, id, req);
			// STANDARD DEVIATION
			dailyAnalysor.ytdVol(today, id, req);
			// AVERAGE RETURN
			dailyAnalysor.ytdAvgReturn(today, id, req);

			// SHARPE RATIO
			// Yearly
			dailyAnalysor.sharpeRatio(id, today, 365, req);
			// 6 Months
			dailyAnalysor.sharpeRatio(id, today, 180, req);

			logger.info("Analysors computed for currency id# " + id);

		}
	}

	public static void calculateCorrelMatrix(ArrayList<String> ccyIdList) throws ParseException, SQLException {
		AnalysorsPublisher dailyAnalysor = new AnalysorsPublisher();
		String today = DateProcessing.todaySqlDate();

		int[] periodsList = { 7, 15, 30, 60, 90, 180, 365 };

		for (String id : ccyIdList) {
			ArrayList<String> stratListDone = new ArrayList<>();
			ArrayList<String> stratList1 = new ArrayList<>();
			ArrayList<String> stratList2 = new ArrayList<>();
			ArrayList<BigDecimal> correlList = new ArrayList<>();
			ArrayList<String> dateList = new ArrayList<>();
			ArrayList<Integer> periodList = new ArrayList<>();
			logger.info("Start computing correlation for currency id# " + id);
			OpenSqlRequests openReq = new OpenSqlRequests(); //Creation of a kept-open SQL request in order to get the daily returns
			for (int period : periodsList) {
				stratListDone.add(id);
				// Correlation Computation
				for (String id2 : ccyIdList) {
					BigDecimal correl = dailyAnalysor.correl(id, id2, DateProcessing.offsetSqlDate(-period, today), period, openReq,
							today);

					boolean redundancy = false;
					for (int y = 0; y < stratListDone.size(); y++) {
						if (id2.equals(stratListDone.get(y))) {
							redundancy = true;
							break;
						} else {
							redundancy = false;
						}
					}

					if (correl != null && !id2.equals(id) && redundancy == false) {

						try {
							stratList1.add(id);
							stratList2.add(id2);
							correlList.add(correl);
							dateList.add(today);
							periodList.add(period);
						} catch (Exception e) {
							logger.warning("Correlation not set for " + id + " / " + id2 + "\nStacktrace:\n" + e.toString());
						}
					}
				}
			}
			openReq.closeConnection(); //Closing of the kept-open SQL request in order to get the daily returns
			logger.info("Start SQL batch queries storage for " + id);
			
			SqlRequests2.setCorrelationBatch(stratList1, stratList2, correlList, dateList, periodList);
			logger.info("End SQL batch queries storage for " + id);
			logger.info("End computing correlation for currency id# " + id);
			
		}
		
		
	}
	

	 
	 
}
