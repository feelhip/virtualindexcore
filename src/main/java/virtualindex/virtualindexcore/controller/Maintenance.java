package virtualindex.virtualindexcore.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import virtualindex.virtualindexcore.dbmaintenance.MaintenanceTask;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class Maintenance 
{
	public static void main(String[] args) throws SQLException, ParseException
	{
	//List of currencies on which to perform maintenance task
	ArrayList<String> ccyList = new ArrayList<String>();	
	SqlRequests req = new SqlRequests();
	ccyList = req.getAllCcyNames();
	
	MaintenanceTask maintenanceTask = new MaintenanceTask();
	
	//maintenanceTask.checkDatesIntegrity(ccyList);
	//maintenanceTask.countCloseMissing(ccyList);
	//maintenanceTask.fixingsManagementLoopMarketData("180d_TBill");
	//maintenanceTask.fixingsManagementLoop(ccyList);
	maintenanceTask.checkSpotsIntegrity(ccyList);
	//maintenanceTask.computeReturns(ccyList);
	
	
	}
	
}
