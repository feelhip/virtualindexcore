package virtualindex.virtualindexcore.ccylivespot;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class CoinseLive {
	private BigDecimal dataArray[] = new BigDecimal[4];
	private String ccyCode;
	private DbConnection query = new DbConnection();
	final Logger logger = Logger.getLogger(Principal.class.getName());

	private void createConnection(String ccy) throws Exception
	{
		logger.info("Start retrieving market data for " + ccy);
		ResultSet selection = query.execute("read", "SELECT coinse_name, coinse_code FROM strategies WHERE strategy_name ='" + ccy + "'");

		this.ccyCode = selection.getString(2);

		String urlString = "https://www.coins-e.com/api/v2/market/" + this.ccyCode + "/depth/";

		WebPageBuffer bufferWebPage = new WebPageBuffer();

		logger.info("Creation of the Buffer Page for Coinse prices");
		JSONObject dataJson = new JSONObject(bufferWebPage.getPage(urlString));

		logger.info("Start JSON parsing");
		JSONObject marketdepth = dataJson.getJSONObject("marketdepth");

		JSONArray askJson = marketdepth.getJSONArray("bids"); 
		JSONArray bidJson = marketdepth.getJSONArray("asks");

		// 0 est la premiere ligne du tableau (-> le dernier cours)

		// On cast l'objet de l'array en JSONObject
		JSONObject lastBidDataJson = (JSONObject) bidJson.get(0);
		JSONObject lastAskDataJson = (JSONObject) askJson.get(0);

		String bidQty = (String) lastBidDataJson.get("q");
		String bidPx = (String) lastBidDataJson.get("r");
		String askQty = (String) lastAskDataJson.get("q");
		String askPx = (String) lastAskDataJson.get("r");

		logger.info("End of JSON parsing");

		this.dataArray[0] = new BigDecimal(bidQty);
		logger.info("BidQty = " + this.dataArray[0]);
		this.dataArray[1] = new BigDecimal(bidPx);
		logger.info("BidPx = " + this.dataArray[1]);
		this.dataArray[2] = new BigDecimal(askQty);
		logger.info("AskQty = " + this.dataArray[2]);
		this.dataArray[3] = new BigDecimal(askPx);
		logger.info("AskPx = " + this.dataArray[3]);
	}

	public BigDecimal[] getAllMarketData(String ccy) throws Exception
	{
		createConnection(ccy);
		return this.dataArray;
	}

	public BigDecimal getAsk(String ccy) throws Exception
	{
		createConnection(ccy);
		return this.dataArray[3];
	}

	public BigDecimal getBid(String ccy) throws Exception
	{
		createConnection(ccy);
		return this.dataArray[1];
	}

	public BigDecimal getMid(String ccy) throws Exception
	{
		createConnection(ccy);

		BigDecimal sum = this.dataArray[1].add(this.dataArray[3]);
		BigDecimal mid = sum.divide(new BigDecimal("2"));
		return mid;
	}

}
