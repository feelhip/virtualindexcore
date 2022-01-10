package virtualindex.virtualindexcore.crm;

import java.util.Hashtable;

import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class ClientActions 
{
	public static void create(String lastName, String firstName, String company, String email, String email2, String phoneNumber, String phoneNumber2, String country) throws Exception
	{
		SqlRequests2.createClient(lastName, firstName, company, email, email2, phoneNumber, phoneNumber2, country);
	}
	
	public static void update(int id, String firstName, String lastName, String company, String email, String email2, String phoneNumber, String phoneNumber2, String country) throws Exception
	{
		SqlRequests2.updateClient(id,firstName, lastName, company, email, email2, phoneNumber, phoneNumber2, country);
	}
	
	public static void getTotalBtcDeposit(int id)
	{
		//Optional - build later
	}
	
	public static void getTotalBtcNav(int id)
	{
		
	}
	
	public static Hashtable<String, String> getClientDetails(int id) throws Exception
	{
		return 	SqlRequests2.getClientDetails(id);
	}
	
	
	
}



