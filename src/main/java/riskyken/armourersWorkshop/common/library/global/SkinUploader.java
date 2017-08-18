package riskyken.armourersWorkshop.common.library.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class SkinUploader implements Runnable {
    
    private static final String UPLOAD_URL = "https://plushie.moe/armourers_workshop/user-skin-upload.php";
    
    private File file;
    private String name;
    private String userId;
    private String description;
    private String accessToken;
    
    private EntityPlayer player;
    
    
    
    private SkinUploader(File file, String name, String userId, String description, String accessToken) {
        this.file = file;
        this.name = name;
        this.userId = userId;
        this.description = description;
        this.accessToken = accessToken;
    }
    
    public static void startUpload(File file, String name, String userId, String description, String accessToken) {
        (new Thread(new SkinUploader(file, name, userId, description, accessToken), LibModInfo.NAME + " upload thread.")).start();
    }
    
    @Override
    public void run() {
        try {
            uploadSkin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeMultipartData(PrintWriter writer, String boundary, String name, String value) {
        String CRLF = "\r\n";
        String charset = "UTF-8";
        
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
        writer.append(CRLF);
        writer.append(value).append(CRLF);
    }
    
    private void writeMultipartFile(PrintWriter writer, OutputStream output, String boundary, String name, String filename, byte[] fileData) throws IOException {
        String CRLF = "\r\n";
        String charset = "UTF-8";
        
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"").append(CRLF);
        writer.append("Content-Type: application/octet-stream").append(CRLF);
        writer.append("Content-Transfer-.Encoding: binary").append(CRLF);
        writer.append(CRLF).flush();
        output.write(fileData);
        output.flush(); // Important before continuing with writer!
    }
    
    private void uploadSkin() throws IOException{
        ModLogger.log("Upload Test Started");
        
        String CRLF = "\r\n";
        String charset = "UTF-8";
        
        String boundary = Long.toHexString(System.currentTimeMillis());
        
        URL uploadUrl = new URL(UPLOAD_URL);
        URLConnection connection = uploadUrl.openConnection();
        
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream output = null;
        PrintWriter writer = null;
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = IOUtils.toByteArray(fileInputStream);
        fileInputStream.close();
        
        try {
            output = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            
            //Send forum data
            writeMultipartData(writer, boundary, "name", name);
            writeMultipartData(writer, boundary, "userId", userId);
            writeMultipartData(writer, boundary, "description", description);
            writeMultipartData(writer, boundary, "accessToken", accessToken);
            
            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"" + name + "\"").append(CRLF);
            writer.append("Content-Type: application/octet-stream").append(CRLF);
            writer.append("Content-Transfer-.Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            output.write(fileBytes);
            output.flush(); // Important before continuing with writer!
            
            
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            
            
            String result = "";
            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
                result = IOUtils.toString(inputStream, Charsets.UTF_8);
            } catch (IOException e) {
                IOUtils.closeQuietly(inputStream);
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            
            ModLogger.log(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(writer);
        }

        ModLogger.log("Upload Test Finished");
    }
}
