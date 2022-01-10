package virtualindex.virtualindexcore.computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class StandardDeviation
{
	private final static Logger logger = Logger.getLogger(Principal.class.getName());

	public static BigDecimal compute(ArrayList<BigDecimal> populationList) throws Exception
	{
		SimpleCalc simpleCalc = new SimpleCalc();
		BigDecimal average = simpleCalc.average(populationList);

		ArrayList<BigDecimal> member1List = new ArrayList<BigDecimal>();
		try {
			for (int x = 0; x < populationList.size(); x++)
			{
				member1List.add(populationList.get(x).subtract(average).pow(2));
			}
			BigDecimal variance = simpleCalc.average(member1List).setScale(12, RoundingMode.HALF_EVEN);

			return simpleCalc.squareRoot(variance);
		} catch (Exception e)
		{
			logger.severe("Problem with Standard Deviation computation. \nPopulation analysed:\n"+populationList.toString());
			throw e;
		}
	}
}
