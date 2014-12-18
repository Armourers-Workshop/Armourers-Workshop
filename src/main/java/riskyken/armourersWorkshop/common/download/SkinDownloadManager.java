package riskyken.armourersWorkshop.common.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class SkinDownloadManager implements Runnable {

    private static final String SKIN_LIST_URL = "https://dl.dropboxusercontent.com/u/9733425/app_update/mods/armourers-workshop/skin-list.txt";
    private static final String SKIN_FOLDER_URL = "https://dl.dropboxusercontent.com/u/9733425/app_update/mods/armourers-workshop/skins/";
    
    public static void downloadSkins() {
        if (!ConfigHandler.downloadSkins) {
            return;
        }
        new Thread(new SkinDownloadManager(), LibModInfo.NAME + " download thread").start();
    }
    
    private ArrayList<String> downloadSkinList() {
        ModLogger.log("Downloading skin list");
        ArrayList<String> fileList = new ArrayList<String>();
        BufferedReader input = null;
        URL url;
        try {
            url = new URL(SKIN_LIST_URL);
            URLConnection urlCon = url.openConnection();
            input = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String line;
            while ((line = input.readLine()) != null)  {
                fileList.add(line);
            }
        }  catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
        }
        return fileList;
    }
    
    public void downloadSkin(String name, ArrayList<String> localFileList) {
        if (!name.contains("@")) {
            return;
        }
        String[] nameSplit = name.split("@");
        String fileName = nameSplit[1];
        
        if (localFileList.contains(fileName)) {
            //Already have this file downloaded.
            return;
        }
        
        fileName = fileName + ".armour";
        
        URL url;
        try {
            url = new URL(SKIN_FOLDER_URL + nameSplit[0]);
            downloadFile(url, fileName);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }
    
    private void downloadFile(URL url, String fileName) {
        ModLogger.log("Downloading skin: " + fileName);
        
        if (!createArmourDirectory()) {
            return;
        }
        
        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        File targetFile = new File(armourDir, File.separatorChar + fileName);
        
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        
        try {
            rbc = Channels.newChannel(url.openStream());
            fos = new FileOutputStream(targetFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(rbc);
        }
    }
    
    public static boolean createArmourDirectory() {
        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        if (!armourDir.exists()) {
            try {
                armourDir.mkdir();
            } catch (Exception e) {
                ModLogger.log(Level.WARN, "Unable to create armour directory.");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        ArrayList<String> remoteFileList = downloadSkinList();
        ArrayList<String> localFileList = TileEntityArmourLibrary.getFileNames();
        for (int i = 0; i < remoteFileList.size(); i++) {
            String file = remoteFileList.get(i);
            downloadSkin(file, localFileList);
        }
    }
}
