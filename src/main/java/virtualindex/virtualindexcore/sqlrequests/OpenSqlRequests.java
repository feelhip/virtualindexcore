package virtualindex.virtualindexcore.sqlrequests;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OpenSqlRequests
{
	private OpenDbConnection openSqlConnection;

	public OpenSqlRequests()
	{
		this.openSqlConnection = new OpenDbConnection();
	}

	public void closeConnection()
	{
		this.openSqlConnection.closeConnection();
	}

	public synchronized ArrayList<BigDecimal> getReturnsInArray_OPEN(String stratName, String sqlDate) throws SQLException {
		ArrayList<BigDecimal> returnsArray = new ArrayList<BigDecimal>();

		ResultSet returnsSet = this.openSqlConnection
				.execute(
						"read",
						"SELECT daily_return FROM close INNER JOIN strategies ON close.id_strategy = strategies.id_strategy WHERE strategies.strategy_name = '"
								+ stratName + "' AND close_date > '" + sqlDate + "' ORDER BY close_date ASC");

		do {
			returnsArray.add(returnsSet.getBigDecimal(1));
		} while (returnsSet.next());
		return returnsArray;
	}
}
