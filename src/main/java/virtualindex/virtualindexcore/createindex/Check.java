package virtualindex.virtualindexcore.createindex;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import virtualindex.virtualindexcore.sqlrequests.DbConnection;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class Check {
	private String strategyName;
	private String direction;
	private ArrayList<String> indexCompo;
	private ArrayList<BigDecimal> indexCompoWeights;
	private String errorMessage = "";
	

	public Check(String strategyName, ArrayList<String> indexCompo, ArrayList<BigDecimal> indexCompoWeights, String direction) {
		this.strategyName = strategyName;
		this.indexCompo = indexCompo;
		this.indexCompoWeights = indexCompoWeights;
		this.direction = direction;

	}

	public boolean getResult() throws Exception {
		if (!checkTotalWeight(this.indexCompoWeights) || !checkCompoUnicity(this.indexCompo)
				|| !checkIndexIsUnique(this.strategyName) || !checkDirection()) {
			return false;
		}

		else {
			return true;
		}
	}

	public String getError() {
		return this.errorMessage;
	}

	private boolean checkTotalWeight(ArrayList<BigDecimal> indexCompoWeights) {
		BigDecimal totalWeight = new BigDecimal("0");
		for (int x = 0; x < indexCompoWeights.size(); x++) {
			totalWeight = totalWeight.add(indexCompoWeights.get(x));
		}

		BigDecimal one = new BigDecimal("1");

		if (totalWeight.compareTo(one) == 0) {
			return true;
		} else {
			this.errorMessage = this.errorMessage + " // Total underlyings weights is not 100% // ";
			return false;
		}
	}

	private boolean checkCompoUnicity(ArrayList<String> indexCompo) {
		HashSet<String> indexCompoHash = new HashSet<String>();

		for (int x = 0; x < indexCompo.size(); x++) {
			indexCompoHash.add(indexCompo.get(x));
		}

		if (indexCompoHash.size() == indexCompo.size()) {
			return true;
		}

		else {
			this.errorMessage = this.errorMessage + " // Underlyings are not unique // ";
			return false;
		}

	}

	private boolean checkIndexIsUnique(String strategyName) throws Exception {
		
		List<String> stratsNameList = SqlRequests2.getAllStratNames();
		if(stratsNameList.contains(strategyName))
		{
			return false;
		}
		else
		{
			return true;
		}
		

	}

	private boolean checkDirection() throws SQLException {

		if (!subCheckDirectionStrat() || !subCheckDirectionUdl()) {
			return false;
		} else
			return true;
	}

	private boolean subCheckDirectionStrat() {
		if (this.direction.equals("xbtc") || this.direction.equals("btcx")) {
			return true;
		}

		else {
			this.errorMessage = this.errorMessage + " // Wrong index direction // ";
			return false;
		}
	}

	private boolean subCheckDirectionUdl() throws SQLException {
		DbConnection sqlQuery = new DbConnection();
		int test = 0;
		for (String udl : this.indexCompo) {
			ResultSet udlDir = sqlQuery.execute("read", "SELECT default_dir FROM strategies WHERE strategy_name='" + udl + "'");

			if (!udlDir.getString(1).equals(this.direction)) {
				test++;
				this.errorMessage = this.errorMessage + " // Wrong underlying direction (" + udl + ") // ";
			}
		}

		if (test != 0) {
			return false;
		} else {
			return true;
		}
	}
}
