package moe.plushie.armourers_workshop.common.update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraftforge.common.MinecraftForge;

public class UpdateCheck implements Runnable {

    /** Should we check for updates. */
	public static boolean checkForUpdates = true;
	
	/** The url to use for update checking */
	//http://plushie.moe/app_update/minecraft_mods/armourers_workshop/update.txt
	//http://bit.ly/2dm5iGe
	
	//http://plushie.moe/app_update/minecraft_mods/armourers_workshop/update.json
	//http://bit.ly/2dxhJ1c
	private static final String UPDATE_URL = "http://bit.ly/2dxhJ1c";
	
	/** Was an update found. */
	public static boolean updateFound = false;
	
	public static String remoteModVersion;
	
	public static void checkForUpdates() {
		if (!checkForUpdates){
		    return;
		}
		
		(new Thread(new UpdateCheck(),LibModInfo.NAME + " update thread.")).start();
	}

	@Override
	public void run() {
		ModLogger.log("Starting Update Check");
		String localVersion = LibModInfo.VERSION;
		
		//localVersion = "1.7.10-0.39.9";
		if(localVersion.equals("@VERSION@")) {
		    return;
		}
		
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
			
			String data = "";
			
			String line = "";
			while ((line = reader.readLine()) != null) {
			    data += line;
			}
	        conn.disconnect();
	        reader.close();
	        
			JsonObject json = (JsonObject) new JsonParser().parse(data);
			
			remoteModVersion = json.getAsJsonObject("promos").get(MinecraftForge.MC_VERSION + "-latest").getAsString();
			
			ModLogger.log(String.format("Latest version for Minecraft %s is %s.", MinecraftForge.MC_VERSION, remoteModVersion));

			if (versionCompare(localVersion, remoteModVersion) < 0) {
                updateFound = true;
                ModLogger.log("Update needed. New version " + remoteModVersion + " your version " + localVersion);
			} else {
			    updateFound = false;
				ModLogger.log("Mod is up to date with the latest version.");
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
