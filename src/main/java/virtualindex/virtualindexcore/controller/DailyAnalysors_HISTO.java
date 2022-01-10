package virtualindex.virtualindexcore.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.analysors.AnalysorsPublisher;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class DailyAnalysors_HISTO {

	private static SqlRequests req = new SqlRequests();

	private final static Logger logger = Logger.getLogger(Principal.class.getName());

	private ArrayList<Integer> ccyIdList = new ArrayList<>();

	public void calculate(int id) throws SQLException, ParseException {
		this.ccyIdList.add(id);
		computeAnalysors(this.ccyIdList);
	}

	public void calculateAll() throws SQLException, ParseException {
		computeAnalysors(SqlRequests2.getAllStratsId());
	}

	
	private void computeAnalysors(ArrayList<Integer> ccyIdList) throws SQLException, ParseException {

		for (int id : ccyIdList) {

			logger.info("Analysors computation STARTED for currency id# " + id);

			int total = req.getClosesNumber(id);
			String startDate = req.getStratStartDate(id);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(startDate);

			Calendar calDate = new GregorianCalendar();
			calDate.setTime(date);

			AnalysorsPublisher dailyAnalysorCompute = new AnalysorsPublisher();

			for (int x = 0; x < total; x++) {
				String today = sdf.format(calDate.getTime()).toString();

				// RETURN
				// Daily
				dailyAnalysorCompute.dailyReturns(today, id);
				// Weekly
				dailyAnalysorCompute.slidingReturns("7d_Return", today, 7, id, req);
				// Monthly
				dailyAnalysorCompute.slidingReturns("30d_Return", today, 30, id, req);
				// 3 Months
				dailyAnalysorCompute.slidingReturns("90d_Return", today, 90, id, req);
				// 6 Months
				dailyAnalysorCompute.slidingReturns("180d_Return", today, 180, id, req);
				// Yearly
				dailyAnalysorCompute.slidingReturns("365d_Return", today, 365, id, req);
				// YTD
				dailyAnalysorCompute.ytdReturn(today, id, req);

				// STANDARD DEVIATION
				// Weekly
				dailyAnalysorCompute.slidingVol("7d_StDev", today, 7, id, req);
				// Monthly
				dailyAnalysorCompute.slidingVol("30d_StDev", today, 30, id, req);
				// 3 Months
				dailyAnalysorCompute.slidingVol("90d_StDev", today, 90, id, req);
				// 6 Months
				dailyAnalysorCompute.slidingVol("180d_StDev", today, 180, id, req);
				// Yearly
				dailyAnalysorCompute.slidingVol("365d_StDev", today, 365, id, req);
				// YTD
				dailyAnalysorCompute.ytdVol(today, id, req);

				// SHARPE RATIO
				// Yearly
				dailyAnalysorCompute.sharpeRatio(id, today, 365, req);
				// 6 Months
				dailyAnalysorCompute.sharpeRatio(id, today, 180, req);

				// AVERAGE RETURN
				// 7 d
				dailyAnalysorCompute.avgReturn(id, today, 7, req);
				// 15 d
				dailyAnalysorCompute.avgReturn(id, today, 15, req);
				// 30 d
				dailyAnalysorCompute.avgReturn(id, today, 30, req);
				// 60 d
				dailyAnalysorCompute.avgReturn(id, today, 60, req);
				// 180 d
				dailyAnalysorCompute.avgReturn(id, today, 180, req);
				// 270 d
				dailyAnalysorCompute.avgReturn(id, today, 270, req);
				// 365 d
				dailyAnalysorCompute.avgReturn(id, today, 365, req);
				// YTD
				dailyAnalysorCompute.ytdAvgReturn(today, id, req);

				calDate.add(Calendar.DATE, 1);

			}
			logger.info("Analysors computation FINISHED for currency id# " + id);
		}

	}

}
