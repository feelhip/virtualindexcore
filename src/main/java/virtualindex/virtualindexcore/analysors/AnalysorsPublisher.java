package virtualindex.virtualindexcore.analysors;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.computer.Correlation;
import virtualindex.virtualindexcore.computer.PriceReturn;
import virtualindex.virtualindexcore.computer.SharpeRatio;
import virtualindex.virtualindexcore.computer.SimpleCalc;
import virtualindex.virtualindexcore.computer.StandardDeviation;
import virtualindex.virtualindexcore.sqlrequests.OpenSqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class AnalysorsPublisher {

	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");
	private Calendar todayCal = new GregorianCalendar();
	private Calendar yesterdayCal = new GregorianCalendar();

	private final static Logger logger = Logger.getLogger(Principal.class.getName());

	public void ytdReturn(String endDate, int stratId, SqlRequests request) throws ParseException {
		this.todayCal.setTime(this.sdfY.parse(endDate));
		Calendar calYear = this.todayCal;
		Date yearDate = calYear.getTime();
		String stringYear = this.sdfY.format(yearDate);
		String firstDay = stringYear + "-01-01";
		String analysorName = "YTD" + stringYear + "Return";
		try {
			request.setSlidingAnalysors(analysorName, stratId, endDate,
					PriceReturn.compute(SqlRequests2.getStratClose(stratId, firstDay), SqlRequests2.getStratClose(stratId, endDate)));

		} catch (Exception e) {
			logger.warning("Cannot calculate YTD return for strat id#" + stratId + "; check if the " + firstDay + " is populated");
			logger.warning(e.toString());
		}
	}

	public void slidingReturns(String analysorName, String endDate, int offsetDays, int stratId, SqlRequests request)
			throws ParseException {
		todayCal.setTime(this.sdf.parse(endDate));
		this.yesterdayCal = todayCal;
		this.yesterdayCal.add(Calendar.DATE, -1 * offsetDays);
		Date yesterdayDate = yesterdayCal.getTime();
		String yesterdayString = this.sdf.format(yesterdayDate);
		try {
			request.setSlidingAnalysors(analysorName, stratId, endDate,
					PriceReturn.compute(SqlRequests2.getStratClose(stratId, yesterdayString), SqlRequests2.getStratClose(stratId, endDate)));
		} catch (Exception e) {
			logger.warning("Cannot calculate " + analysorName + " for ccy id#" + stratId + "; check if the last " + offsetDays
					+ " days are populated");
			logger.warning(e.toString());
		}
	}

	public void dailyReturns(String endDate, int stratId) throws ParseException {
		this.todayCal.setTime(this.sdf.parse(endDate));
		this.yesterdayCal = this.todayCal;
		this.yesterdayCal.add(Calendar.DATE, -1);
		Date yesterdayDate = yesterdayCal.getTime();
		String yesterdayString = this.sdf.format(yesterdayDate);
		try {
			SqlRequests2.setDailyReturn(
					PriceReturn.compute(SqlRequests2.getStratClose(stratId, yesterdayString), SqlRequests2.getStratClose(stratId, endDate)),
					stratId, endDate);
		} catch (Exception e) {
			logger.warning("Cannot calculate Daily Return for ccy id#" + stratId + "; check if the day before " + endDate
					+ " is populated");
			logger.warning(e.toString());
		}
	}

	public void slidingVol(String analysorName, String endDate, int offsetDays, int stratId, SqlRequests request)
			throws SQLException, ParseException {

		this.todayCal.setTime(this.sdf.parse(endDate));
		Calendar startDateCal = this.todayCal;
		startDateCal.add(Calendar.DATE, -1 * offsetDays);
		Date startDateDate = startDateCal.getTime();
		String startDate = this.sdf.format(startDateDate);
		if (!checkStartDate(startDate, stratId, request)) {
			logger.warning("Given start (" + startDate + ") date is before the effective start date for strategy id#" + stratId);
			return;
		}

		try {
			request.setSlidingAnalysors(analysorName, stratId, endDate,
					StandardDeviation.compute(request.getStratReturns(startDate, endDate, stratId)));
		} catch (Exception e) {
			logger.warning("Cannot calculate " + analysorName + " for ccy id#" + stratId + "; check if the last " + offsetDays
					+ " days are populated");
			logger.warning(e.toString());
		}
	}

	public void ytdVol(String endDate, int stratId, SqlRequests request) throws ParseException, SQLException {

		Date startingDateYear = this.sdfY.parse(endDate);
		String firstDay = this.sdfY.format(startingDateYear) + "-01-01";
		String analysorName = "YTD" + this.sdfY.format(startingDateYear) + "StDev";
		if (!checkStartDate(firstDay, stratId, request)) {
			logger.warning("Given start (" + firstDay + ") date is before the effective start date for strategy id#" + stratId);
			return;
		}

		try {
			request.setSlidingAnalysors(analysorName, stratId, endDate,
					StandardDeviation.compute(request.getStratReturns(firstDay, endDate, stratId)));
		} catch (Exception e) {
			logger.warning("Cannot calculate " + analysorName + " for ccy id#" + stratId + "; check if index is populated since "
					+ firstDay);
			logger.warning(e.toString());
		}
	}

	private boolean checkStartDate(String startDate, int stratId, SqlRequests request) throws ParseException, SQLException {
		Date realStartDate = sdf.parse(request.getStratStartDate(stratId));
		Date givenStartDate = sdf.parse(startDate);
		return(realStartDate.before(givenStartDate) || realStartDate.equals(givenStartDate)); 

	}

	public void sharpeRatio(int stratId, String date, int offsetDays, SqlRequests request) throws SQLException, ParseException {

		this.todayCal.setTime(this.sdf.parse(date));
		Calendar startDateCal = this.todayCal;
		startDateCal.add(Calendar.DATE, -1 * offsetDays);
		Date startDateDate = startDateCal.getTime();
		String startDate = this.sdf.format(startDateDate);
		if (!checkStartDate(startDate, stratId, request)) {
			// logger.warning("Given start ("+startDate+") date is before the effective start date for strategy id#"+stratId);
			return;
		}

		

		BigDecimal riskFreeRate = SqlRequests2.getMarketData(offsetDays + "d_TBill", date);
		try {
			BigDecimal stratAvgReturn = SimpleCalc.average(request.getStratReturns(startDate, date, stratId));

			BigDecimal stratStDev = request.getAnalysorValue(offsetDays + "d_StDev", stratId, date);

			BigDecimal result = SharpeRatio.compute(stratStDev, stratAvgReturn, riskFreeRate);
			request.setSlidingAnalysors(offsetDays + "d_Sharpe", stratId, date, result);
		}

		catch (Exception e) {
			logger.warning("No data at d-" + offsetDays + "- cannot compute Sharpe ratio for id#" + stratId);
			logger.warning(e.toString());
		}

	}

	public void avgReturn(int stratId, String date, int offsetDays, SqlRequests request) throws ParseException, SQLException {
		this.todayCal.setTime(this.sdf.parse(date));
		Calendar startDateCal = this.todayCal;
		startDateCal.add(Calendar.DATE, -1 * offsetDays);
		Date startDateDate = startDateCal.getTime();
		String startDate = this.sdf.format(startDateDate);
		if (!checkStartDate(startDate, stratId, request)) {
			// logger.warning("Given start ("+startDate+") date is before the effective start date for strategy id#"+stratId);
			return;
		}

		try {
			BigDecimal stratAvgReturn = SimpleCalc.average(request.getStratReturns(startDate, date, stratId));
			request.setSlidingAnalysors(offsetDays + "d_AvgReturn", stratId, date, stratAvgReturn);
		} catch (Exception e) {
			logger.warning("No data at d-" + offsetDays + "- cannot compute Sharpe ratio for id#" + stratId);
			logger.warning(e.toString());
		}
	}

	public void ytdAvgReturn(String endDate, int stratId, SqlRequests request) throws ParseException, SQLException {
		Date startingDateYear = this.sdfY.parse(endDate);
		String firstDay = this.sdfY.format(startingDateYear) + "-01-01";
		String analysorName = "YTD" + this.sdfY.format(startingDateYear) + "AvgReturn";
		if (!checkStartDate(firstDay, stratId, request)) {
			// logger.warning("Given start ("+firstDay+") date is before the effective start date for strategy id#"+stratId);
			return;
		}

		try {
			BigDecimal stratAvgReturn = SimpleCalc.average(request.getStratReturns(firstDay, endDate, stratId));
			request.setSlidingAnalysors(analysorName, stratId, endDate, stratAvgReturn);
		} catch (Exception e) {
			logger.warning("Cannot calculate " + analysorName + " for ccy id#" + stratId + "; check if index is populated since "
					+ firstDay);
			logger.warning(e.toString());
		}
	}

	public BigDecimal correl(String strat1Name, String strat2Name, String startDate, int period, OpenSqlRequests request,String calculationDate) throws SQLException
	{
		Correlation correlation = new Correlation();
		ArrayList<BigDecimal> series1 = null;
		ArrayList<BigDecimal> series2 = null;
		
		try{
		 series1 = request.getReturnsInArray_OPEN(strat1Name, startDate);
		 series2 = request.getReturnsInArray_OPEN(strat2Name, startDate);
		}
		catch(Exception e)
		{
			logger.severe("Issue with GET of the strategies return");
		}
			
		if(series1.size() == series2.size() && (series1.size()==period ||series1.size()==period-1) && series1.get(0)!=null && series2.get(0)!=null)
		{
			try{
			return correlation.compute(series1,series2);
			}
			catch(Exception e)
			{
				logger.severe("Issue with the correlation computation (period = "+period+") of "+strat1Name +" and "+ strat2Name +"\nThe integrity of both series needs to be checked from "+startDate+"\n"+e.toString());
			}
			return null;
		}
		
		else
		{
			logger.warning("Period : "+period+"\nSeries' size "+strat1Name +" : " +series1.size()+"\nSeries' size "+strat2Name +" : " +series2.size()
					+"\nSeries' first member "+strat1Name +" : " +series1.get(0)+"\nSeries' first member "+strat2Name +" : " +series2.get(0));
			return null;}
	
		
		
		
	}

}
