package virtualindex.virtualindexcore.ccylivespot;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.configuration.ConfigSetup;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2.Exchanges;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class CryptsyLivePrice {

	private static final Logger logger = Logger.getLogger(Principal.class.getName());
	private static final int THREADSLEEPINGTIME = Integer.parseInt(ConfigSetup.getValue("webBufferSleepTime"))*2;
	private static final int ATTEMPTSNUMBER = 2;
	private static final String CRYPTSYAPIURL = "http://pubapi.cryptsy.com/api.php?method=singleorderdata&marketid=";
	private static enum DataType {
		BIDPX, ASKPX, BIDQTY, ASKQTY, MIDPX
	};

	private static BigDecimal getJsonData(String ccy, DataType dataType) throws Exception
	{
		String ccyCode;
		String ccyName;
		JSONObject dataJson = new JSONObject();
		JSONObject returnJson = new JSONObject();
		JSONObject lastBidDataJson = new JSONObject();
		JSONObject lastAskDataJson= new JSONObject();

		logger.info("Start retrieving market data for " + ccy);
		
		Hashtable<String, String> codesTable = SqlRequests2.getExchangeCodes(ccy, Exchanges.CRYPTSY);
		
		ccyName = codesTable.get("name");
		ccyCode = codesTable.get("code");

		
		String urlString = CRYPTSYAPIURL + ccyCode;
		
		
		for (int x = 1 ; x<=ATTEMPTSNUMBER; x++)
		{
			try{
			logger.info("Creation of JSON from the buffered page of Cryptsy prices");
			dataJson = new JSONObject(WebPageBuffer.getPage(urlString));
			logger.info("Start JSON parsing");
			 returnJson = dataJson.getJSONObject("return");

			JSONObject returnJson2 = returnJson.getJSONObject(ccyName);

			JSONArray askJson = returnJson2.getJSONArray("sellorders");
			// sellorders (buyorders) est un array d'objets
			JSONArray bidJson = returnJson2.getJSONArray("buyorders");

			// 0 est la premiere ligne du tableau (-> le dernier cours) On cast
			// l'objet de l'array en JSONObject
			 lastBidDataJson = (JSONObject) bidJson.get(0);
			 lastAskDataJson = (JSONObject) askJson.get(0);
			
			break;
			}
			catch (Exception e)
			{
				logger.warning("Issue when parsing the web page (the page content could be empty):\n"+urlString+"\nAttempt "+x+"/"+ATTEMPTSNUMBER+"\nThread sleeping for "+THREADSLEEPINGTIME+" ms");
				Thread.sleep(THREADSLEEPINGTIME);
			}
			if (x==5)
			{
				Exception e = new Exception("Fatal error when parsing the JSON web page:\n"+urlString+"\nProcess failed after "+ATTEMPTSNUMBER+" attempts.");
				logger.severe(e.toString());
				throw e;
			}
		}
		

		logger.info("End of JSON parsing");

		switch (dataType)
		{
		case BIDPX: {
			String bidPx = (String) lastBidDataJson.get("price");
			logger.info(dataType.toString() + " = " + bidPx);
			return new BigDecimal(bidPx);
		}
		case ASKPX: {
			String askPx = (String) lastAskDataJson.get("price");
			logger.info(dataType.toString() + " = " + askPx);
			return new BigDecimal(askPx);
		}
		case MIDPX: {
			String bidPx = (String) lastBidDataJson.get("price");
			String askPx = (String) lastAskDataJson.get("price");
			BigDecimal sum = new BigDecimal(bidPx).add(new BigDecimal(askPx));
			BigDecimal mid = sum.divide(new BigDecimal("2"));
			return mid;
		}
		case BIDQTY: {
			String bidQty = (String) lastBidDataJson.get("quantity");
			logger.info(dataType.toString() + " = " + bidQty);
			return new BigDecimal(bidQty);
		}
		case ASKQTY: {
			String askQty = (String) lastAskDataJson.get("quantity");
			logger.info(dataType.toString() + " = " + askQty);
			return new BigDecimal(askQty);
		}
		default: {
			throw new Exception("SEVERE ERROR - Case not taken into account into the Enumeration");
		}
		}

	}

	public static BigDecimal getIndicativeAsk(String ccy) throws Exception
	{
		return BrokerFees.applyAskFees(new BigDecimal("0.0025"), getJsonData(ccy, DataType.ASKPX));
	}

	public static BigDecimal getIndicativeBid(String ccy) throws Exception
	{
		return BrokerFees.applyBidFees(new BigDecimal("0.0025"), getJsonData(ccy, DataType.BIDPX));
	}

	public static BigDecimal getIndicativeMid(String ccy) throws Exception
	{
		return getJsonData(ccy, DataType.MIDPX);
	}

	public static BigDecimal getTradingAsk(String ccy, BigDecimal brokerFees) throws Exception
	{
		return BrokerFees.applyAskFees(brokerFees, getJsonData(ccy, DataType.ASKPX));
	}

	public static BigDecimal getTradingBid(String ccy, BigDecimal brokerFees) throws Exception
	{
		return BrokerFees.applyBidFees(brokerFees, getJsonData(ccy, DataType.BIDPX));
	}

}
