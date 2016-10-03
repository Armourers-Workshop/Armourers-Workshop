package riskyken.armourersWorkshop.common.library.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class SkinUploader implements Runnable {
    
    private EntityPlayer player;
    
    private SkinUploader(EntityPlayer player) {
        this.player = player;
    }
    
    
    public static void startUpload(EntityPlayer player) {
        (new Thread(new SkinUploader(player), LibModInfo.NAME + " upload thread.")).start();
    }
    
    
    @Override
    public void run() {
        try {
            uploadSkin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void uploadSkin() throws IOException{
        ModLogger.log("Upload Test Started");
        
        if (player == null) {
            return;
        }
        if (player.getGameProfile() == null) {
            return;
        }
        if (player.getGameProfile().getId() == null) {
            return;
        }
        
        //http://plushie.moe/armourers_workshop/skin-list.php
        //http://plushie.moe/armourers_workshop/skin-upload.php
        //fileToUpload
        String skinUpload = "https://plushie.moe/armourers_workshop/skin-upload.php";
        String CRLF = "\r\n";
        String charset = "UTF-8";
        
        //skinUpload = "http://localhost:8080/";
        
        String boundary = Long.toHexString(System.currentTimeMillis());
        String fileName = player.getGameProfile().getName() + ".txt";
        String fileData = player.getGameProfile().getId().toString();
        
        URL uploadUrl = new URL(skinUpload);
        URLConnection connection = uploadUrl.openConnection();
        
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream output = null;
        PrintWriter writer = null;
        
        try {
            output = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            
            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"" + fileName + "\"").append(CRLF);
            writer.append("Content-Type: application/octet-stream").append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            output.write(fileData.getBytes());
            //Files.copy(binaryFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
            
            
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            
            
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String data = "";
            
            String line = "";
            while ((line = reader.readLine()) != null) {
                data += line;
            }
            
            ModLogger.log(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(writer);
        }

        ModLogger.log("Upload Test Finished");
    }
}
