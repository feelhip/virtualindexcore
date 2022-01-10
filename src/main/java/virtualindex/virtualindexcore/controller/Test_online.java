package virtualindex.virtualindexcore.controller;


import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.json.JSONException;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.email.Email;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class Test_online {

	private static final Logger logger =  Logger.getLogger(Principal.class.getName());
	
	public static void launch() throws SQLException, IOException, JSONException 
	{
		SqlRequests req = new SqlRequests();
		req.testSqlReq();
		logger.info("Access to the test controller SUCCESSFUL");
		logger.info("Access to the database SUCCESSFUL");
		
		Email email = new Email();
		email.send("Message Test", "Message Test");
		
		logger.info("Program called externally");
	}

}
