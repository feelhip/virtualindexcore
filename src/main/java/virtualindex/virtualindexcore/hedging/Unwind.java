package virtualindex.virtualindexcore.hedging;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import virtualindex.virtualindexcore.ccylivespot.CryptsyLivePrice;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class Unwind {
	private int dealId;
	private BigDecimal btcUnwindPortion;
	private BigDecimal btcUnwindTotalAmount = new BigDecimal("0");
	private int stratId;
	private ArrayList<ArrayList<Object>> composition = new ArrayList<>();
	private ArrayList<BigDecimal> brokerFeesList = new ArrayList<BigDecimal>();
	private Checks checks = new Checks();

	public Unwind(int dealId, BigDecimal btcUnwindPortion, ArrayList<BigDecimal> brokerFeesList) throws Exception {
		this.dealId = dealId;
		this.stratId = SqlRequests2.getStratIdFromDealId(dealId);
		this.composition = SqlRequests2.getComposition(stratId);
		this.btcUnwindPortion = btcUnwindPortion;
		this.brokerFeesList = brokerFeesList;
	}

	public ArrayList<BigDecimal> getTheoricDehedgingBtcPrice() throws Exception {
		ArrayList<BigDecimal> hedgedAmtList = SqlRequests2.getHedgedAmountList(this.dealId);
		ArrayList<BigDecimal> btcBuyList = new ArrayList<BigDecimal>();

		if (!this.checks.checkHedgedAmts(hedgedAmtList)) {
			return null;
		} else {

			int x = 0;
			for (BigDecimal ccyHedgeAmt : hedgedAmtList) {

				CryptsyLivePrice liveCryptsyPrice = new CryptsyLivePrice();
				BigDecimal liveBid = liveCryptsyPrice.getTradingBid(SqlRequests2.getComposition_names(this.composition).get(x), brokerFeesList.get(x));

				BigDecimal btcToBuy = ccyHedgeAmt.multiply(liveBid).multiply(this.btcUnwindPortion);

				btcToBuy = btcToBuy.setScale(12, RoundingMode.HALF_EVEN);

				btcBuyList.add(btcToBuy);
				btcUnwindTotalAmount = btcUnwindTotalAmount.add(btcToBuy);
				x++;
			}
			return btcBuyList;
		}
	}

	public void setRealDehedgingBtcPrice(ArrayList<BigDecimal> btcPriceList) throws Exception {
		if (!this.checks.checkHedgedAmts(SqlRequests2.getHedgedAmountList(this.dealId))) {
			System.out.println("Deal id#" + this.dealId + " is totally unwound");
		} else {

			BigDecimal totalBtcRedeemed = new BigDecimal("0");
			for (BigDecimal btcPrice : btcPriceList) {
				totalBtcRedeemed = totalBtcRedeemed.add(btcPrice);
			}
			SqlRequests2.storeNewOrder(dealId,  stratId, totalBtcRedeemed.multiply(new BigDecimal("-1")), "unwind", new BigDecimal("0"));

			int x = 0;
			for (BigDecimal btcPrice : btcPriceList) {

				SqlRequests2.recordCurrencyTrade(SqlRequests2.getLastOrderId(), SqlRequests2.getComposition_ids(this.composition).get(x),
						getAmtsListToDeHedge().get(x).multiply(new BigDecimal("-1")), btcPrice, brokerFeesList.get(x),"unwind");

				x++;
			}
		}
	}
	
	public BigDecimal getBtcTotalUnwoundAmount()
	{
		return btcUnwindTotalAmount.setScale(12, RoundingMode.HALF_EVEN);
	}

	public ArrayList<BigDecimal> getAmtsListToDeHedge() throws Exception

	{
		ArrayList<BigDecimal> amtList = new ArrayList<>();
		if (!this.checks.checkHedgedAmts(amtList)) {
			return null;
		}
		amtList = SqlRequests2.getHedgedAmountList(dealId);
		for (int x = 0; x < amtList.size(); x++) {
			amtList.set(x, amtList.get(x).multiply(this.btcUnwindPortion));
		}
		return amtList;
	}
}