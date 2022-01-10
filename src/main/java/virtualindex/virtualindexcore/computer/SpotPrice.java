package virtualindex.virtualindexcore.computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
public class SpotPrice 
{
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	public static BigDecimal calculate(
			 
			ArrayList<BigDecimal> indexCompoWeights,
			ArrayList<BigDecimal> udlBasePrices,
			ArrayList<BigDecimal> udlCurrentPrices,
			BigDecimal basePrice) throws Exception
	
	{
		BigDecimal price = new BigDecimal("0");
		price.setScale(12, RoundingMode.HALF_EVEN);
		
		BigDecimal res1 = new BigDecimal("0");
		res1.setScale(12, RoundingMode.HALF_EVEN);
		
		BigDecimal res2 = new BigDecimal("0");
		res2.setScale(12, RoundingMode.HALF_EVEN);
		
		BigDecimal total = new BigDecimal("0");
		total.setScale(12, RoundingMode.HALF_EVEN);
		try{
		for(int x =0; x<indexCompoWeights.size(); x++ )
		{
			
			res1 = udlCurrentPrices.get(x).multiply(basePrice);
			res2= res1.divide(udlBasePrices.get(x),12,RoundingMode.HALF_EVEN);
			price = res2.multiply(indexCompoWeights.get(x));
			
			total = total.add(price);
			
		}
		
		return total;}
		catch(Exception e)
		{
			logger.severe("Issue with the Index Price Computation.\nUnderlyings weights:\n"+indexCompoWeights.toString()+
					"\nUnderlyings Base Prices:\n"+udlBasePrices+"\nUnderlyings Current Prices:\n"+udlCurrentPrices+"\nBase Price:\n"+basePrice);
			throw e;
		}
	}
}
