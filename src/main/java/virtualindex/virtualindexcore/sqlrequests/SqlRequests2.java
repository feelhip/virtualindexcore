package virtualindex.virtualindexcore.sqlrequests;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

import com.opencsv.CSVWriter;

public final class SqlRequests2 {

	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	private final static DbConnection2 sqlConnection = new DbConnection2();

	public static enum Exchanges {
		CRYPTSY, COINSE, KRAKEN
	};

	/* ------------------------- */
	/* ---------- SET ---------- */
	/* ------------------------- */

	public static void setCorrelationBatch(ArrayList<String> stratList1,
			ArrayList<String> stratList2, ArrayList<BigDecimal> correlList,
			ArrayList<String> dateList, ArrayList<Integer> periodList)
			throws SQLException {

		ArrayList<String> queriesList = new ArrayList<>();
		try {
			for (int x = 0; x < stratList1.size(); x++) {
				queriesList
						.add("INSERT INTO correlation (var1, var2, value, value_date, period) "
								+ "VALUES ('"
								+ stratList1.get(x)
								+ "','"
								+ stratList2.get(x)
								+ "','"
								+ correlList.get(x)
								+ "','"
								+ dateList.get(x)
								+ "',"
								+ periodList.get(x) + ")");
			}
			sqlConnection.storeQueriesBatch(queriesList);
		} catch (Exception e) {
			logger.severe(e.toString());
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void updateStratConfig(int id, String description, String fullName)
	{

		try {
			sqlConnection.execute("write",
					"UPDATE strategies SET "
							+ "full_name ='" + fullName + "', strat_des='" + description + "' WHERE id_strategy = " + id);
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void createClient(String lastName, String firstName, String company, String email, String email2, String phoneNumber, String phoneNumber2, String country) throws Exception
	{

		String[] columnNames = { "company", "email", "email2", "phone_number", "phone_number2", "country" };
		String[] columnContents = { company, email, email2, phoneNumber, phoneNumber2, country };

		if (lastName == null || firstName == null)
		{
			throw new Exception("First Name or Last Name is NULL");
		}

		try {

			sqlConnection.execute("write",
					"INSERT INTO clients (last_name, first_name, creation_date, creation_time) "
							+ "VALUES ('" + lastName + "','" + firstName + "','" + DateProcessing.todaySqlDate() + "','" + DateProcessing.todaySqlTime() + "')");

			int id = sqlConnection.execute("read",
					"SELECT MAX(id) FROM clients").getInt(1);

			for (int x = 0; x < columnNames.length; x++)
			{
				sqlConnection.execute("write", "UPDATE clients SET " + columnNames[x] + " = '" + columnContents[x] + "' WHERE id = " + id);
			}

		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}

	}

	public static void updateClient(int id, String firstName, String lastName, String company, String email, String email2, String phoneNumber, String phoneNumber2, String country) throws Exception
	{

		String[] columnNames = { "first_name", "last_name", "company", "email", "email2", "phone_number", "phone_number2", "country" };
		String[] columnContents = { firstName, lastName, company, email, email2, phoneNumber, phoneNumber2, country };

		try {
			for (int x = 0; x < columnNames.length; x++)
			{
				sqlConnection.execute("write", "UPDATE clients SET " + columnNames[x] + " = '" + columnContents[x] + "' WHERE id = " + id);
			}

		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}

	}

	public static void createStrat(String stratName, String fullName, String stratDes) throws Exception {

		try {
			sqlConnection.execute("write", "INSERT INTO strategies (strategy_name, instr_type, full_name, strat_des) "
					+ "VALUE ('" + stratName + "', 'index', '" + fullName + "','" + stratDes + "')");
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void configureStrat(int strat_id, String startDate, BigDecimal base, String direction, String currency)
			throws Exception {

		try {
			sqlConnection.execute("write",
					"INSERT INTO strategies_configuration (id_strategy,start_date, base, direction, currency) " + "VALUE ("
							+ strat_id + ",'" + startDate + "','" + base + "', '" + direction + "','" + currency + "')");
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void addUnderlying(int stratId, int underlyingId, BigDecimal underlyingWeight)
	{

		try {

			sqlConnection.execute("write", "INSERT INTO strategies_underlyings "
					+ "(id_strategy, id_underlying, underlying_weight) "
					+ "VALUES ('" + stratId + "','" + underlyingId + "','" + underlyingWeight + "')");

		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static String selectMinStartDate(ArrayList<Integer> stratIdList) throws SQLException {

		try {
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
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<ArrayList<String>> showAllClients() throws Exception
	{

		ArrayList<ArrayList<String>> clients = new ArrayList<ArrayList<String>>();
		try {
			ResultSet selection = sqlConnection.execute("read", "SELECT id,last_name, first_name,company, email, email2, phone_number, phone_number2, country, creation_date, creation_time  FROM clients");

			do {
				ArrayList<String> client = new ArrayList<String>();
				for (int x = 1; x <= 11; x++)
				{
					client.add(selection.getString(x));
				}

				clients.add(client);
			} while (selection.next());

			return clients;
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void createNewDeal(int investorId, int stratId) {

		try {
			sqlConnection.execute("write",
					"INSERT INTO deals (strat_id, investor_id, strike_date, strike_time) VALUES ('"
							+ stratId + "','" + investorId + "','"
							+ DateProcessing.todaySqlDate() + "','"
							+ DateProcessing.todaySqlTime() + "')");

		} catch (Exception e) {
			logger.severe(e.toString());
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void storeNewOrder(int dealId, int stratId,
			BigDecimal grossBtcAmt, String direction, BigDecimal fees) {

		try {
			sqlConnection
					.execute(
							"write",
							"INSERT INTO orders_histo (deal_id,strat_id,gross_btc_amt,direction,date,time,fees) "
									+ "VALUES ('"
									+ dealId
									+ "','"
									+ stratId
									+ "','"
									+ grossBtcAmt
									+ "','"
									+ direction
									+ "','"
									+ DateProcessing.todaySqlDate()
									+ "', '"
									+ DateProcessing.todaySqlTime()
									+ "', '" + fees + "')");
		} catch (Exception e) {
			logger.severe(e.toString());
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void recordCurrencyTrade(int orderId, Integer udlId,
			BigDecimal udlAmt, BigDecimal btcAmt, BigDecimal brokerFees, String direction)
			throws SQLException {

		try {
			sqlConnection
					.execute(
							"write",
							"INSERT INTO ccy_trades_histo (order_id, udl_id, udl_amt, btc_amt, broker_fees,direction, trade_date, trade_time) VALUES "
									+ "('"
									+ orderId
									+ "','"
									+ udlId
									+ "','"
									+ udlAmt
									+ "','"
									+ btcAmt
									+ "','"
									+ brokerFees
									+ "','"
									+ direction
									+ "','"
									+ DateProcessing.todaySqlDate()
									+ "','"
									+ DateProcessing.todaySqlTime() + "')");
		} catch (Exception e) {
			logger.severe(e.toString());
		} finally {
			sqlConnection.closeConnection();
		}

	}

	/* --------------------------- */
	/* ---------- COUNT ---------- */
	/* --------------------------- */

	public static int countCorrelationItems(String date) throws SQLException {

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT COUNT(value_date) FROM correlation WHERE value_date = '"
							+ date + "'");

			return rs.getInt(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int countAnalysors(String date) throws SQLException {

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT COUNT(value) FROM analysors WHERE close_date = '"
							+ date + "'");

			return rs.getInt(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int countAnalysors(String date, int stratId) throws SQLException {

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT COUNT(value) FROM analysors "
							+ "WHERE close_date = '" + date
							+ "' AND strat_id = " + stratId);

			return rs.getInt(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int countClientDeals(int clientId) throws Exception {

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT COUNT(deal_id) FROM deals WHERE investor_id = " + clientId);

			return rs.getInt(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	/* ------------------------- */
	/* ---------- GET ---------- */
	/* ------------------------- */

	public static HashMap<String, BigDecimal> getCorrelTable(String stratName, String date, int period) throws Exception
	{
		HashMap<String, BigDecimal> correlTable = new HashMap<String, BigDecimal>();

		try
		{
			ResultSet rs = sqlConnection.execute("read", "SELECT var2, value "
					+ "FROM correlation "
					+ "WHERE var1 = '" + stratName + "' "
					+ "AND value_date = '" + date + "' "
					+ "AND period = '" + period + "'");

			do {
				try {
					correlTable.put(rs.getString(1), rs.getBigDecimal(2));
				} catch (Exception e) {
					logger.warning("No data for the selected date");
				}
			} while (rs.next());

			return correlTable;
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		}

		finally {
			sqlConnection.closeConnection();
		}
	}

	public static BigDecimal getCorrel(String stratName1, String stratName2, String date, int period) throws SQLException
	{

		try
		{
			ResultSet rs = sqlConnection.execute("read", "SELECT value "
					+ "FROM correlation "
					+ "WHERE var1 = '" + stratName1 + "' "
					+ "AND var2 = '" + stratName2 + "' "
					+ "AND value_date = '" + date + "' "
					+ "AND period = '" + period + "'");

			return rs.getBigDecimal(1);

		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		}
	}

	public static String getLastCorrelDate(String stratName) throws SQLException
	{

		try
		{
			ResultSet selection = sqlConnection.execute("read",
					"SELECT MAX(value_date) FROM correlation WHERE var1 = '" + stratName + "'");
			return selection.getString(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		}

	}

	public static ArrayList<String> getClientStratsList(int id) throws SQLException
	{
		ArrayList<String> stratsList = new ArrayList<String>();

		try {
			ResultSet rs = sqlConnection.execute("read", "SELECT s.strategy_name FROM strategies s INNER JOIN deals d ON d.strat_id = s.id_strategy WHERE d.investor_id = " + id + ";");

			do {
				stratsList.add(rs.getString(1));
			} while (rs.next());
			return stratsList;
		} catch (SQLException e) {
			logger.warning("Client #" + id + " doesn't own any strategy. " + e.toString());
			throw e;
		}

		finally {
			sqlConnection.closeConnection();
		}
	}

	public static int getLastOrderId() throws SQLException {

		try {
			ResultSet selection = sqlConnection.execute("read",
					"SELECT MAX(order_id) FROM orders_histo");
			return selection.getInt(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int getLastDealId() throws SQLException {

		try {
			ResultSet selection = sqlConnection.execute("read",
					"SELECT MAX(deal_id) FROM deals");
			return selection.getInt(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<String> getAllStratNames() throws Exception {

		ArrayList<String> stratNamesList = new ArrayList<String>();
		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT strategy_name FROM strategies");
			do {
				stratNamesList.add(rs.getString(1));
			} while (rs.next());

			return stratNamesList;
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw new Exception();

		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<Integer> getAllCcyIds() {

		ArrayList<Integer> stratIdsList = new ArrayList<Integer>();
		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT id_strategy FROM strategies "
							+ "WHERE instr_type = 'ccy'");

			do {
				stratIdsList.add(rs.getInt(1));
			} while (rs.next());
			return stratIdsList;
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		}

		finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<Integer> getStratIdFromClose(String closeSrc,
			String date) {
		ArrayList<Integer> ccyList = new ArrayList<Integer>();

		try {
			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT id_strategy FROM close"
									+ " WHERE close_date = '"
									+ date
									+ "' AND close_source != 'pricer' ORDER BY id_strategy ASC");
			do {
				ccyList.add(rs.getInt(1));

			} while (rs.next());

			return ccyList;
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<Integer> getAllStratsId(String exchangeName) {
		ArrayList<Integer> stratsIdList = new ArrayList<Integer>();

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT id_strategy FROM strategies "
							+ "WHERE instr_type = 'ccy' and default_source = '"
							+ exchangeName + "' ORDER BY id_strategy ASC");

			do {
				stratsIdList.add(rs.getInt(1));
			} while (rs.next());

			return stratsIdList;
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static BigDecimal getStratClose(int stratId, String date) throws Exception { // NEED
		// TO BE CLEANED ON THE ORIGINAL CLASS

		try {
			ResultSet rs = sqlConnection
					.execute("read",
							"SELECT close_value FROM close WHERE id_strategy = "
									+ stratId + " " + "AND close_date = '"
									+ date + "'");
			return rs.getBigDecimal(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw new Exception("Pricing doesn't exist in the database");
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static String getStratName(int stratId) { // NEED TO BE CLEANED ON
														// THE ORIGINAL CLASS

		try {
			ResultSet request = sqlConnection.execute("read",
					"SELECT strategy_name FROM strategies "
							+ "WHERE id_strategy ='" + stratId + "'");
			return (request.getString(1));
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static String getStratType(int stratId) {

		try {
			ResultSet request = sqlConnection.execute("read",
					"SELECT instr_type FROM strategies "
							+ "WHERE id_strategy ='" + stratId + "'");
			return (request.getString(1));
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static String getStratDes(int stratId) throws Exception {

		try {
			ResultSet request = sqlConnection.execute("read",
					"SELECT strat_des FROM strategies "
							+ "WHERE id_strategy ='" + stratId + "'");
			return (request.getString(1));
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static BigDecimal getReturn(int stratId, String sqlDate) { // NEED TO
																		// BE
																		// CLEANED
																		// ON
																		// THE
																		// ORIGINAL
																		// CLASS
		BigDecimal returnValue;

		try {
			ResultSet returnsSet = sqlConnection.execute("read",
					"SELECT daily_return FROM close WHERE id_strategy = "
							+ stratId + " AND close_date = '" + sqlDate + "'");

			returnValue = (returnsSet.getBigDecimal(1));
			return returnValue;
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int getStratId(String stratName) throws Exception {

		try {
			ResultSet request = sqlConnection.execute("read",
					"SELECT id_strategy FROM strategies "
							+ "WHERE strategy_name ='" + stratName + "'");

			return (request.getInt(1));
		} catch (SQLException e) {
			logger.severe(e.toString());
			throw new Exception("Strategy name doesn't exist in the database");
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<String> getMarketDataList() {

		try {
			ArrayList<String> marketDataList = new ArrayList<String>();
			ResultSet rs = sqlConnection.execute("read",
					"SELECT data_name FROM market_data_list");
			do {
				marketDataList.add(rs.getString(1));
			} while (rs.next());

			return marketDataList;
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<String> getDateMarketDataList(String date) {
		ArrayList<String> marketDataList = new ArrayList<String>();

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT data_name FROM market_data"
							+ " WHERE value_date ='" + date + "'");
			do {
				marketDataList.add(rs.getString(1));
			} while (rs.next());

			return marketDataList;
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static BigDecimal getMarketData(String dataName, String date) {

		String query = "SELECT data_value FROM market_data "
				+ "WHERE data_name = '" + dataName + "'  AND value_date = '"
				+ date + "'";
		try {
			ResultSet rs = sqlConnection.execute("read", query);
			return rs.getBigDecimal(1);
		} catch (SQLException e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<Integer> getAllStratsId() {
		ArrayList<Integer> stratsIdList = new ArrayList<Integer>();

		try {
			ResultSet rs = sqlConnection.execute("read",
					"SELECT id_strategy FROM strategies");

			do {
				stratsIdList.add(rs.getInt(1));
			} while (rs.next());

			return stratsIdList;
		} catch (SQLException e) {
			logger.severe(Thread.currentThread().getStackTrace().toString());
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, String> getStratCompo(int id)
			throws Exception {

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT s.strategy_name, su.underlying_weight  "
									+ "FROM strategies_underlyings su, strategies s "
									+ "WHERE s.id_strategy = su.id_underlying AND su.id_strategy="
									+ id + " ORDER BY s.strategy_name ASC");

			Hashtable<String, String> ht = new Hashtable<>();

			do {
				try {
					ht.put(rs.getString(1), rs.getString(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return ht;
		} catch (Exception e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static String getStratLastDate(int id) throws Exception {

		try {
			ResultSet rs = sqlConnection.execute("read", "SELECT MAX(close_date) FROM close	" + "WHERE id_strategy = " + id);
			return rs.getString(1);
		} catch (Exception e)
		{
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int getStratIdFromDealId(int dealId) throws Exception {

		try {
			ResultSet selection = sqlConnection.execute("read",
					"SELECT strat_id FROM deals WHERE deal_id = '" + dealId
							+ "'");
			return selection.getInt(1);
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int getInvestorIdFromDealId(int dealId) throws Exception {

		try {
			ResultSet selection = sqlConnection.execute("read",
					"SELECT investor_id FROM deals WHERE deal_id = '" + dealId
							+ "'");
			return selection.getInt(1);
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<BigDecimal> getHedgedAmountList(int dealId) throws Exception {

		int stratId = getStratIdFromDealId(dealId);
		ArrayList<Integer> compoIdsList = SqlRequests2
				.getComposition_ids(SqlRequests2.getComposition(stratId));
		ArrayList<BigDecimal> hedgedAmtList = new ArrayList<BigDecimal>();

		try {
			for (int udlId : compoIdsList) {

				String purchaseReq = "SELECT SUM(cth.udl_amt) FROM ccy_trades_histo cth INNER JOIN orders_histo dh ON cth.order_id = dh.order_id WHERE dh.deal_id = '"
						+ dealId
						+ "' AND cth.udl_id = "
						+ udlId
						+ " AND cth.direction = 'purchase';";
				String unwindReq = "SELECT SUM(cth.udl_amt) FROM ccy_trades_histo cth INNER JOIN orders_histo dh ON cth.order_id = dh.order_id WHERE dh.deal_id = '"
						+ dealId
						+ "' AND cth.udl_id = "
						+ udlId
						+ " AND cth.direction = 'unwind';";

				ResultSet sumPurchased = sqlConnection.execute("read",
						purchaseReq);
				BigDecimal purchased = sumPurchased.getBigDecimal(1);

				ResultSet sumSold = sqlConnection.execute("read", unwindReq);

				BigDecimal sold = new BigDecimal("0");
				try {

					if (sumSold.getBigDecimal(1) != null) {
						sold = sumSold.getBigDecimal(1);
					}
				} catch (Exception e) {
					logger.info("No amount has been unwinded for now");
					System.out.println("No amount unwinded for now");
					logger.info(e.toString());
				}

				BigDecimal total = purchased.subtract(sold);
				total = total.setScale(12, RoundingMode.HALF_EVEN);

				hedgedAmtList.add(total);
			}
			return hedgedAmtList;
		} catch (Exception e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}

	}

	public static int getBasketSize(int stratId) throws Exception {

		String request = "SELECT COUNT(su.id_underlying) "
				+ "FROM strategies_underlyings su "
				+ "WHERE su.id_strategy = "
				+ stratId
				+ " ;";
		try {
			ResultSet selection = sqlConnection.execute("read", request);
			return selection.getInt(1);
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<String> getAllAnalysorsTypes() throws Exception
	{

		ArrayList<String> analysorsList = new ArrayList<String>();

		String request = "SELECT analysor_name FROM analysors_types ORDER BY analysor_name ASC;";
		try {
			ResultSet selection = sqlConnection.execute("read", request);
			do {
				analysorsList.add(selection.getString(1));

			} while (selection.next());
			return analysorsList;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<ArrayList<Object>> getComposition(int id) {

		ArrayList<ArrayList<Object>> composition = new ArrayList<>();
		ArrayList<Object> idsList = new ArrayList<>();
		ArrayList<Object> namesList = new ArrayList<>();
		ArrayList<Object> weightsList = new ArrayList<>();

		String request = "SELECT su.id_underlying, s.strategy_name, su.underlying_weight "
				+ "FROM strategies_underlyings su "
				+ "INNER JOIN strategies s "
				+ "ON s.id_strategy = su.id_underlying "
				+ "WHERE su.id_strategy = "
				+ id
				+ " ORDER BY id_underlying ASC;";
		try {
			ResultSet selection = sqlConnection.execute("read", request);
			do {
				idsList.add(selection.getInt(1));
				namesList.add(selection.getString(2));
				weightsList.add(selection.getBigDecimal(3));
			} while (selection.next());

			if (idsList.size() != namesList.size()
					|| idsList.size() != weightsList.size()
					|| namesList.size() != weightsList.size()) {
				logger.severe("Inconsitency in the composition list size");
				throw new Exception();
			}

			composition.add(idsList);
			composition.add(namesList);
			composition.add(weightsList);

			return composition;
		}

		catch (Exception e) {
			logger.severe(Thread.currentThread().getStackTrace().toString());
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<Integer> getComposition_ids(
			ArrayList<ArrayList<Object>> composition) {
		ArrayList<Object> idsList = composition.get(0);
		ArrayList<Integer> idListInt = new ArrayList<Integer>();
		for (Object id : idsList) {
			idListInt.add((Integer) id);
		}
		return idListInt;
	}

	public static ArrayList<String> getComposition_names(
			ArrayList<ArrayList<Object>> composition) {
		ArrayList<Object> namesList = composition.get(1);
		ArrayList<String> nameListString = new ArrayList<String>();
		for (Object id : namesList) {
			nameListString.add((String) id);
		}
		return nameListString;
	}

	public static ArrayList<BigDecimal> getComposition_weights(
			ArrayList<ArrayList<Object>> composition) {
		ArrayList<Object> idList = composition.get(2);
		ArrayList<BigDecimal> weightsListBD = new ArrayList<BigDecimal>();
		for (Object id : idList) {
			weightsListBD.add((BigDecimal) id);
		}
		return weightsListBD;
	}

	public static ArrayList<String> getLikeIndicesNames(String stratName) {

		ArrayList<String> resultList = new ArrayList<String>();
		String request = "SELECT strategy_name FROM strategies WHERE instr_type = 'index' AND strategy_name LIKE '%"
				+ stratName + "%';";
		try {
			ResultSet selection = sqlConnection.execute("read", request);
			do {
				resultList.add(selection.getString(1));
			} while (selection.next());
			return resultList;
		} catch (Exception e) {
			logger.warning(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<String> getIndicesNames(String stratName) {

		ArrayList<String> resultList = new ArrayList<String>();
		String request = "SELECT strategy_name FROM strategies WHERE instr_type = 'index' AND strategy_name = '"
				+ stratName + "';";
		try {
			ResultSet selection = sqlConnection.execute("read", request);
			do {
				resultList.add(selection.getString(1));
			} while (selection.next());
			return resultList;
		} catch (Exception e) {
			logger.warning(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, BigDecimal> getPositions()
	{

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT s.strategy_name, SUM(udl_amt) "
									+ "FROM ccy_trades_histo p "
									+ "INNER JOIN strategies s "
									+ "ON s.id_strategy=p.udl_id "
									+ "GROUP BY udl_id "
									+ "ORDER BY udl_id ASC;");

			Hashtable<String, BigDecimal> ht = new Hashtable<>();

			do {
				try {
					ht.put(rs.getString(1), rs.getBigDecimal(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return ht;
		} catch (Exception e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, BigDecimal> getPositions(int id)
	{

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT s.strategy_name, SUM(udl_amt) "
									+ "FROM ccy_trades_histo p "
									+ "INNER JOIN strategies s "
									+ "ON s.id_strategy=p.udl_id "
									+ "WHERE s.id_strategy ='" + id + "' "
									+ "GROUP BY udl_id "
									+ "ORDER BY udl_id ASC;");

			Hashtable<String, BigDecimal> ht = new Hashtable<>();

			do {
				try {
					ht.put(rs.getString(1), rs.getBigDecimal(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return ht;
		} catch (Exception e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static BigDecimal getStratBase(int id) throws SQLException {

		try {
			ResultSet rs = sqlConnection.execute("read", "SELECT base FROM strategies_configuration " + "WHERE id_strategy = "
					+ id);
			return rs.getBigDecimal(1);
		} catch (Exception e)
		{
			logger.severe(e.toString());
			throw e;
		} finally
		{
			sqlConnection.closeConnection();
		}
	}

	public static String getIndexStartDate(int id) throws Exception {

		try {
			ResultSet rs = sqlConnection.execute("read", "SELECT start_date FROM strategies_configuration "
					+ "WHERE id_strategy = " + id);

			return rs.getString(1);
		} catch (Exception e)
		{
			logger.severe(e.toString());
			throw e;
		} finally
		{
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, String> getStrategyDetails(int id) throws Exception
	{

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT * FROM strategies s "
									+ "INNER JOIN strategies_configuration c "
									+ "ON s.id_strategy = c.id_strategy "
									+ "WHERE s.id_strategy = " + id);

			Hashtable<String, String> ht = new Hashtable<>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int x = 1; x <= rsmd.getColumnCount(); x++)
			{
				try {
					ht.put(rsmd.getColumnName(x), rs.getString(x));
				} catch (Exception e)
				{
					ht.put(rsmd.getColumnName(x), "null");
				}
			}

			return ht;
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;

		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, String> getClientDetails(int id) throws Exception
	{

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT * from clients WHERE id =" + id);

			Hashtable<String, String> ht = new Hashtable<>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int x = 1; x <= rsmd.getColumnCount(); x++)
			{
				ht.put(rsmd.getColumnName(x), rs.getString(x));
			}

			return ht;
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;

		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<Hashtable<String, String>> getClientDealsDetails(int id) throws Exception
	{
		ArrayList<Hashtable<String, String>> dealsDetails = new ArrayList<Hashtable<String, String>>();

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT d.strike_date, d.strike_time, oh.deal_id, s.strategy_name ,SUM(oh.gross_btc_amt) AS 'total_gross_btc_amt' "
									+ "FROM orders_histo oh "
									+ "INNER JOIN deals d "
									+ "ON d.deal_id = oh.deal_id "
									+ "INNER JOIN strategies s "
									+ "ON s.id_strategy=d.strat_id "
									+ "WHERE d.investor_id =" + id + " "
									+ "GROUP BY oh.deal_id;");
			do {
				Hashtable<String, String> dealDetails = new Hashtable<>();
				ResultSetMetaData rsmd = rs.getMetaData();

				for (int x = 1; x <= rsmd.getColumnCount(); x++)
				{
					dealDetails.put(rsmd.getColumnName(x), rs.getString(x));
				}
				dealsDetails.add(dealDetails);
			} while (rs.next());

			return dealsDetails;
		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;

		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static int getLastClientId() throws Exception {

		try {
			ResultSet selection = sqlConnection.execute("read",
					"SELECT MAX(id) FROM clients;");
			return selection.getInt(1);
		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, BigDecimal> getLastCloseAnalysors(int id)
	{

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT aa.name, aa.value "
									+ "FROM analysors aa "
									+ "JOIN (SELECT MAX(a.close_date) "
									+ "AS max_date "
									+ "FROM analysors a) m  "
									+ "ON m.max_date = aa.close_date "
									+ "WHERE aa.strat_id = " + id);

			Hashtable<String, BigDecimal> ht = new Hashtable<>();

			do {
				try {
					ht.put(rs.getString(1), rs.getBigDecimal(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return ht;
		} catch (Exception e) {
			logger.severe(e.toString());
			return null;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Map<Long, BigDecimal> getDailYReturnTimeSeries(int id) throws Exception
	{
		Map<Long, BigDecimal> dataMap = new TreeMap<Long, BigDecimal>();

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT close_date,daily_return FROM close WHERE id_strategy = " + id + " ORDER BY close_date DESC");
			do {
				try {
					dataMap.put(DateProcessing.stringToEpoch(rs.getString(1)), rs.getBigDecimal(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return dataMap;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Map<Long, BigDecimal> getAnalysorTimeSeries(int id, String analysorName) throws Exception
	{
		Map<Long, BigDecimal> dataMap = new TreeMap<Long, BigDecimal>();

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT close_date,value FROM analysors WHERE strat_id = " + id + " AND name = '" + analysorName + "' ORDER BY close_date DESC");
			do {
				try {
					dataMap.put(DateProcessing.stringToEpoch(rs.getString(1)), rs.getBigDecimal(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return dataMap;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Map<Long, BigDecimal> getCorrelationTimeSeries(String var1, String var2, int period) throws Exception
	{
		Map<Long, BigDecimal> dataMap = new TreeMap<Long, BigDecimal>();

		try {

			ResultSet rs = sqlConnection
					.execute(
							"read",
							"SELECT value_date,value FROM correlation WHERE var1 = '" + var1 + "' AND var2 = '" + var2 + "' AND period = '" + period + "' ORDER BY value_date DESC");
			do {
				try {
					dataMap.put(DateProcessing.stringToEpoch(rs.getString(1)), rs.getBigDecimal(2));
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());

			return dataMap;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static void setDailyReturn(BigDecimal analysorValue, int stratId, String date) throws Exception {

		try {
			sqlConnection.execute("write", "UPDATE close SET daily_return = " + analysorValue + " " + "WHERE id_strategy = "
					+ stratId + " AND close_date = '" + date + "'");
		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	// Export Functions

	public static void exportClose(int id, String startDate, String endDate, String filePath, String fileName) throws Exception {

		CSVWriter writer = new CSVWriter(new FileWriter(filePath + fileName), ',');
		try {
			ResultSet rs = sqlConnection.execute("read", "SELECT close_date, close_value "
					+ "FROM close "
					+ "WHERE id_strategy = '" + id + "' "
					+ "AND close_date >= '" + startDate + "' "
					+ "AND close_date <= '" + endDate + "'");

			String[] headers = "Date#Close".split("#");
			writer.writeNext(headers);

			do {
				try {
					String entry = rs.getString(1) + "#" + rs.getString(2);
					String[] entries = entry.split("#");
					writer.writeNext(entries);
				} catch (Exception e) {
					throw new Exception();
				}
			} while (rs.next());
			writer.close();
			/*
			 * writer.writeAll(rs,true);
			 */

		} catch (Exception e) {
			logger.severe(e.toString());
			throw e;
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static String getCcyDefaultSoure(String ccy) throws Exception
	{

		try {
			ResultSet selection = sqlConnection.execute("read", "SELECT default_source FROM strategies"
					+ " WHERE strategy_name ='" + ccy + "'");

			return selection.getString(1);
		} catch (Exception e)
		{
			logger.severe(e.toString());
			throw e;
		} finally
		{
			sqlConnection.closeConnection();
		}
	}

	public static ArrayList<ArrayList<Object>> getOrderedCloses(String orderedData, String orderDir, String date) throws Exception
	{

		try {
			String query = null;
			ArrayList<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();
			ArrayList<Object> idList = new ArrayList<Object>();
			ArrayList<Object> nameList = new ArrayList<Object>();
			ArrayList<Object> valueList = new ArrayList<Object>();

			query = "SELECT c.id_strategy id, s.strategy_name name, c.close_value value FROM close c "
					+ "INNER JOIN strategies s ON s.id_strategy = c.id_strategy "
					+ "WHERE c.close_date = '" + date + "' ORDER BY " + orderedData + " " + orderDir;

			ResultSet rs = sqlConnection.execute("read", query);
			do {
				idList.add(rs.getInt(1));
				nameList.add(rs.getString(2));
				valueList.add(rs.getBigDecimal(3));
			} while (rs.next());
			dataList.add(idList);
			dataList.add(nameList);
			dataList.add(valueList);
			return dataList;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}

	}

	public static ArrayList<ArrayList<Object>> getOrderedDailyReturns(String orderedData, String orderDir, String date) throws Exception
	{

		try {
			String query = null;
			ArrayList<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();
			ArrayList<Object> idList = new ArrayList<Object>();
			ArrayList<Object> nameList = new ArrayList<Object>();
			ArrayList<Object> valueList = new ArrayList<Object>();

			query = "SELECT c.id_strategy id, s.strategy_name name, c.daily_return value FROM close c "
					+ "INNER JOIN strategies s ON s.id_strategy = c.id_strategy "
					+ "WHERE c.close_date = '" + date + "' ORDER BY " + orderedData + " " + orderDir;

			ResultSet rs = sqlConnection.execute("read", query);
			do {
				idList.add(rs.getInt(1));
				nameList.add(rs.getString(2));
				valueList.add(rs.getBigDecimal(3));
			} while (rs.next());
			dataList.add(idList);
			dataList.add(nameList);
			dataList.add(valueList);
			return dataList;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}

	}

	public static ArrayList<ArrayList<Object>> getOrderedAnalysors(String analysorName, String orderedData, String orderDir, String date) throws Exception
	{
		try {
			ArrayList<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();

			String query = "SELECT  a.strat_id id, s.strategy_name name, a.value value "
					+ "FROM analysors a "
					+ "INNER JOIN strategies s "
					+ "ON s.id_strategy = a.strat_id "
					+ "WHERE a.close_date = '" + date + "' "
					+ "AND a.name = '" + analysorName + "' "
					+ "ORDER BY " + orderedData + " " + orderDir + ";";

			ArrayList<Object> idList = new ArrayList<Object>();
			ArrayList<Object> namesList = new ArrayList<Object>();
			ArrayList<Object> valueList = new ArrayList<Object>();

			ResultSet rs = sqlConnection.execute("read", query);
			do {
				idList.add(rs.getInt(1));
				namesList.add(rs.getString(2));
				valueList.add(rs.getBigDecimal(3));

			} while (rs.next());
			dataList.add(idList);
			dataList.add(namesList);
			dataList.add(valueList);
			return dataList;

		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

	public static Hashtable<String, String> getExchangeCodes(String stratName, Exchanges exchange) throws Exception
	{
		String query;
		switch (exchange)
		{
		case CRYPTSY: {
			query = "SELECT cryptsy_name, cryptsy_code FROM strategies WHERE strategy_name ='" + stratName + "'";
			break;
		}
		case COINSE: {
			query = "SELECT coinse_name, coinse_code FROM strategies WHERE strategy_name ='" + stratName + "'";
			break;
		}
		case KRAKEN: {
			query = "SELECT kraken_name, kraken_code FROM strategies WHERE strategy_name ='" + stratName + "'";
			break;
		}
		default: {
			throw new Exception("SEVERE ERROR - Case not taken into account into the Enumeration");
		}
		}

		Hashtable<String, String> codesTable = new Hashtable<String, String>();
		try
		{
			ResultSet rs = sqlConnection.execute("read", query);
			codesTable.put("name", rs.getString(1));
			codesTable.put("code", rs.getString(2));

			return codesTable;

		} catch (Exception e)
		{
			logger.severe(e.toString());
			throw e;
		} finally
		{
			sqlConnection.closeConnection();
		}

	}

	// DELETION

	public static void deleteIndexCorrelation(int id) throws Exception {

		String stratName = SqlRequests2.getStratName(id);
		try {
			sqlConnection.execute("write", "DELETE FROM correlation WHERE var1 = '" + stratName + "' OR var2 = '" + stratName + "'");
		} catch (Exception e) {
			logger.severe(e.toString());
			throw new Exception(e);
		} finally {
			sqlConnection.closeConnection();
		}
	}

}
