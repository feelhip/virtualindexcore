package virtualindex.virtualindexcore.buildingalgorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;

import virtualindex.virtualindexcore.controller.BacktestIndex;
import virtualindex.virtualindexcore.createindex.StoreStrategy;


public class DiscreteParametersBuilding 
{
	private String name;
	private String fullName ;
	private BigDecimal base = new BigDecimal("100").setScale(12, RoundingMode.HALF_EVEN);
	private String direction = "xbtc" ;
	private String description ;
	private String currency ;
	private String startDate ;
	private ArrayList<String> indexCompo ;		
	private ArrayList<BigDecimal> indexCompoWeights ;
	
	public void buildIndex(int basketSz, boolean equiweighted) throws Exception
	{
		buildBasket();
		StoreStrategy indexTest = new StoreStrategy();	
		try {
			indexTest.create(name, indexCompo, indexCompoWeights, base, direction,fullName,description, currency,startDate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BacktestIndex.backtest(this.name);
	}
	
	private void buildBasket()
	{
		
		
	}
	
	
}
