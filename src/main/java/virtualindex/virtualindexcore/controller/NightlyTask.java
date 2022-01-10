package virtualindex.virtualindexcore.controller;

import virtualindex.virtualindexcore.cyclicaljobscontrollers.AnalysorsSelector;
import virtualindex.virtualindexcore.kpireport.KpiGenerator;
import virtualindex.virtualindexcore.sqlrequests.DateProcessing;
import virtualindex.virtualindexcore.sqlrequests.SqlRequests2;

public class NightlyTask {

	static public void nightlyCorrelMatrix() throws Exception {

		// Correlation computation
		AnalysorsSelector.calculateCorrelMatrix(SqlRequests2.getAllStratNames());

		// KPI Correlation report sending and archiving
		KpiGenerator correlKpi = new KpiGenerator.Builder(DateProcessing.todaySqlDate()).correlations().build();
		correlKpi.sendByEmail();
		correlKpi.archive();

	}

}
