package virtualindex.virtualindexcore.ccylivespot;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.webreader.WebPageBuffer;

public class KrakenLive {
	private BigDecimal dataArray[]= new BigDecimal[4];
	private String ccyCode;
	private String ccyName;
	private DbConnection query = new DbConnection();
	final Logger logger = Logger.getLogger(Principal.class.getName());
	
	private void createConnection (String ccy) throws Exception
	{	
		logger.info("Start retrieving market data for "+ccy);
		ResultSet selection = query.execute("read","SELECT kraken_name, kraken_code FROM strategies WHERE strategy_name ='"+ccy+"'");
		this.ccyName = selection.getString(1);
		this.ccyCode = selection.getString(2);
		
		String urlString = "https://api.kraken.com/0/public/Depth?pair="+this.ccyCode+"&count=1";
		
		// resultat : {"error":[],"result":{"XXBTZEUR":{"asks":[["313.53960","0.462",1413342066]],"bids":[["311.85701","0.400",1413342067]]}}}
		WebPageBuffer bufferWebPage = new WebPageBuffer();
		
		
		logger.info("Creation of the Buffer Page for Kraken prices");
		
		JSONObject dataJson = new JSONObject(bufferWebPage.getPage(urlString));

		logger.info("Start JSON parsing");
		JSONObject result = dataJson.getJSONObject("result");
		
		JSONObject result2 = result.getJSONObject(ccyName);
		
		JSONArray askJson = result2.getJSONArray("asks");
		JSONArray bidJson = result2.getJSONArray("bids"); 
		
		JSONArray askJson2 = askJson.getJSONArray(0);
		JSONArray bidJson2 = bidJson.getJSONArray(0); 
		
		
		String bidQty = (String) bidJson2.get(1);
		String bidPx = (String) bidJson2.get(0);
		String askQty = (String) askJson2.get(1);
		String askPx = (String) askJson2.get(0);
		
		/*
		//On cast l'objet de l'array en JSONObject
		JSONObject lastBidDataJson = (JSONObject) bidJson.get(0);
		JSONObject lastAskDataJson = (JSONObject) askJson.get(0);
		
		
		String bidQty = (String) lastBidDataJson.get("q");
		String bidPx = (String) lastBidDataJson.get("r");
		String askQty = (String) lastAskDataJson.get("q");
		String askPx = (String) lastAskDataJson.get("r");*/
		
		logger.info("End of JSON parsing");
		
		this.dataArray[0]= new BigDecimal(bidQty);
		logger.info("BidQty = "+this.dataArray[0]);
		this.dataArray[1]=new BigDecimal(bidPx);
		logger.info("BidPx = "+this.dataArray[1]);
		this.dataArray[2]=new BigDecimal(askQty);
		logger.info("AskQty = "+this.dataArray[2]);
		this.dataArray[3]=new BigDecimal(askPx);
		logger.info("AskPx = "+this.dataArray[3]);
	}
	
	public BigDecimal[] getAllMarketData(String ccy) throws Exception
	{
		createConnection(ccy);
		return this.dataArray;
	}
	
	public BigDecimal getAsk(String ccy) throws Exception
	{
		createConnection(ccy);
		return this.dataArray[3] ;
	}
	
	public BigDecimal getBid(String ccy) throws Exception
	{
		createConnection(ccy);
		return this.dataArray[1] ;
	}
	
	public BigDecimal getMid(String ccy) throws Exception
	{
		createConnection(ccy);
		
		BigDecimal  sum = this.dataArray[1].add(this.dataArray[3] );
		BigDecimal mid = sum.divide(new BigDecimal("2"));	
		return mid ;
	}

}
