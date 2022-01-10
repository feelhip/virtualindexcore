package virtualindex.virtualindexcore.createindex;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class StoreStrategy
{
	//private SqlRequests req = new SqlRequests();
	private ArrayList<Integer> indexCompoIds = new ArrayList<Integer>();
	private String computedStartDate;
	final static Logger logger = Logger.getLogger(Principal.class.getName());

	public void create(String stratName,
			ArrayList<String> indexCompoNames,
			ArrayList<BigDecimal> indexCompoWeights,
			BigDecimal base, String direction, String fullName, String stratDes, String currency, String startDate) throws Exception
	{
		Check check = new Check(stratName, indexCompoNames, indexCompoWeights, direction);

		// DbConnection customReq = new DbConnection();
		if (check.getResult())
		{

			SqlRequests2.createStrat(stratName, fullName, stratDes);
			int stratId = SqlRequests2.getStratId(stratName);

			for (String name : indexCompoNames)
			{
				this.indexCompoIds.add(SqlRequests2.getStratId(name));
			}
			// We add 1 day to the start date as the return at strategy day 1 is
			// 0
			this.computedStartDate = getStartDate(startDate);

			SqlRequests2.configureStrat(stratId, this.computedStartDate, base, direction, currency);

			for (int x = 0; x < indexCompoNames.size(); x++)
			{
				SqlRequests2.addUnderlying(stratId, this.indexCompoIds.get(x), indexCompoWeights.get(x));
				/*
				 * customReq.execute("write",
				 * "INSERT INTO strategies_underlyings(id_strategy, id_underlying, underlying_weight)"
				 * + "VALUES ('"+strat_id+"','"+this.indexCompoIds.get(x)+"','"+
				 * indexCompoWeights.get(x)+"')");
				 */
			}
			logger.info("New Strategy created successfully");

		}
		else
		{
			logger.severe("Strategy configuration issue " + check.getError());
		}

	}

	private String getStartDate(String date) throws ParseException, SQLException
	{
		if (date.equals("default"))
		{
			return DateProcessing.offsetSqlDate(1, SqlRequests2.selectMinStartDate(this.indexCompoIds));
		}
		else
		{
			return date;
		}
	}

}
