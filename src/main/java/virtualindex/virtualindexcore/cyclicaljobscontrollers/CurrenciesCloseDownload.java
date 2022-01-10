package virtualindex.virtualindexcore.cyclicaljobscontrollers;

import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.ccylivespot.BitstampLivePrice;
import virtualindex.virtualindexcore.ccylivespot.CryptsyLivePrice;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests;

public class CurrenciesCloseDownload {

	final Logger logger = Logger.getLogger(Principal.class.getName());
private SqlRequests req = new SqlRequests();

	public void populateCloses() throws Exception {
		

		// CRYPTSY DOWNLOAD
		
		ArrayList<String> ccyList = req.getAllCcyNames("cryptsy");

		for (String ccy : ccyList) {
			try {
				int stratId = req.getStratId(ccy);
				Checks check = new Checks();
				if (check.todayValueNotPresent(req.getStratId(ccy))) {
					req.storeStrategyClosePrice(stratId, CryptsyLivePrice.getIndicativeMid(ccy),  "xbtc",
							"cryptsy");
				} else {
					logger.warning(ccy + " : Check failed: see logs");
				}
			}

			catch (Exception e) {
				logger.severe("Currency # " + ccy + " not downloaded: check logs");
				logger.severe(e.toString());
			}
		}

		// BITSTAMP DOWNLOAD
		BitstampLivePrice bitstampLivePrice = new BitstampLivePrice();
		try {
			Checks check = new Checks();
			if (check.todayValueNotPresent(18)) {
				req.storeStrategyClosePrice(18, bitstampLivePrice.getMidPx(), "btcx", "bitstamp");
			} else {
				logger.warning("#18" + " : Check failed: see logs");
			}
		} catch (Exception e) {
			logger.severe("Currency # 18 not downloaded: check logs");
			logger.severe(e.toString());
		}
	}
	
	public void populateCloses(String ccy)
	{
		
		try {
			int stratId = req.getStratId(ccy);
			Checks check = new Checks();
			if (check.todayValueNotPresent(req.getStratId(ccy))) {
				req.storeStrategyClosePrice(stratId, CryptsyLivePrice.getIndicativeMid(ccy),  "xbtc",
						"cryptsy");
			} else {
				logger.warning(ccy + " : Check failed: see logs");
			}
		}

		catch (Exception e) {
			logger.severe("Currency # " + ccy + " not downloaded: check logs");
			logger.severe(e.toString());
		}
	}
	
}
