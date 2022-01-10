package virtualindex.virtualindexcore.hedging;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;

import virtualindex.virtualindexcore.ccylivespot.CryptsyLivePrice;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class Purchase {

	private BigDecimal stratAmount;
	private int stratId;

	private ArrayList<BigDecimal> btcSellingAmtList = new ArrayList<BigDecimal>();
	private ArrayList<BigDecimal> weightsList = new ArrayList<BigDecimal>();
	private ArrayList<Integer> compoIds = new ArrayList<Integer>();
	private ArrayList<String> compoNames = new ArrayList<String>();
	private ArrayList<BigDecimal> hedgingAmountList = new ArrayList<BigDecimal>();
	private ArrayList<BigDecimal>brokerFeesList = new ArrayList<BigDecimal>();

	public Purchase(String indexName, BigDecimal stratInvestedAmt, BigDecimal myFees,ArrayList<BigDecimal>brokerFeesList) throws Exception {
		FeesAdding feesAdding = new FeesAdding();
		this.stratAmount = feesAdding.applyFees(stratInvestedAmt, myFees);
		this.stratId = SqlRequests2.getStratId(indexName);
		ArrayList<ArrayList<Object>> composition = SqlRequests2.getComposition(this.stratId);
		this.weightsList = SqlRequests2.getComposition_weights(composition);
		this.compoIds = SqlRequests2.getComposition_ids(composition);
		this.compoNames = SqlRequests2.getComposition_names(composition);
		this.brokerFeesList = brokerFeesList;
		
		computeTheoricHedgingAmt(brokerFeesList);
	}

	public ArrayList<BigDecimal> getTheoricHedgingAmt()  {
		
		return hedgingAmountList;
	}
	public ArrayList<BigDecimal> getBtcFx() {
		return this.btcSellingAmtList;
	}

	public BigDecimal getTotalBtcAmt()
	{
		BigDecimal total = new BigDecimal("0");
		for(BigDecimal amt:btcSellingAmtList )
		{
			total.add(amt);
		}
		
		return total.setScale(12, RoundingMode.HALF_EVEN);
	}
	
	
	public void setRealHedgingAmt(int dealId, BigDecimal grossBtcAmt, BigDecimal fees, ArrayList<BigDecimal> udlsAmt)
			throws Exception {

		Hashtable<String, String> compoTable = SqlRequests2.getStratCompo(stratId);

		if (compoTable.size() != udlsAmt.size()) {
			throw new Exception("The number of the strategy underlying is different from the number of hedging amount input");
		}

		SqlRequests2.storeNewOrder( dealId,  this.stratId, grossBtcAmt, "purchase", fees);
		int x = 0;
		for (BigDecimal udlAmt : udlsAmt) {
			System.out.println("Ccy: " + this.compoNames.get(x) + " - Buy:" + udlAmt + " - Sell: "
					+ this.stratAmount.multiply(weightsList.get(x)) + " BTC");
			SqlRequests2.recordCurrencyTrade(SqlRequests2.getLastOrderId(), this.compoIds.get(x), udlAmt,
					this.stratAmount.multiply(weightsList.get(x)).multiply(new BigDecimal("-1")),this.brokerFeesList.get(x), "purchase");
			x++;
		}

	}

	private  void computeTheoricHedgingAmt(ArrayList<BigDecimal>brokerFeesList) throws Exception
	{
		for (int x = 0; x < this.weightsList.size(); x++) {
			BigDecimal weight = this.weightsList.get(x).multiply(this.stratAmount);
			CryptsyLivePrice liveCryptsyPrice = new CryptsyLivePrice();
			BigDecimal livePrice = liveCryptsyPrice.getTradingAsk(this.compoNames.get(x),brokerFeesList.get(x));

			BigDecimal spot_btcx = new BigDecimal("1").divide(livePrice, 12, RoundingMode.HALF_EVEN);
			BigDecimal hedgeAmt = spot_btcx.multiply(weight);
			hedgingAmountList.add(hedgeAmt.setScale(12, RoundingMode.HALF_EVEN));
			btcSellingAmtList.add(livePrice.multiply(hedgeAmt).setScale(12, RoundingMode.HALF_EVEN));
		}
	}
	
}
