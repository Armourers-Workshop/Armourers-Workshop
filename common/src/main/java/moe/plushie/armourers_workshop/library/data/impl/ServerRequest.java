package moe.plushie.armourers_workshop.library.data.impl;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class ServerRequest {

    private String path = "";
    private ServerPermission permission = null;
    private final ArrayList<String> query = new ArrayList<>();
    private final ArrayList<String> body = new ArrayList<>();

    public static ServerRequest fromJSON(IODataObject object) {
        var request = new ServerRequest();
        object.get("path").ifPresent(it -> request.path = it.stringValue());
        object.get("query").allValues().forEach(it -> request.query.add(it.stringValue()));
        object.get("body").allValues().forEach(it -> request.body.add(it.stringValue()));
        if (request.path.isEmpty()) {
            return null;
        }
        return request;
    }

    public static Object createFile(String name, Callable<ByteBuf> bytes) {
        return new ServerRequest.MultipartFormFile(name, bytes);
    }

    public Callable<InputStream> build(String baseURL, Map<String, ?> parameters) throws Exception {
        var builder = new StringBuilder();
        builder.append(baseURL);
        builder.append(path);
        // concat all query parameters.
        var delimiter = "?";
        for (var it : query) {
            builder.append(delimiter);
            builder.append(getKey(it));
            builder.append("=");
            builder.append(getQueryValue(it, parameters));
            delimiter = "&";
        }
        // when body is not required, we will create a simple request.
        if (body.isEmpty()) {
            var part = new SinglePart(new URL(builder.toString()));
            return part::upload;
        }
        var multipartForm = new MultipartForm(builder.toString());
        for (var it : body) {
            multipartForm.add(getBodyValue(it, parameters));
        }
        return multipartForm::upload;
    }

    public boolean has(String key) {
        return query.contains(key) || body.contains(key);
    }

    public boolean isSSLRequired() {
        return permission != null && permission.isSSLRequired();
    }

    public String path() {
        return path;
    }

    public void setPermission(ServerPermission permission) {
        this.permission = permission;
    }

    public ServerPermission permission() {
        return permission;
    }

    private String getKey(String key) {
        if (key.contains("=")) {
            return getValue(key.split("="), 0);
        }
        return key;
    }

    private String getQueryValue(String key, Map<String, ?> parameters) throws Exception {
        if (key.contains("=")) {
            return getValue(key.split("="), 1);
        }
        var value = parameters.get(key);
        if (value == null) {
            ModLog.debug("missing value of '{}' at '{}', with: {}", key, path, parameters);
            throw new RuntimeException("missing value of '" + key + "' at '" + path + "'");
        }
        return URLEncoder.encode(value.toString(), "UTF-8");
    }

    private MultipartForm.Value getBodyValue(String key, Map<String, ?> parameters) throws Exception {
        if (key.contains("=")) {
            String[] part = key.split("=");
            return new MultipartForm.Text(getValue(part, 0), getValue(part, 1));
        }
        var value = parameters.get(key);
        if (value == null) {
            ModLog.debug("missing value of '{}' at '{}', with: {}", key, path, parameters);
            throw new RuntimeException("missing value of '" + key + "' at '" + path + "'");
        }
        if (value instanceof MultipartFormFile file) {
            return file.build(key);
        }
        return new MultipartForm.Text(key, value.toString());
    }

    private String getValue(String[] s1, int index) {
        if (index < s1.length) {
            return s1[index];
        }
        return "";

    }

    public static class SinglePart {

        private final URL url;

        public SinglePart(URL url) {
            this.url = url;
        }

        public InputStream upload() throws IOException {
            var connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            return connection.getInputStream();
        }
    }

    private static class MultipartForm {

        private static final String CRLF = "\r\n";

        private final String charset = "UTF-8";
        private final String uploadUrl;
        private final ArrayList<Value> values = new ArrayList<>();

        public MultipartForm(String uploadUrl) {
            this.uploadUrl = uploadUrl;
        }

        public void add(Value value) {
            values.add(value);
        }

        public InputStream upload() throws IOException {
            //ModLogger.log("Accessing: " + uploadUrl);

            var boundary = Long.toHexString(System.currentTimeMillis());

            var uploadUrl = new URL(this.uploadUrl);
            var connection = uploadUrl.openConnection();

            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream output = null;
            PrintWriter writer = null;

            try {
                output = connection.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

                for (var value : values) {
                    value.write(output, writer, boundary);
                }

                // End of multipart/form-data.
                writer.append("--" + boundary + "--").append(CRLF).flush();

                return connection.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                StreamUtils.closeQuietly(writer);
                StreamUtils.closeQuietly(output);
            }

            return null;
        }

        private interface Value {
            void write(OutputStream output, PrintWriter writer, String boundary) throws IOException;
        }

        private static class Text implements Value {

            private final String name;
            private final String value;

            public Text(String name, String value) {
                this.name = name;
                this.value = value;
            }

            @Override
            public void write(OutputStream output, PrintWriter writer, String boundary) throws IOException {
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(CRLF);
                writer.append("Content-Type: text/plain; charset=").append("UTF-8").append(CRLF);
                writer.append(CRLF);
                writer.append(value).append(CRLF);
            }
        }

        private static class File implements Value {

            private final String name;
            private final String filename;
            private final ByteBuf fileBytes;

            public File(String name, String filename, ByteBuf fileBytes) {
                this.name = name;
                this.filename = filename;
                this.fileBytes = fileBytes;
            }

            @Override
            public void write(OutputStream output, PrintWriter writer, String boundary) throws IOException {
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"").append(CRLF);
                writer.append("Content-Type: application/octet-stream").append(CRLF);
                writer.append(CRLF).flush();
                output.write(fileBytes.array());
                output.flush(); // Important must flush before continuing with writer!
                writer.append(CRLF).flush();
            }
        }
    }

    private static class MultipartFormFile {

        private final String name;
        private final Callable<ByteBuf> bytes;

        public MultipartFormFile(String name, Callable<ByteBuf> bytes) {
            this.name = name;
            this.bytes = bytes;
        }

        public MultipartForm.File build(String key) throws Exception {
            return new MultipartForm.File(key, name, bytes.call());
        }
    }
}
