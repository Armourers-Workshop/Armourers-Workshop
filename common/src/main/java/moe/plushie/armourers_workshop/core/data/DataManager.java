package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.data.source.FileDataSource;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();

    private FileDataSource fileDataSource;

    private final HashMap<String, Connection> reusableConnections = new HashMap<>();

    public static DataManager getInstance() {
        return INSTANCE;
    }

    public void connect(File rootPath) {
        try {
            reusableConnections.clear();
            // connect to file data source.
            fileDataSource = createFileDataSource(new FileDataSource.Local(rootPath));
            if (fileDataSource != null) {
                fileDataSource.connect();
                fileDataSource.setReconnectHandler(() -> {
                    disconnect();
                    connect(rootPath);
                });
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void disconnect() {
        try {
            reusableConnections.clear();
            // disconnect from file data source.
            if (fileDataSource != null) {
                fileDataSource.disconnect();
                fileDataSource = null;
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public String saveSkin(@Nullable String id, Skin skin) throws Exception {
        try (var outputStream = new ByteArrayOutputStream(5 * 1024)) {
            SkinSerializer.writeToStream(skin, null, outputStream);
            return saveSkinData(id, new ByteArrayInputStream(outputStream.toByteArray()));
        }
    }

    public Skin loadSkin(String id) throws Exception {
        try (var inputStream = loadSkinData(id)) {
            return SkinSerializer.readFromStream(null, inputStream);
        }
    }

    public void removeSkin(String id) throws Exception {
        if (fileDataSource != null) {
            fileDataSource.remove(id);
            return;
        }
        throw new Exception("Missing data source connect!");
    }

    public String saveSkinData(@Nullable String id, InputStream inputStream) throws Exception {
        if (fileDataSource != null) {
            return fileDataSource.save(id, inputStream);
        }
        throw new Exception("Missing data source connect!");
    }

    public InputStream loadSkinData(String id) throws Exception {
        if (fileDataSource != null) {
            return fileDataSource.load(id);
        }
        throw new Exception("Missing data source connect!");
    }

    public boolean isConnected() {
        return fileDataSource != null;
    }


    private FileDataSource createFileDataSource(FileDataSource fallback) throws Exception {
        var uri = ModConfig.Common.skinDatabaseURL;
        if (uri.startsWith("jdbc:")) {
            var name = uri.replaceAll("jdbc:([^:]+):(.+)", "$1");
            var source = new FileDataSource.SQL(name, createConnection(uri));
            return switch (ModConfig.Common.skinDatabaseFallback) {
                case 0 -> new FileDataSource.Fallback(source, fallback, false);
                case 2 -> new FileDataSource.Fallback(source, fallback, true);
                default -> source; // only use database source.
            };
        }
        return fallback;
    }

    // https://web.archive.org/web/20240216222419/https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-connect-drivermanager.html#connector-j-examples-connection-drivermanager
    // https://web.archive.org/web/20240704142945/https://github.com/xerial/sqlite-jdbc
    // https://web.archive.org/web/20240721072726/https://github.com/DataGrip/redis-jdbc-driver
    private Connection createConnection(String uri) throws Exception {
        var connection = reusableConnections.get(uri);
        if (connection != null) {
            return connection;
        }
        connection = DriverManager.getConnection(uri);
        reusableConnections.put(uri, connection);
        return connection;
    }
}


