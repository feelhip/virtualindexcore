package virtualindex.virtualindexcore.computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class Covariance 
{
	private Checks checks = new Checks();
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	
	public BigDecimal compute (ArrayList <BigDecimal> popList1, ArrayList <BigDecimal> popList2)
	{
		if (this.checks.areListSameSz(popList1, popList2))
		{
			ArrayList <BigDecimal> varList = new ArrayList<BigDecimal>();
			BigDecimal avgPop1 = SimpleCalc.average(popList1);
			BigDecimal avgPop2 = SimpleCalc.average(popList2);
			
			for(int x = 0; x<popList1.size(); x++)
			{
				varList.add(popList1.get(x).subtract(avgPop1).multiply(popList2.get(x).subtract(avgPop2)));
			}
			
			BigDecimal avg = SimpleCalc.average(varList).setScale(12, RoundingMode.HALF_EVEN);
			
			return avg;
		}

		
		else
		{
			logger.warning("List are not of the same size: check logs");
			System.out.println("List are not of the same size: check logs");
			return null;
		}
	}
}
