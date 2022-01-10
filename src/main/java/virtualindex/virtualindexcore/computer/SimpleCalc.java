package virtualindex.virtualindexcore.computer;
import java.util.logging.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import virtualindex.virtualindexcore.Principal;

public class SimpleCalc 
{
	private final static Logger logger = Logger.getLogger(Principal.class.getName());
	public static BigDecimal average (ArrayList<BigDecimal> populationList)
	{
		BigDecimal average = new BigDecimal("0");
		BigDecimal total = new BigDecimal("0");
		BigDecimal popSize = new BigDecimal(populationList.size());
		
		for(int x = 0; x< populationList.size();x++)
		{
			total = total.add(populationList.get(x));
		}
		
		average = total.divide(popSize, 12, RoundingMode.HALF_EVEN);
		
		return average;
		
	}
	
	public static BigDecimal squareRoot (BigDecimal number) throws Exception
	{
		try{
		String stringVar1 = number.toString();
		
		Double doubleVar1 = Double.parseDouble(stringVar1);
		Double doubleVar2 = (double) 0 ;
		doubleVar2 = Math.sqrt(doubleVar1);
		String stringVar2 = doubleVar2.toString();	
		BigDecimal bigDecVar2 =  new BigDecimal(stringVar2).setScale(12, RoundingMode.HALF_EVEN);
		
		
		return bigDecVar2;}
		catch (Exception e)
		{
			logger.severe("Issue with the Square Root calculation.\nNUmber to compute: "+number);
			throw e;
		}
	}
}
