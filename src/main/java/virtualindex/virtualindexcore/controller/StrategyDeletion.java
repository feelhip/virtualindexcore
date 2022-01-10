package virtualindex.virtualindexcore.controller;

import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class StrategyDeletion {
	static final Logger logger = Logger.getLogger(Principal.class.getName());
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void delete (String scope, int id) throws Exception 
	{
		SqlRequests req = new SqlRequests();
		
		if(id<=261)
		{
			logger.warning("This id ("+id+") belongs to a base currency. DELETION FORBIDDEN");
		}
		else
		{
		
		switch (scope)
		{
		case "all":
		{
			req.deleteIndex(id);
			req.deleteIndexHisto(id);
			req.deleteIndexConfig(id);
			req.deleteIndexUnderlyings(id);
			req.deleteIndexAnalysors(id);
			SqlRequests2.deleteIndexCorrelation(id);
			logger.warning("Strategy "+id +" successfully deleted");
			break;
	
		}
		case "histo":
		{
			req.deleteIndexHisto(id);
			req.deleteIndexAnalysors(id);
			logger.warning("Strategy "+id+": historic values (close and analysors) successfully deleted");
			break;
		}
		case "config":
		{
			req.deleteIndexConfig(id);
			logger.warning("Strategy "+id+": configuration values successfully deleted");
			break;
		}
		case "correl":
		{
			SqlRequests2.deleteIndexCorrelation(id);
			logger.warning("Strategy "+id+": correlation data successfully deleted");
			break;
		}
		}
		}

	}

}
