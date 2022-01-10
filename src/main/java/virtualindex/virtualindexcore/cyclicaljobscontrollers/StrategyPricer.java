package virtualindex.virtualindexcore.cyclicaljobscontrollers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.computer.SpotPrice;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class StrategyPricer
{
	private int id;
	private String sqlStrikeDate;
	private String strategyName;
	private String direction;
	private ArrayList<Integer> compoIdsList;
	private ArrayList<String> compoNamesList;
	private ArrayList<BigDecimal> compoWeightsList;
	private ArrayList<BigDecimal> underlyingsStrikeList;
	private BigDecimal base;

	private SqlRequests req = new SqlRequests();

	final static Logger logger = Logger.getLogger(Principal.class.getName());

	public StrategyPricer(String strategyName) throws SQLException
	{
		this.strategyName = strategyName;
		this.id = this.req.getStratId(strategyName);
		ArrayList<ArrayList<Object>> composition = SqlRequests2.getComposition(this.id);
		this.direction = this.req.getStratDirection(this.id);
		this.base = this.req.getStratBase(this.id);
		this.sqlStrikeDate = this.req.getIndexStartDate(this.id);
		this.compoNamesList = SqlRequests2.getComposition_names(composition);
		this.compoIdsList = SqlRequests2.getComposition_ids(composition);
		this.compoWeightsList = SqlRequests2.getComposition_weights(composition);
		this.underlyingsStrikeList = this.req.getStratClose(this.compoIdsList, this.sqlStrikeDate);
	}

	public BigDecimal calculateCryptsyLivePrice(String side) throws Exception
	{
		CcyLivePricing livePricing = new CcyLivePricing();
		BigDecimal spotPrice = SpotPrice.calculate(compoWeightsList, this.underlyingsStrikeList, livePricing.getLivePrice(compoNamesList, side), this.base);
		logger.info("Live spot Price for " + this.strategyName + " : " + spotPrice);
		return spotPrice;
	}

	private BigDecimal calculateClosePrice(String sqlDate) throws Exception
	{
		BigDecimal spotPrice = SpotPrice.calculate(compoWeightsList, this.underlyingsStrikeList, this.req.getStratClose(this.compoIdsList, sqlDate), this.base);
		logger.info("Close price for " + this.strategyName + " : " + sqlDate + " - " + spotPrice);
		return spotPrice;
	}

	public void storeTodayClosePrice(String sqlDate) throws Exception
	{
		Checks check = new Checks();
		if (check.todayValueNotPresent(this.id))
		{
			this.req.storeStrategyClosePrice(this.id, calculateClosePrice(sqlDate), sqlDate, "00:00:00", this.direction, "pricer");
		}
		else
		{
			logger.info("Today' spot price already updated in database");
		}
	}

	public void storeClosePrice(String sqlDate) throws Exception
	{
		this.req.storeStrategyClosePrice(this.id, calculateClosePrice(sqlDate), sqlDate, "00:00:00", this.direction, "pricer");
	}

}
