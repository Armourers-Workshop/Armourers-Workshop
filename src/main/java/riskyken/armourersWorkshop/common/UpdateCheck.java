package riskyken.armourersWorkshop.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;

public class UpdateCheck implements Runnable {

    /** Should we check for updates. */
	public static boolean checkForUpdates = true;
	
	/** Only show update for this version of Mincraft. */
	public static boolean relevantUpdates = true;
	
	/** The url to use for update checking */
	//private static final String UPDATE_URL = "https://dl.dropboxusercontent.com/u/9733425/app_update/mods/armourers-workshop/update.txt";
	private static final String UPDATE_URL = "http://bit.ly/1qLai6P";
	
	/** Was an update found. */
	public static boolean updateFound = false;
	
	public static String remoteModVersion;
	public static String remoteMinecraftVersion;
	public static String remoteVersionInfo;
	
	public static void checkForUpdates() {
		if (!checkForUpdates) { return; }
		
		(new Thread(new UpdateCheck(),LibModInfo.NAME + " update thread.")).start();
	}

	@Override
	public void run() {
		ModLogger.log("Starting Update Check");
		String localVersion = LibModInfo.VERSION;
		
		try {
		    if (localVersion.contains("-")) {
		        String[] lvSplit = localVersion.split("-");
		        localVersion = lvSplit[1];
		    }
		    
			String location = UPDATE_URL;
			HttpURLConnection conn = null;
			while (location != null && !location.isEmpty()) {
				URL url = new URL(location);

				if (conn != null)
					conn.disconnect();

				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
				conn.setRequestProperty("Referer", "http://" + LibModInfo.VERSION);
				conn.connect();
				location = conn.getHeaderField("Location");
			}

			if (conn == null)
				throw new NullPointerException();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;

			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("@");
				if (tokens[0] != null && !tokens[0].equals("") && tokens.length > 2) {
					remoteModVersion = tokens[0];
					remoteMinecraftVersion = tokens[1];
					remoteVersionInfo = tokens[2];
					break;
				}
			}
			conn.disconnect();
			reader.close();

			if (versionCompare(localVersion, remoteModVersion) < 0) {
			    if (relevantUpdates) {
			        if (!MinecraftForge.MC_VERSION.equals(remoteMinecraftVersion)) {
			            updateFound = false;
			            ModLogger.log("Update found but not relevant.");
			        } else {
			            updateFound = true;
		                ModLogger.log("Update needed. New version " + remoteModVersion + " your version " + LibModInfo.VERSION);
			        }
			    } else {
			        updateFound = true;
			        ModLogger.log("Update needed. New version " + remoteModVersion + " your version " + LibModInfo.VERSION);
			    }
			}
			else {
			    updateFound = false;
				ModLogger.log("Is up to date");
			}
			
		} catch (Exception e) {
			ModLogger.log(Level.WARN, "Unable to read from remote version authority.");
			ModLogger.log(Level.WARN, e.toString());
			updateFound = false;
		}
		
	}
	
	private int versionCompare(String str1, String str2)
	{
	    String[] vals1 = str1.split("\\.");
	    String[] vals2 = str2.split("\\.");
	    int i = 0;
	    // set index to first non-equal ordinal or length of shortest version string
	    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) 
	    {
	      i++;
	    }
	    // compare first non-equal ordinal number
	    if (i < vals1.length && i < vals2.length) 
	    {
	        int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
	        return Integer.signum(diff);
	    }
	    // the strings are equal or one string is a substring of the other
	    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
	    else
	    {
	        return Integer.signum(vals1.length - vals2.length);
	    }
	}
	
}
