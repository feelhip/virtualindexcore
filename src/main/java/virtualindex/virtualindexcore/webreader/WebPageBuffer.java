package virtualindex.virtualindexcore.webreader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;
import virtualindex.virtualindexcore.configuration.ConfigSetup;
import virtualindex.virtualindexcore.email.Email;

public class WebPageBuffer {
	private static final Logger logger = Logger.getLogger(Principal.class.getName());
	private static final int THREADSLEEPINGTIME = Integer.parseInt(ConfigSetup.getValue("webBufferSleepTime"));
	
	public static String getPage(String urlString) throws Exception {

		// Open the connection

		HttpURLConnection connection = null;
		Map<String, String> errorsMap = new HashMap<String, String>();
		BufferedReader reader = null;

		for (int x = 1; x <= 5; x++) {

			errorsMap.clear();

			try {
				URL url = new URL(urlString);
				logger.info("HTTP connection started");
				connection = (HttpURLConnection) url.openConnection();
				connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			} catch (Exception exception) {
				errorsMap.put("Error #" + x, exception.toString());
				logger.severe("Connection refused - Attempt n:" + x);
				try {
					logger.severe("Trying to disconnect");
					connection.disconnect();
					logger.severe("Disconnected");
				} catch (Exception e)
				{
					logger.severe("Disconnection failed. (already disconnected)");
				}
				logger.severe(exception.toString());
				try
				{
					String newLine = System.getProperty("line.separator");
					Email email = new Email();
					String messObj = "Connection Refused - retrying in 1 min (" + x + "/5)";
					String message = "Connection refused" + newLine + "URL : " + urlString + newLine + "Attempt to reconnect n:" + x;
					email.send(messObj, message);
				} catch (Exception mailException)
				{
					logger.severe("Mail not sent - check logs");
					logger.severe(mailException.toString());
				}
				logger.severe("Thread sleeping for "+THREADSLEEPINGTIME+"ms");
				Thread.sleep(THREADSLEEPINGTIME);
				logger.severe("Thread awaken - retry connection");
			}

			if (errorsMap.isEmpty()) {
				logger.info("Web page stored successfully in buffer");
				break;
			}
			else if (x == 5)
			{
				Exception e = new Exception("Issue when accessing web page:\n" + urlString);
				logger.severe(e.toString());
				throw e;
			}
		}

		// Store the BufferReader in the a String
		String dataString = reader.readLine().toString();
		connection.disconnect();
		return dataString;
	}
}
