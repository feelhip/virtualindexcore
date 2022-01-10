package virtualindex.virtualindexcore.ccylivespot;

import java.math.BigDecimal;

public class BrokerFees 
{
	public static BigDecimal applyBidFees(BigDecimal fees, BigDecimal spot)
	{
		spot = spot.subtract(spot.multiply(fees));
		
		return spot;	
	}
	
	public static BigDecimal applyAskFees(BigDecimal fees, BigDecimal spot)
	{
		spot = spot.add(spot.multiply(fees));
		
		return spot;	
	}
}
