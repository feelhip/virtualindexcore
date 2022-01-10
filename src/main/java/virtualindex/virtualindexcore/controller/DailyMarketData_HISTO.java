package virtualindex.virtualindexcore.controller;

import virtualindex.virtualindexcore.ccyhistospot.USTBills_rates;

public class DailyMarketData_HISTO 
{
	public static void main (String[] args) throws Exception
	{
		USTBills_rates rates = new USTBills_rates();
		rates.setAllRate(365);
		rates.setAllRate(180);
		
		
		
	}
}
