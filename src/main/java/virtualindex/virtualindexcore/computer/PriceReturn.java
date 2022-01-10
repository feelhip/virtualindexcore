package virtualindex.virtualindexcore.computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class PriceReturn {
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	public static BigDecimal compute(BigDecimal oldValue, BigDecimal newValue) throws Exception
	{
		try{
		BigDecimal result = new BigDecimal("0");
		result.setScale(12, RoundingMode.HALF_EVEN);		
		result = newValue.subtract(oldValue);
		result = result.divide(oldValue,12,RoundingMode.HALF_EVEN);
		
		return result ;
		}
		catch (Exception e)
		{
			logger.severe("Issue with the Return computation.\nOld value = "+oldValue+" \nNew value = "+newValue); 
			throw e;
		}
	}

}
