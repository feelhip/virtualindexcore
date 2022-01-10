package virtualindex.virtualindexcore.filehandler.csv;

import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class CsvWriter {

	public static void exportClose(int id, String startDate, String endDate, String filePath, String fileName) throws Exception
	{
		SqlRequests2.exportClose( id,  startDate,  endDate,  filePath,  fileName);
		
	}
	
	
}
