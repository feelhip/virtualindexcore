package virtualindex.virtualindexcore.controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import virtualindex.virtualindexcore.hedging.Purchase;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class PurchaseOrders 
{
	public static void main(String[] args) throws Exception 
	{	
		//Purchase variables
		ArrayList<BigDecimal>brokerFeesList = new ArrayList<BigDecimal>();
		brokerFeesList.add(new BigDecimal("0.02"));
		brokerFeesList.add(new BigDecimal("0.03"));
		brokerFeesList.add(new BigDecimal("0.035"));
		
		
		String stratName = "TOPTEST_1000";
		
		BigDecimal notional = new BigDecimal("2");
		BigDecimal fees = new BigDecimal("0.01"); // Fees are a percentage
		//---------
		displayOrder(stratName, notional, fees,brokerFeesList);
		
		//Record purchase variables
		int investorId = 2;
		String orderStratName = "TOPTEST_1000";
		
		BigDecimal orderNotional = new BigDecimal("2"); 
		BigDecimal orderFees = new BigDecimal("0.01"); //Fees are a percentage (ex: 1 = 100%)
		//Manual set of the exact amounts of the underlyings to be purchased
		ArrayList<BigDecimal> udlsAmt = new ArrayList<BigDecimal> ();			
		udlsAmt.add(new BigDecimal("38575.436"));	// example
		udlsAmt.add(new BigDecimal("287.792"));	// example
		udlsAmt.add(new BigDecimal("968167.816"));	// example		
		//---------		
		
		recordNewDeal(investorId, orderStratName, orderNotional, orderFees,udlsAmt,brokerFeesList);
		
		int dealId = 1; // TEST - Example
		recordTapDeal( dealId,   orderNotional, orderFees,udlsAmt,brokerFeesList);
	}
	
	
	
	private static void displayOrder(String stratName ,BigDecimal notional , BigDecimal fees, ArrayList<BigDecimal>brokerFeesList) throws Exception
	{

		int stratId = SqlRequests2.getStratId(stratName);
		
		
		
		Purchase purchase = new Purchase(stratName, notional, fees, brokerFeesList);
		int x = 0;
		BigDecimal totalBtcAmt = new BigDecimal("0");
		for(BigDecimal toHedge: purchase.getTheoricHedgingAmt())
		{
			System.out.println("-------");
			System.out.println("BUY "+SqlRequests2.getComposition_names(SqlRequests2.getComposition(stratId)).get(x));
			System.out.println(toHedge);
			System.out.println("SELL BTC: ");
			System.out.println(purchase.getBtcFx().get(x));
			totalBtcAmt = totalBtcAmt.add(purchase.getBtcFx().get(x));
			x++;
		}
		System.out.println("-------");
		System.out.println("Total BTC amout: "+totalBtcAmt+" vs. notional: "+notional);
	}
	
	private static void recordNewDeal(int investorId, String stratName,BigDecimal nominal,BigDecimal fees, ArrayList<BigDecimal> udlsAmt,ArrayList<BigDecimal>brokerFeesList) throws Exception 
	{
		SqlRequests2.createNewDeal(investorId, SqlRequests2.getStratId(stratName));
		int newDealId = SqlRequests2.getLastDealId();

		Purchase recordedPurchase = new Purchase(stratName, nominal, fees,brokerFeesList);
		recordedPurchase.setRealHedgingAmt(newDealId,  nominal, fees, udlsAmt);
	}
	
	private static void recordTapDeal(int dealId, BigDecimal nominal,BigDecimal fees, ArrayList<BigDecimal> udlsAmt,ArrayList<BigDecimal>brokerFeesList) throws Exception 
	{
		int stratId = SqlRequests2.getStratIdFromDealId(dealId);
		
		Purchase purchase = new Purchase(SqlRequests2.getStratName(stratId), nominal, fees,brokerFeesList);
		purchase.setRealHedgingAmt(dealId, nominal, fees, udlsAmt);
	}

}
