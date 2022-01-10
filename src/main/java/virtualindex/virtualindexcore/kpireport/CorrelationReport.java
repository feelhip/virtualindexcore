package virtualindex.virtualindexcore.kpireport;



import java.sql.SQLException;

import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;



public final class CorrelationReport {
	


	
	public static String generate(String date) throws SQLException 
	{
		 String separator = "----------------------------------------------------------";
		 String newLine = System.getProperty("line.separator");
		 String smallSeparator = "---";
		
		int todayIndicator = SqlRequests2.countCorrelationItems(date);
		int yesterdayIndicator = SqlRequests2.countCorrelationItems(DateProcessing.offsetSqlDate(-1, date));
		String status = "OK";
		
		if (todayIndicator < yesterdayIndicator)
		{
			status = "KO";
		}
		
		
		
		return separator+newLine+smallSeparator+" CORRELATION "+smallSeparator+"> "+status+ newLine 
				+"Today: "+todayIndicator+newLine+"Yesterday: "+yesterdayIndicator+newLine + separator+newLine; 
	}

	
	
}
