package virtualindex.virtualindexcore.ccylivespot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONException;

import virtualindex.virtualindexcore.filehandler.json.JsonReader;

public class BitstampLivePrice {

	private JsonReader jsonreader;

	public BitstampLivePrice() throws Exception {
		jsonreader = new JsonReader("https://www.bitstamp.net/api/ticker/");
	}

	public BigDecimal getAskPx() throws JSONException {
		return jsonreader.getBigDecimal("ask");
	}

	public BigDecimal getBidPx() throws JSONException {
		return jsonreader.getBigDecimal("bid");
	}

	public BigDecimal getMidPx() throws JSONException {
		BigDecimal sum = jsonreader.getBigDecimal("ask").add(jsonreader.getBigDecimal("bid"));
		return sum.divide(new BigDecimal("2"), 12, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getVWAP() throws JSONException {
		return jsonreader.getBigDecimal("vwap");
	}
}
