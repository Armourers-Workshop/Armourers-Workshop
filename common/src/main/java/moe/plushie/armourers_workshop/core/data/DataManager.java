package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.data.source.SkinFileDataSource;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();

    private SkinFileDataSource fileDataSource;

    private final HashMap<String, Connection> reusableConnections = new HashMap<>();

    public static DataManager getInstance() {
        return INSTANCE;
    }

    public void connect(File rootPath) {
        try {
            reusableConnections.clear();
            // connect to file data source.
            fileDataSource = createFileDataSource(new SkinFileDataSource.Local(rootPath));
            if (fileDataSource != null) {
                fileDataSource.connect();
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

    public String saveSkin(Skin skin) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(5 * 1024);
        SkinFileStreamUtils.saveSkinToStream(stream, skin);
        byte[] bytes = stream.toByteArray();
        return saveSkinData(new ByteArrayInputStream(bytes));
    }

    public Skin loadSkin(String id) throws Exception {
        var inputStream = loadSkinData(id);
        return SkinFileStreamUtils.loadSkinFromStream2(inputStream);
    }

    public String saveSkinData(InputStream inputStream) throws Exception {
        if (fileDataSource != null) {
            return fileDataSource.save(inputStream);
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


    private SkinFileDataSource createFileDataSource(SkinFileDataSource fallback) throws Exception {
        var uri = ModConfig.Common.skinDatabaseURL;
        if (uri.startsWith("jdbc:")) {
            var name = uri.replaceAll("jdbc:([^:]+):(.+)", "$1");
            var source = new SkinFileDataSource.SQL(name, createConnection(uri));
            return new SkinFileDataSource.Fallback(source, fallback);
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


