package virtualindex.virtualindexcore.computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
public class SharpeRatio 
{
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	public static BigDecimal compute(BigDecimal stDev, BigDecimal priceReturn, BigDecimal riskFreeRate) throws Exception
	{
		
		try{
		BigDecimal result = priceReturn.subtract(riskFreeRate.divide(new BigDecimal(100), RoundingMode.HALF_EVEN));
		result = result.divide(stDev, RoundingMode.HALF_EVEN);
		result = result.setScale(12, RoundingMode.HALF_EVEN);
				
		return result ;}
		
		catch(Exception e)
		{
			logger.severe("Issue with the Sharpe Ration calculation:\nStandard Deviation = "+stDev+"\nReturn = "+priceReturn+"\nRisk Free Rate = "+riskFreeRate);
			throw e;
		}
	}
}
