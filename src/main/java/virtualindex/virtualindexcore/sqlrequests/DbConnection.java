package virtualindex.virtualindexcore.sqlrequests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.configuration.ConfigSetup;

public class DbConnection {
	// private static String CONNECTION_STRING =
	// "jdbc:mysql://localhost/virtualindex?user=root&password=athena77";
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultset = null;
	final Logger logger = Logger.getLogger(Principal.class.getName());

	public DbConnection() {
		
		openConnection();
	}

	public void finalize() {
		closeConnection();
		logger.info("Close SQL connection: "+this.connection.toString());
	}

	public synchronized ResultSet execute(String type, String query) throws SQLException {

		
		
		try {
			if (type.equals("read")) {
				this.statement = this.connection.createStatement();
				this.resultset = this.statement.executeQuery(query);
				this.resultset.next();
				

			} else {
				this.statement = this.connection.createStatement();
				this.statement.execute(query);

			}

			
			ResultSet rs2;
			rs2 = this.resultset;
			return rs2;
		} 
		catch (Exception e) {
			logger.severe("SQL request failed\n" + e.toString());
			resultset = null;
			return null;
		}
		


	}

	public void storeQueriesBatch(ArrayList<String> queriesList) throws SQLException {
		
		try{
		this.statement = this.connection.createStatement();
		for (String query : queriesList) {
			this.statement.addBatch(query);
		}
		this.statement.executeBatch();
		}
		catch(Exception e)
		{
			logger.severe("SQL request failed\n" + e.toString());
		}


	}

	private void openConnection() {
		try {
			//String connString = "jdbc:mysql://localhost/dev_1?user=root&password=athena77";
			String connString = ConfigSetup.getValue("dbConnectionString");
			
			   Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(connString);
			logger.info("Create SQL connection: "+this.connection.toString());
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void closeConnection() {
		if (this.resultset != null) {
			try {
				this.resultset.close();
			} catch (SQLException e) {
				logger.severe(e.toString());
			}
		}
		if (this.statement != null) {
			try {
				this.statement.close();
			} catch (SQLException e) {
				logger.severe(e.toString());
			}
		}
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				logger.severe(e.toString());
			}
		}
	}

}
