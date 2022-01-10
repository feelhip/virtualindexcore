package virtualindex.virtualindexcore.hedging;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeesAdding 

{
	
	
	public BigDecimal applyFees(BigDecimal grossAmt, BigDecimal fees)
	{
		BigDecimal result= grossAmt.subtract(grossAmt.multiply(fees));
		
		
		return result.setScale(12, RoundingMode.HALF_EVEN);
	}
}
