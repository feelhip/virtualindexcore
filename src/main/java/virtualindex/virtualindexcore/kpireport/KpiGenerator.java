package virtualindex.virtualindexcore.kpireport;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.configuration.ConfigSetup;
import virtualindex.virtualindexcore.email.Email;

public class KpiGenerator {
	
			private final String date ;
			private final String correlationReport;
			private final String analysorsReport ;
			private final String currenciesReport;
			private final String marketDateReport;
			private final String version = "["+ConfigSetup.getValue("version")+"]";
	
	
	public static class Builder {
		// Required parameters
		private  String date = "";
		

		// Optional parameters
		private String correlationReport = "";
		private String analysorsReport = "";
		private String currenciesReport = "";
		private String marketDateReport = "";

		public Builder(String date) {
			this.date = date;
		}

		public Builder currencies() {

			this.currenciesReport = CurrenciesReport.generate(date);
			return this;
		}
		
		public Builder correlations() throws SQLException {

			this.correlationReport =  CorrelationReport.generate(date);
			return this;
		}
		
		public Builder marketData() throws Exception {

			this.marketDateReport =  MarketDataReport.generate(date);
			return this;
		}
		
		public Builder analysors() throws SQLException {

			this.analysorsReport =  AnalysorsReport.generate(date);
			return this;
		}
		
		public KpiGenerator build()
		{
			return new KpiGenerator(this);
		}

	}
	
	private KpiGenerator(Builder builder)
	{
		date = builder.date;
		correlationReport = builder.correlationReport;
		currenciesReport = builder.currenciesReport;
		marketDateReport = builder.marketDateReport;
		analysorsReport = builder.analysorsReport;
	}
	
	private String getStringFormat()
	{
		String newLine = System.getProperty("line.separator");
		
		return "----------------------------------------------------------" 
		+ newLine 
		+ version +" -- KPI REPORT -- " + date + " --"
		+ newLine 
		+ "----------------------------------------------------------" 
		+ newLine 
		+ currenciesReport
		+ newLine 
		+ marketDateReport
		+ newLine 
		+ analysorsReport
		+ newLine 
		+ correlationReport
		;
	}
	
	public void sendByEmail() throws IOException, JSONException
	{
		Email email = new Email();
		email.send("Daily KPI Report "+date, getStringFormat());
		
	}
	
	public void archive() throws IOException
	{
		final String path = Principal.path;
		Date generationTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSZ");
		String generationTimeStr = sdf.format(generationTime);
		FileWriter file = new FileWriter(path+"/outputs/kpi_report/daily_kpi_of_"+date+"_"+generationTimeStr+".txt");
		file.write(getStringFormat());
		file.flush();
		file.close();
	}
	
}
