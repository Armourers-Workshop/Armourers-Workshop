package moe.plushie.armourers_workshop.library.data.global;

import moe.plushie.armourers_workshop.utils.StreamUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MultipartForm {
    
    private static final String CRLF = "\r\n";
    
    private final String charset = "UTF-8";
    private final String uploadUrl;
    private final ArrayList<MultipartText> textList;
    private final ArrayList<MultipartFile> fileList;
    
    public MultipartForm(String uploadUrl) {
        this.uploadUrl = uploadUrl;
        textList = new ArrayList<>();
        fileList = new ArrayList<>();
    }
    
    public void addText(String name, String value) {
        textList.add(new MultipartText(name, value));
    }
    
    public void addFile(String name, String filename, byte[] fileBytes) {
        fileList.add(new MultipartFile(name, filename, fileBytes));
    }
    
    private class MultipartText {
        
        private final String name;
        private final String value;
        
        public MultipartText(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public void write(PrintWriter writer, String boundary) {
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
            writer.append(CRLF);
            writer.append(value).append(CRLF);
        }
    }
    
    private class MultipartFile {
        
        private final String name;
        private final String filename;
        private final byte[] fileBytes;
        
        public MultipartFile(String name, String filename, byte[] fileBytes) {
            this.name = name;
            this.filename = filename;
            this.fileBytes = fileBytes;
        }
        
        public void write(OutputStream output, PrintWriter writer, String boundary) throws IOException {
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"").append(CRLF);
            writer.append("Content-Type: application/octet-stream").append(CRLF);
            writer.append("Content-Transfer-.Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();;
            output.write(fileBytes);
            output.flush(); // Important must flush before continuing with writer!
            writer.append(CRLF).flush();
        }
    }
    
    public String upload() throws IOException{
        //ModLogger.log("Accessing: " + uploadUrl);
        
        String boundary = Long.toHexString(System.currentTimeMillis());
        
        URL uploadUrl = new URL(this.uploadUrl);
        URLConnection connection = uploadUrl.openConnection();
        
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream output = null;
        PrintWriter writer = null;
        
        String result = "";
        
        try {
            output = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            
            //Send forum text data.
            for (int i = 0; i < textList.size(); i++) {
                textList.get(i).write(writer, boundary);
            }
            
            // Send binary files.
            for (int i = 0; i < fileList.size(); i++) {
                fileList.get(i).write(output, writer, boundary);
            }
            
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            
            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
                result = StreamUtils.toString(inputStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                StreamUtils.closeQuietly(inputStream);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(writer);
            StreamUtils.closeQuietly(output);
        }

        return result;
    }
}
