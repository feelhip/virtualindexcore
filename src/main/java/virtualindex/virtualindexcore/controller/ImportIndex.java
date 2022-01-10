package virtualindex.virtualindexcore.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import virtualindex.virtualindexcore.createindex.ImportJsonStrat;
import virtualindex.virtualindexcore.createindex.StoreStrategy;

public class ImportIndex 
{
	static public  void create(String fileName) throws Exception
	{
		ImportJsonStrat importStrat = new ImportJsonStrat(fileName);
			
		String name = importStrat.getName();
		String fullName = importStrat.getFullName();
		BigDecimal base = importStrat.getBase();
		String direction = importStrat.getDirection();
		String description = importStrat.getDescription();
		String currency = importStrat.getCurrency();
		String startDate = importStrat.getStartDate();
		ArrayList<String> indexCompo = importStrat.getCompoNames();		
		ArrayList<BigDecimal> indexCompoWeights = importStrat.getCompoWeights();							
		
		StoreStrategy indexTest = new StoreStrategy();	
		try {
			indexTest.create(name, indexCompo, indexCompoWeights, base, direction,fullName,description, currency,startDate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	
	
}
