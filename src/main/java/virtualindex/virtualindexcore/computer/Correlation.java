package virtualindex.virtualindexcore.computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class Correlation
{
	private Covariance covar = new Covariance();
	private final static Logger logger = Logger.getLogger(Principal.class.getName());

	public synchronized BigDecimal compute(ArrayList<BigDecimal> popList1, ArrayList<BigDecimal> popList2) throws Exception
	{
		try {
			BigDecimal cov = this.covar.compute(popList1, popList2);
			BigDecimal correl = cov.divide(StandardDeviation.compute(popList1).multiply(StandardDeviation.compute(popList2)), 12, RoundingMode.HALF_EVEN);
			return correl;
		}
		catch (Exception e)
		{
			logger.severe("Error in correlation computation: StDev of one series could be null (0E-12) \n StDev Series1 = " + StandardDeviation.compute(popList1) + "\n StDev Series2 = " + StandardDeviation.compute(popList2) + "\n" + e.toString());
			throw e;
		}
	}
}
