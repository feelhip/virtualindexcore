package virtualindex.virtualindexcore.ccylivespot;

import java.math.BigDecimal;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class CcyDownloadCenter 
{
	final Logger logger = Logger.getLogger(Principal.class.getName());

	public BigDecimal getAsk(String ccy, String source) throws Exception 
	{
		switch (source)
		{
		case "coinse":
			{
				CoinseLive coinseConn = new CoinseLive();
				return coinseConn.getAsk(ccy);
			}
			
		case "cryptsy":
			{
				CryptsyLivePrice cryptsyConn = new CryptsyLivePrice();
				return cryptsyConn.getIndicativeAsk(ccy);
			}
			
		case "kraken":
		{
			KrakenLive krakenConn = new KrakenLive();
			return krakenConn.getAsk(ccy);
		}
		}
		return null;	
	}
	
	public BigDecimal getBid(String ccy, String source) throws Exception 
	{
		switch (source)
		{
		case "coinse":
			{
				CoinseLive coinseConn = new CoinseLive();
				return coinseConn.getBid(ccy);
			}
			
		case "cryptsy":
			{
				CryptsyLivePrice cryptsyConn = new CryptsyLivePrice();
				return cryptsyConn.getIndicativeBid(ccy);
			}
			
		case "kraken":
		{
			KrakenLive krakenConn = new KrakenLive();
			return krakenConn.getBid(ccy);
		}
		}
		return null;	
	}
	
	public BigDecimal getMid(String ccy, String source) throws Exception 
	{
		switch (source)
		{
		case "coinse":
			{
				CoinseLive coinseConn = new CoinseLive();
				return coinseConn.getMid(ccy);
			}
			
		case "cryptsy":
			{
				CryptsyLivePrice cryptsyConn = new CryptsyLivePrice();
				return cryptsyConn.getIndicativeMid(ccy);
			}
			
		case "kraken":
		{
			KrakenLive krakenConn = new KrakenLive();
			return krakenConn.getMid(ccy);
		}
		}
		return null;	
	}
}
