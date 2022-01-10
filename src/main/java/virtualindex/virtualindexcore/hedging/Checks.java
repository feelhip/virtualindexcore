package virtualindex.virtualindexcore.hedging;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Checks 
{
	public Boolean checkHedgedAmts(ArrayList <BigDecimal> hedgedAmtList)
	{
		int tester = 0;
		
		for (int x = 0; x< hedgedAmtList.size(); x++)
		{
			if (hedgedAmtList.get(x).compareTo(new BigDecimal("0"))!=0)
			{
				tester ++;
			}
		}
		
		if (tester == hedgedAmtList.size())
		{
			System.out.println("CHECK OK: There are still positions on the deal");
			return true;
		}
		
		else if (tester != hedgedAmtList.size() && tester !=0)
		{
			System.out.println("SEVERE: Inconsistency in the hedged amounts: check database");
			return false;
		}
		
		else 
		{
			System.out.println("WARNING: No size remaining: this deal is totally unwound");
			return false;
		}
		

	}
}
