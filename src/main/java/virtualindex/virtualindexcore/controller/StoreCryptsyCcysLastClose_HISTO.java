package virtualindex.virtualindexcore.controller;

import java.util.ArrayList;

import virtualindex.virtualindexcore.ccyhistospot.CryptsyHistoImport;

public class StoreCryptsyCcysLastClose_HISTO 
{
	public static void main(String[] args) throws Exception
	{
		//VARIABLES
		ArrayList<String> ccyList = new ArrayList<String>();	
		//SqlRequests req = new SqlRequests();
		//ccyList = req.getAllCcyNames();
		ccyList.add("BTC");
				
		for (int x = 0; x< ccyList.size(); x++)
		{	
		CryptsyHistoImport histoImport = new CryptsyHistoImport(ccyList.get(x));
		histoImport.displayJSON();
		//histoImport.copyToDb("2014-11-25");
		}
	}
}


