package virtualindex.virtualindexcore.sqlrequests;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class SqlRequests {
	private DbConnection sqlConnection = new DbConnection();
	final Logger logger = Logger.getLogger(Principal.class.getName());
	private ResultSet rs;

	public int getStratId(String stratName) throws SQLException {

		ResultSet request = this.sqlConnection.execute("read", "SELECT id_strategy FROM strategies " + "WHERE strategy_name ='"
				+ stratName + "'");
		
		return (request.getInt(1));
	}

	public String getStratName(int stratId) throws SQLException {

		ResultSet request = this.sqlConnection.execute("read", "SELECT strategy_name FROM strategies " + "WHERE id_strategy ='"
				+ stratId + "'");
		return (request.getString(1));
	}







	public void storeNewOrder(int investorId, int stratId, BigDecimal grossBtcAmt, String direction, BigDecimal fees)
			throws SQLException {
		this.sqlConnection.execute("write",
				"INSERT INTO deals_histo ( investor_id,strat_id,gross_btc_amt,direction,date,time,fees) " + "VALUES ('" + investorId + "','" + stratId + "','" + grossBtcAmt + "','" + direction + "','"
						+ DateProcessing.todaySqlDate() + "', '"+DateProcessing.todaySqlTime()+"', '" + fees + "')");
	}




	

	public synchronized ArrayList<BigDecimal> getReturnsInArray(String stratName, String sqlDate) throws SQLException {
		ArrayList<BigDecimal> returnsArray = new ArrayList<BigDecimal>();

		ResultSet returnsSet = this.sqlConnection
				.execute(
						"read",
						"SELECT daily_return FROM close INNER JOIN strategies ON close.id_strategy = strategies.id_strategy WHERE strategies.strategy_name = '"
								+ stratName + "' AND close_date > '" + sqlDate + "' ORDER BY close_date ASC");

		do {
			returnsArray.add(returnsSet.getBigDecimal(1));
		} while (returnsSet.next());
		return returnsArray;
	}

	public ArrayList<BigDecimal> getReturnsInArray(int stratId, String sqlDate) throws SQLException {
		ArrayList<BigDecimal> returnsArray = new ArrayList<BigDecimal>();
		ResultSet returnsSet = this.sqlConnection.execute("read", "SELECT daily_return FROM close WHERE id_strategy = " + stratId
				+ " AND close_date > '" + sqlDate + "' ORDER BY close_date ASC");
		do {
			returnsArray.add(returnsSet.getBigDecimal(1));
		} while (returnsSet.next());
		return returnsArray;
	}

	public BigDecimal getReturn(int stratId, String sqlDate) throws SQLException {
		BigDecimal returnValue = new BigDecimal("0");
		ResultSet returnsSet = this.sqlConnection.execute("read", "SELECT daily_return FROM close WHERE id_strategy = " + stratId
				+ " AND close_date = '" + sqlDate + "'");

		returnValue = (returnsSet.getBigDecimal(1));

		return returnValue;
	}

	public ArrayList<String> getAllIndicesNames() throws SQLException {
		ArrayList<String> stratNamesList = new ArrayList<String>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT strategy_name FROM strategies " + "WHERE instr_type = 'index'");

		do {
			stratNamesList.add(rs.getString(1));
		} while (rs.next());

		return stratNamesList;
	}

	public ArrayList<String> getAllCcyNames() throws SQLException {
		ArrayList<String> stratNamesList = new ArrayList<String>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT strategy_name FROM strategies " + "WHERE instr_type = 'ccy'");

		do {
			stratNamesList.add(rs.getString(1));
		} while (rs.next());

		return stratNamesList;
	}




	public ArrayList<Integer> getAllIndicesId() throws SQLException {
		ArrayList<Integer> stratsIdList = new ArrayList<Integer>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT id_strategy FROM strategies WHERE instr_type = 'index'");

		do {
			stratsIdList.add(rs.getInt(1));
		} while (rs.next());

		return stratsIdList;
	}



	public ArrayList<String> getAllCcyNames(String exchangeName) throws SQLException {
		ArrayList<String> stratNamesList = new ArrayList<String>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT strategy_name FROM strategies "
				+ "WHERE instr_type = 'ccy' and default_source = '" + exchangeName + "'");

		do {
			stratNamesList.add(rs.getString(1));
		} while (rs.next());

		return stratNamesList;
	}

	public ArrayList<String> getAllStratNames(String exchangeName) throws SQLException {
		ArrayList<String> stratNamesList = new ArrayList<String>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT strategy_name FROM strategies");

		do {
			stratNamesList.add(rs.getString(1));
		} while (rs.next());

		return stratNamesList;
	}

	public ArrayList<BigDecimal> getStratClose(int stratId) throws SQLException {
		ArrayList<BigDecimal> closeList = new ArrayList<BigDecimal>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT close_value FROM close WHERE id_strategy = " + stratId);
		do {
			try {
				closeList.add(rs.getBigDecimal(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (rs.next());

		return closeList;
	}



	public ArrayList<BigDecimal> getStratClose(ArrayList<Integer> stratIdList, String date) throws SQLException {
		ArrayList<BigDecimal> stratCloseList = new ArrayList<BigDecimal>();
		for (int id : stratIdList) {

			ResultSet rs = this.sqlConnection.execute("read", "SELECT close_value FROM close WHERE id_strategy = " + id + " "
					+ "AND close_date = '" + date + "'");
			stratCloseList.add(rs.getBigDecimal(1));
		}

		return stratCloseList;
	}

	public ArrayList<String> getStratCloseDate(int stratId) throws SQLException {
		ArrayList<String> closeDateList = new ArrayList<String>();
		ResultSet rs = this.sqlConnection.execute("read", "SELECT close_date FROM close WHERE id_strategy = " + stratId);
		do {
			closeDateList.add(rs.getString(1));
		} while (rs.next());

		return closeDateList;
	}

	public void testSqlReq() throws SQLException {
		this.sqlConnection.execute("write", "INSERT INTO test (testcol,testdate, testtime) " + "VALUES ('TEST','"
				+ DateProcessing.todaySqlDate() + "','" + DateProcessing.todaySqlTime() + "')");
	}

	public void storeStrategyClosePrice(int stratId, BigDecimal close, String closeDate, String closeTime, String direction,
			String source) throws SQLException {
		this.sqlConnection.execute("write",
				"INSERT INTO close (id_strategy,close_value, close_date, close_time, direction, close_source) " + "VALUES ('"
						+ stratId + "','" + close + "','" + closeDate + "','" + closeTime + "','" + direction + "','" + source
						+ "')");
	}

	public void storeStrategyClosePrice(int stratId, BigDecimal close, String direction, String source) throws SQLException {
		this.sqlConnection.execute("write",
				"INSERT INTO close (id_strategy,close_value, close_date, close_time, direction, close_source) " + "VALUES ('"
						+ stratId + "','" + close + "','" + DateProcessing.todaySqlDate() + "','" + DateProcessing.todaySqlTime()
						+ "','" + direction + "','" + source + "')");
	}

	public void setDailyReturn(BigDecimal analysorValue, int stratId, String date) throws SQLException {
		this.sqlConnection.execute("write", "UPDATE close SET daily_return = " + analysorValue + " " + "WHERE id_strategy = "
				+ stratId + " AND close_date = '" + date + "'");
	}

	public void setSlidingAnalysors(String analysorName, int stratId, String date, BigDecimal value) throws SQLException {
		this.sqlConnection.execute("write", "INSERT INTO analysors (name, strat_id, close_date, value, compute_time) VALUES "
				+ "('" + analysorName + "'," + stratId + ", '" + date + "', '" + value + "',NOW())");
	}

	public ArrayList<BigDecimal> getStratCloses(String startDate, String endDate, int stratId) throws SQLException {
		ArrayList<BigDecimal> closeList = new ArrayList<>();

		ResultSet rs = this.sqlConnection.execute("read", "SELECT close_value FROM close " + " WHERE id_strategy = '" + stratId
				+ "' AND close_date > '" + startDate + "' AND close_date <='" + endDate + "'");

		do {
			closeList.add(rs.getBigDecimal(1));
		} while (rs.next());

		return closeList;
	}

	public ArrayList<BigDecimal> getStratReturns(String startDate, String endDate, int stratId) throws SQLException {
		ArrayList<BigDecimal> closeList = new ArrayList<>();

		ResultSet rs = this.sqlConnection.execute("read", "SELECT daily_return FROM close " + " WHERE id_strategy = '" + stratId
				+ "' AND close_date > '" + startDate + "' AND close_date <='" + endDate + "'");

		do {
			closeList.add(rs.getBigDecimal(1));
		} while (rs.next());

		return closeList;
	}

	public String getStratStartDate(int id) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT MIN(close_date) FROM close WHERE id_strategy = " + id);

		return rs.getString(1);
	}

	public int getClosesNumber(int id) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT COUNT(close_date) FROM close WHERE id_strategy = " + id);

		return rs.getInt(1);
	}






	public BigDecimal getAnalysorValue(String anlysorName, int stratId, String date) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT value FROM analysors " + "WHERE strat_id = '" + stratId
				+ "' AND close_date = '" + date + "' AND name = '" + anlysorName + "'");

		return rs.getBigDecimal(1);

	}

	public void setMarketData(String name, BigDecimal value, String date) throws SQLException {
		this.sqlConnection.execute("write", "INSERT INTO market_data (data_name, data_value, value_date) " + "VALUES ('" + name
				+ "', '" + value + "', '" + date + "')");
	}

	public String getFredCode(String dataName) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT fred_data_code FROM market_data_list " + "WHERE data_name = '"
				+ dataName + "'");

		return rs.getString(1);
	}





	public void createStrat(String stratName, String fullName, String stratDes) throws SQLException {
		this.sqlConnection.execute("write", "INSERT INTO strategies (strategy_name, instr_type, full_name, strat_des) "
				+ "VALUE ('" + stratName + "', 'index', '" + fullName + "','" + stratDes + "')");
	}

	public void configureStrat(int strat_id, String startDate, BigDecimal base, String direction, String currency)
			throws SQLException {
		this.sqlConnection.execute("write",
				"INSERT INTO strategies_configuration (id_strategy,start_date, base, direction, currency) " + "VALUE ("
						+ strat_id + ",'" + startDate + "','" + base + "', '" + direction + "','" + currency + "')");
	}

	public String selectMinStartDate(ArrayList<Integer> stratIdList) throws SQLException {
		if (stratIdList.size() == 1) {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT MIN(close_date) FROM close WHERE id_strategy = " + stratIdList.get(0));
			return rs.getString(1);

		} else {
			List<String> dates = new ArrayList<String>();
			for (int id : stratIdList) {
				ResultSet rs = sqlConnection.execute("read", "SELECT MIN(close_date) FROM close WHERE id_strategy = " + id);
				dates.add(rs.getString(1));
			}
			Collections.sort(dates);
			return dates.get(dates.size() - 1);
		}
	}

	public void deleteIndex(int id) throws SQLException {
		this.sqlConnection.execute("write", "DELETE FROM strategies WHERE id_strategy = " + id);
	}

	public void deleteIndexHisto(int id) throws SQLException {
		this.sqlConnection.execute("write", "DELETE FROM close WHERE id_strategy = " + id);
	}

	public void deleteIndexConfig(int id) throws SQLException {
		this.sqlConnection.execute("write", "DELETE FROM strategies_configuration WHERE id_strategy = " + id);
	}

	public void deleteIndexAnalysors(int id) throws SQLException {
		this.sqlConnection.execute("write", "DELETE FROM analysors WHERE strat_id = " + id);
	}

	public void deleteIndexUnderlyings(int id) throws SQLException {
		this.sqlConnection.execute("write", "DELETE FROM strategies_underlyings WHERE id_strategy = " + id);
	}

	public String getIndexDirection(int id) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT direction from strategies_configuration "
				+ "WHERE id_strategy = " + id);
		return rs.getString(1);
	}

	public BigDecimal getStratBase(int id) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT base FROM strategies_configuration " + "WHERE id_strategy = "
				+ id);
		try {
			return rs.getBigDecimal(1);
		} catch (Exception e) {
			return new BigDecimal("0");
		}
	}

	public String getIndexStartDate(int id) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT start_date FROM strategies_configuration "
				+ "WHERE id_strategy = " + id);

		return rs.getString(1);
	}

	public String getStratLastDate(int id) throws SQLException {
		this.rs = this.sqlConnection.execute("read", "SELECT MAX(close_date) FROM close	" + "WHERE id_strategy = " + id);
		return this.rs.getString(1);

	}

	public String getStratDirection(int id) throws SQLException {
		this.rs = this.sqlConnection.execute("read", "SELECT direction FROM strategies_configuration" + " WHERE id_strategy = "
				+ id);
		try {
			return this.rs.getString(1);
		} catch (Exception e) {
			return "n/a";
		}

	}

	public String getStratCcy(int id) throws SQLException {
		this.rs = this.sqlConnection.execute("read", "SELECT currency FROM strategies_configuration" + " WHERE id_strategy = "
				+ id);
		try {
			return this.rs.getString(1);
		} catch (Exception e) {
			return "n/a";
		}

	}

	public String getStratDes(int id) throws SQLException {
		this.rs = this.sqlConnection.execute("read", "SELECT strat_des FROM strategies" + " WHERE id_strategy = " + id);
		return this.rs.getString(1);
	}

	public String getStratFullName(int id) throws SQLException {
		this.rs = this.sqlConnection.execute("read", "SELECT full_name FROM strategies" + " WHERE id_strategy = " + id);
		return this.rs.getString(1);
	}

	public Hashtable<String, String> getStratAnalysors(int id, String date) throws SQLException {

		Hashtable<String, String> ht = new Hashtable<>();

		ResultSet rs = this.sqlConnection.execute("read", "SELECT name,value FROM analysors " + "WHERE close_date = '" + date
				+ "' AND strat_id = " + id);

		do {

			ht.put(rs.getString(1), rs.getString(2));
		} while (rs.next());

		return ht;

	}

	public Hashtable<String, String> getStratCompo(int id) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT s.strategy_name, su.underlying_weight  "
				+ "FROM strategies_underlyings su, strategies s " + "WHERE s.id_strategy = su.id_underlying AND su.id_strategy="
				+ id);

		Hashtable<String, String> ht = new Hashtable<>();

		do {
			try {
				ht.put(rs.getString(1), rs.getString(2));
			} catch (Exception e) {
				ht.put("n/a", "n/a");
			}
		} while (rs.next());

		return ht;

	}

	public Hashtable<String, String> getCompoSpots(int id, String date) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT s.strategy_name, c.close_value FROM strategies s "
				+ "LEFT JOIN strategies_underlyings su ON su.id_underlying = s.id_strategy "
				+ "LEFT JOIN close c ON c.id_strategy = su.id_underlying " + "WHERE su.id_strategy = " + id
				+ " and c.close_date = '" + date + "';");

		Hashtable<String, String> ht = new Hashtable<>();

		do {

			ht.put(rs.getString(1), rs.getString(2));
		} while (rs.next());

		return ht;

	}

	public Hashtable<String, String> getCompoDailyReturns(int id, String date) throws SQLException {
		ResultSet rs = this.sqlConnection.execute("read", "SELECT s.strategy_name, c.daily_return FROM strategies s "
				+ "LEFT JOIN strategies_underlyings su ON su.id_underlying = s.id_strategy "
				+ "LEFT JOIN close c ON c.id_strategy = su.id_underlying " + "WHERE su.id_strategy = " + id
				+ " and c.close_date = '" + date + "';");

		Hashtable<String, String> ht = new Hashtable<>();

		do {

			ht.put(rs.getString(1), rs.getString(2));
		} while (rs.next());

		return ht;

	}

	public synchronized void setCorrelation(String strat1, String strat2, BigDecimal value, String date, int period)
			throws SQLException {
		try {
			this.sqlConnection.execute("write", "INSERT INTO correlation (var1, var2, value, value_date, period) " + "VALUES ('"
					+ strat1 + "','" + strat2 + "','" + value + "','" + date + "'," + period + ")");
		} catch (Exception e) {
			logger.severe("SQL Request error");
			logger.severe(e.toString());
		}

	}



}
