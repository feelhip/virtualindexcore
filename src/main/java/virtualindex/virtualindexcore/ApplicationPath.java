package virtualindex.virtualindexcore;

import java.io.File;

public class ApplicationPath {
	
	public static String getApplicationPath() {
		File file = new File(Principal.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		File parent = file.getParentFile();
		return parent.toString();
	}
}
