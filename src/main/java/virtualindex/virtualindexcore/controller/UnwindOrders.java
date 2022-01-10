package virtualindex.virtualindexcore.controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import virtualindex.virtualindexcore.hedging.Unwind;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class UnwindOrders {

	public static void main(String[] args) throws Exception 
	{
		//Purchase variables
		ArrayList<BigDecimal>brokerFeesList = new ArrayList<BigDecimal>();
		brokerFeesList.add(new BigDecimal("0.02"));
		brokerFeesList.add(new BigDecimal("0.03"));
		brokerFeesList.add(new BigDecimal("0.035"));	
		
		//VARIABLES
		int unwoundDealId = 1;
		BigDecimal unwindPortion = new BigDecimal("1"); // example - percentage of the total amount to be sold
		//---------	
		displayOrder(unwoundDealId,unwindPortion,brokerFeesList);
		
		
		//VARIABLES
		
		ArrayList<BigDecimal> btcExchangePriceList = new ArrayList<>();
		btcExchangePriceList.add(new BigDecimal("0.4823"));	// example
		btcExchangePriceList.add(new BigDecimal("0.4829"));	// example
		btcExchangePriceList.add(new BigDecimal("0.8392"));	// example
		//---------
		
		
		
		
		
		recordUnwind(unwoundDealId, unwindPortion, btcExchangePriceList,brokerFeesList);
	}

	
	
	private static void displayOrder(int dealId , BigDecimal unwindPortion, ArrayList<BigDecimal> brokerFeesList) throws Exception
	{
		int stratId = SqlRequests2.getStratIdFromDealId(dealId);
		ArrayList<ArrayList<Object>> composition = SqlRequests2.getComposition(stratId);
		
		Unwind unwind = new Unwind(dealId, unwindPortion, brokerFeesList);
				//Display theoretical dehedging amounts
		ArrayList <BigDecimal> toDehedge = unwind.getTheoricDehedgingBtcPrice();
		
		try{
		
		int x =0;
		
		for(BigDecimal amt: toDehedge)
		{	
			System.out.println("Sell: "+unwind.getAmtsListToDeHedge().get(x)+" "+SqlRequests2.getComposition_names(composition).get(x));
			System.out.println("Buy "+amt+" BTC");
			x++;
		}}
		catch(Exception e)
		{
			System.out.println("Position is closed\n"+e.toString());
			}
		
	}
	
	private static void recordUnwind(int dealId, BigDecimal unwindPortion, ArrayList<BigDecimal> btcExchangePriceList, ArrayList<BigDecimal> brokerFeesList) throws Exception
	{

		
		Unwind unwind = new Unwind(dealId, unwindPortion,brokerFeesList);
		unwind.setRealDehedgingBtcPrice(btcExchangePriceList);
	}
	
}
