package moe.plushie.armourers_workshop.core.data.source;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenUUID;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class FileDataSource {

    protected String name = "";
    protected String lastGenUUID = "";

    public abstract void connect() throws Exception;

    public abstract void disconnect() throws Exception;

    public void setReconnectHandler(Runnable reconnectHandler) {
    }

    public String save(@Nullable String id, InputStream stream) throws Exception {
        var bytes = StreamUtils.readStreamToByteArray(stream);
        var fileHash = Arrays.hashCode(bytes);
        var identifier = searchNode(fileHash, bytes);
        if (identifier != null) {
            return identifier;
        }
        var skin = SkinSerializer.readFromStream(null, new ByteArrayInputStream(bytes));
        identifier = getFreeId(id);
        insertNode(identifier, skin, fileHash, bytes);
        return identifier;
    }

    public InputStream load(String id) throws Exception {
        var bytes = queryNode(id);
        return new ByteArrayInputStream(bytes);
    }

    public abstract void remove(String id) throws Exception;

    public abstract boolean contains(String id) throws Exception;

    public abstract long getCreationTime(String id) throws Exception;

    public abstract long getLastModifiedTime(String id) throws Exception;

    protected abstract void insertNode(String id, Skin skin, int hash, byte[] bytes) throws Exception;

    protected abstract byte[] queryNode(String id) throws Exception;

    protected abstract String searchNode(int hash, byte[] bytes) throws Exception;

    protected abstract List<String> queryNodes() throws Exception;

    private String getFreeId(@Nullable String id) throws Exception {
        if (id != null) {
            return id;
        }
        var uuid = lastGenUUID;
        while (uuid.isEmpty() || contains(uuid)) {
            uuid = OpenUUID.randomUUIDString();
        }
        lastGenUUID = uuid;
        return uuid;
    }

    public static class SQL extends FileDataSource {

        private final Connection connection;

        private Runnable reconnectHandler;
        private ScheduledExecutorService keepAliveChecker;

        private PreparedStatement insertStatement;
        private PreparedStatement existsStatement;
        private PreparedStatement searchStatement;
        private PreparedStatement timeStatement;
        private PreparedStatement queryStatement;
        private PreparedStatement queryAllStatement;
        private PreparedStatement removeStatement;

        public SQL(String name, Connection connection) {
            this.name = name;
            this.connection = connection;
        }

        @Override
        public void setReconnectHandler(Runnable reconnectHandler) {
            this.reconnectHandler = reconnectHandler;
        }

        @Override
        public void connect() throws SQLException {
            ModLog.debug("Connect to file db: '{}'", name);

            // try to create skin table if needed.
            var builder = new SQLTableBuilder("Skin");
            builder.add("id", "VARCHAR(48) NOT NULL PRIMARY KEY");
            builder.add("type", "VARCHAR(48)");
            builder.add("created_at", "TIMESTAMP");
            builder.add("modified_at", "TIMESTAMP");
            builder.add("name", "VARCHAR(512)");
            builder.add("flavour", "VARCHAR(1024)");
            builder.add("author", "VARCHAR(48)");
            builder.add("hash", "INT NOT NULL");
            builder.add("file", "LONGBLOB NOT NULL");
            builder.execute(connection);

            // create precompiled statement when create after;
            queryAllStatement = connection.prepareStatement("SELECT `id` FROM `Skin`");
            queryStatement = connection.prepareStatement("SELECT `file` FROM `Skin` where `id` = (?)");
            searchStatement = connection.prepareStatement("SELECT `id`, `file` FROM `Skin` where `hash` = (?)");
            existsStatement = connection.prepareStatement("SELECT `id` FROM `Skin` where `id` = (?)");
            timeStatement = connection.prepareStatement("SELECT `created_at`, `modified_at` FROM `Skin` where `id` = (?)");
            insertStatement = connection.prepareStatement("INSERT INTO `Skin` (`id`, `type`, `author`, `name`, `flavour`, `created_at`, `modified_at`, `hash`, `file`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            removeStatement = connection.prepareStatement("DELETE FROM `Skin` where `id` = (?)");

            // we need to check the validity of the connection.
            if (ModConfig.Common.skinDatabaseKeepAlive > 0) {
                keepAliveChecker = createKeepAliveChecker(ModConfig.Common.skinDatabaseKeepAlive);
            }
        }

        @Override
        public void disconnect() throws SQLException {
            ModLog.debug("Disconnect from file db: '{}'", name);

            if (keepAliveChecker != null) {
                keepAliveChecker.shutdownNow();
                keepAliveChecker = null;
            }

            safeClose(removeStatement);
            safeClose(insertStatement);
            safeClose(timeStatement);
            safeClose(existsStatement);
            safeClose(searchStatement);
            safeClose(queryStatement);
            safeClose(queryAllStatement);

            connection.close();
        }

        @Override
        protected void insertNode(String id, Skin skin, int hash, byte[] bytes) throws SQLException {
            ModLog.debug("Save file '{}' into '{}'", id, name);
            insertStatement.setString(1, id);
            insertStatement.setString(2, skin.type().registryName().toString());
            insertStatement.setString(3, skin.authorUUID());
            insertStatement.setString(4, skin.customName());
            insertStatement.setString(5, skin.flavourText());
            insertStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            insertStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            insertStatement.setInt(8, hash);
            insertStatement.setBytes(9, bytes);
            insertStatement.executeUpdate();
        }

        @Override
        protected byte[] queryNode(String id) throws Exception {
            queryStatement.setString(1, id);
            try (var result = queryStatement.executeQuery()) {
                if (result.next()) {
                    ModLog.debug("Load file '{}' from '{}'", id, name);
                    return result.getBytes(1);
                }
            }
            throw new FileNotFoundException("the file '" + id + "' not found in " + name + "!");
        }

        @Override
        protected List<String> queryNodes() throws Exception {
            try (var result = queryAllStatement.executeQuery()) {
                var results = new ArrayList<String>();
                while (result.next()) {
                    results.add(result.getString(1));
                }
                return results;
            }
        }

        @Override
        protected String searchNode(int hash, byte[] bytes) throws SQLException {
            searchStatement.setInt(1, hash);
            try (var result = searchStatement.executeQuery()) {
                while (result.next()) {
                    byte[] bytes2 = result.getBytes(2);
                    if (Arrays.equals(bytes, bytes2)) {
                        return result.getString(1);
                    }
                }
                return null;
            }
        }

        @Override
        public void remove(String id) throws Exception {
            ModLog.debug("Remove file '{}' from '{}'", id, name);
            removeStatement.setString(1, id);
            removeStatement.executeUpdate();
        }

        @Override
        public boolean contains(String id) throws SQLException {
            existsStatement.setString(1, id);
            try (var result = existsStatement.executeQuery()) {
                return result.next();
            }
        }

        @Override
        public long getCreationTime(String id) throws SQLException {
            timeStatement.setString(1, id);
            try (var result = timeStatement.executeQuery()) {
                if (result.next()) {
                    return result.getTimestamp(1).getTime();
                }
                return 0;
            }
        }

        @Override
        public long getLastModifiedTime(String id) throws SQLException {
            timeStatement.setString(1, id);
            try (var result = timeStatement.executeQuery()) {
                if (result.next()) {
                    return result.getTimestamp(2).getTime();
                }
                return 0;
            }
        }

        private ScheduledExecutorService createKeepAliveChecker(int seconds) {
            var executor = java.util.concurrent.Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                try {
                    if (!connection.isValid(2)) {
                        if (reconnectHandler != null) {
                            reconnectHandler.run();
                        }
                    }
                } catch (SQLException e) {
                    if (reconnectHandler != null) {
                        reconnectHandler.run();
                    }
                }
            }, 0, seconds, TimeUnit.SECONDS);
            return executor;
        }

        private void safeClose(AutoCloseable closeable) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static class Local extends FileDataSource {

        private static final int NODE_DATA_VERSION = 2;

        private final File nodeRootPath;

        private final ExecutorService thread = Executors.newFixedThreadPool(1, "AW-SKIN-IO");
        private final Map<String, Node> nodes = new ConcurrentHashMap<>();

        public Local(File rootPath) {
            this.name = rootPath.getParentFile().getName();
            this.nodeRootPath = new File(rootPath, "objects");
        }

        @Override
        public void connect() throws Exception {
            ModLog.debug("Connect to file db: '{}'", name);
            this.loadNodes();
        }

        @Override
        public void disconnect() throws Exception {
            ModLog.debug("Disconnect from file db: '{}'", name);
            this.thread.shutdown();
        }

        @Override
        protected void insertNode(String id, Skin skin, int hash, byte[] bytes) {
            ModLog.debug("Save file '{}' into '{}'", id, name);
            var newNode = new Node(id, hash, bytes);
            newNode.save(new ByteArrayInputStream(bytes));
            nodes.put(newNode.id, newNode);
        }

        @Override
        protected byte[] queryNode(String id) throws Exception {
            var node = nodes.get(id);
            if (node == null) {
                // when the identifier not found in the nodes,
                // we will check the file once.
                var parent = new File(nodeRootPath, id);
                if (parent.isDirectory()) {
                    node = loadNode(parent);
                }
            }
            if (node != null && node.isValid()) {
                // we can safely access the node now.
                ModLog.debug("Load file '{}' from '{}'", id, name);
                return StreamUtils.readFileToByteArray(node.getFile());
            }
            throw new FileNotFoundException("the node '" + id + "' not found in " + name + "!");
        }

        @Override
        protected List<String> queryNodes() throws Exception {
            return new ArrayList<>(nodes.keySet());
        }

        @Override
        protected String searchNode(int hash, byte[] bytes) throws Exception {
            for (var node : nodes.values()) {
                if (node.isValid() && node.fileHash == hash && node.equalContents(bytes)) {
                    return node.id;
                }
            }
            return null;
        }

        @Override
        public void remove(String id) throws Exception {
            ModLog.debug("Remove file '{}' from '{}'", id, name);
            var node = nodes.remove(id);
            if (node != null) {
                node.remove();
            }
        }

        @Override
        public boolean contains(String id) throws Exception {
            return nodes.containsKey(id);
        }

        @Override
        public long getCreationTime(String id) throws Exception {
            var node = nodes.get(id);
            if (node != null) {
                return FileUtils.getCreationTime(node.getFile());
            }
            throw new FileNotFoundException("the node '" + id + "' not found in " + name + "!");
        }

        @Override
        public long getLastModifiedTime(String id) throws Exception {
            var node = nodes.get(id);
            if (node != null) {
                return FileUtils.getLastModifiedTime(node.getFile());
            }
            throw new FileNotFoundException("the node '" + id + "' not found in " + name + "!");
        }

        private void loadNodes() {
            // objects/<skin-id>/0|1
            for (var file : FileUtils.listFilesRecursive(nodeRootPath)) {
                if (file.isDirectory()) {
                    loadNode(file);
                }
            }
        }

        private Node loadNode(File parent) {
            try {
                var indexFile = new File(parent, "0");
                if (indexFile.exists()) {
                    var serializer = new TagSerializer(new FileInputStream(indexFile));
                    var node = new Node(serializer);
                    nodes.put(node.id, node);
                    return node;
                }
                var name = FileUtils.getRelativePath(parent, nodeRootPath);
                var node = generateNode(name.substring(1), new File(parent, "1"));
                if (node != null) {
                    nodes.put(node.id, node);
                    return node;
                }
            } catch (Exception e) {
                ModLog.error("can't load file: {}, pls try fix or remove it.", parent);
            }
            return null;
        }

        private Node generateNode(String identifier, File skinFile) throws Exception {
            if (!skinFile.isFile()) {
                return null;
            }
            var bytes = StreamUtils.readFileToByteArray(skinFile);
            var stream = new ByteArrayInputStream(bytes);
            var skin = SkinSerializer.readFromStream(null, stream);
            if (skin == null) {
                return null;
            }
            var node = new Node(identifier, Arrays.hashCode(bytes), bytes);
            node.save(new ByteArrayInputStream(bytes));
            return node;
        }

        private class Node implements IDataSerializable.Immutable {

            final String id;
            final int version;

            final int fileSize;
            final int fileHash;

            Node(String id, int hash, byte[] bytes) {
                this.id = id;
                this.version = NODE_DATA_VERSION;
                // file
                this.fileSize = bytes.length;
                this.fileHash = hash;
            }

            Node(IDataSerializer serializer) {
                this.id = serializer.read(CodingKeys.UID);
                this.version = serializer.read(CodingKeys.VERSION);
                // file
                this.fileSize = serializer.read(CodingKeys.FILE_SIZE);
                this.fileHash = serializer.read(CodingKeys.FILE_HASH);
            }

            @Override
            public void serialize(IDataSerializer serializer) {
                serializer.write(CodingKeys.UID, id);
                serializer.write(CodingKeys.VERSION, version);
                serializer.write(CodingKeys.FILE_SIZE, fileSize);
                serializer.write(CodingKeys.FILE_HASH, fileHash);
            }

            public void save(InputStream inputStream) {
                thread.execute(() -> {
                    try {
                        var skinFile = getFile();
                        var indexFile = getIndexFile();
                        FileUtils.forceMkdirParent(skinFile);
                        var outputStream = new FileOutputStream(skinFile);
                        StreamUtils.transferTo(inputStream, outputStream);
                        TagSerializer.writeToStream(this, new FileOutputStream(indexFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            public void remove() {
                thread.execute(() -> {
                    var skinFile = getFile();
                    FileUtils.deleteQuietly(skinFile.getParentFile());
                });
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Node that)) return false;
                return fileSize == that.fileSize && fileHash == that.fileHash;
            }

            public boolean equalContents(byte[] bytes) {
                int index = 0;
                try (var stream = new FileInputStream(getFile())) {
                    byte[] buff = new byte[1024];
                    while (index < bytes.length) {
                        // when the readable content is smaller than the chunk size,
                        // this call should return actual size or -1.
                        int readSize = stream.read(buff);
                        if (readSize <= 0) {
                            break;
                        }
                        // prevents target files different from declaring file size in the index file.
                        if (index + readSize > bytes.length) {
                            return false;
                        }
                        // we maybe need a higher efficient method of comparison, not this.
                        for (int i = 0; i < readSize; ++i) {
                            if (bytes[index + i] != buff[i]) {
                                return false;
                            }
                        }
                        index += readSize;
                    }
                } catch (Exception ignored) {
                }
                // the index and length should be exactly the same after we finish comparing,
                // if not, it means that the target file is too small.
                return index == bytes.length;
            }

            @Override
            public int hashCode() {
                return Objects.hash(fileSize, fileHash);
            }

            public File getFile() {
                return new File(nodeRootPath, id + "/1");
            }

            public File getIndexFile() {
                return new File(nodeRootPath, id + "/0");
            }

            public boolean isValid() {
                return getFile().exists();
            }
        }

        private static class CodingKeys {

            public static final IDataSerializerKey<String> UID = IDataSerializerKey.create("UUID", IDataCodec.STRING, "");
            public static final IDataSerializerKey<Integer> VERSION = IDataSerializerKey.create("Version", IDataCodec.INT, 0);
            public static final IDataSerializerKey<Integer> FILE_SIZE = IDataSerializerKey.create("FileSize", IDataCodec.INT, 0);
            public static final IDataSerializerKey<Integer> FILE_HASH = IDataSerializerKey.create("FileHash", IDataCodec.INT, 0);
        }
    }


    public static class Fallback extends FileDataSource {

        private final FileDataSource source;
        private final FileDataSource fallbackSource;

        private final boolean isMoveFiles;

        public Fallback(FileDataSource source, FileDataSource fallbackSource, boolean isMoveFiles) {
            this.source = source;
            this.fallbackSource = fallbackSource;
            this.isMoveFiles = isMoveFiles;
        }

        @Override
        public void setReconnectHandler(Runnable reconnectHandler) {
            super.setReconnectHandler(reconnectHandler);
            fallbackSource.setReconnectHandler(reconnectHandler);
        }

        @Override
        public void connect() throws Exception {
            fallbackSource.connect();
            source.connect();
            // move all file into database if needed.
            if (isMoveFiles) {
                migrateAllSkins();
            }
        }

        @Override
        public void disconnect() throws Exception {
            source.disconnect();
            fallbackSource.disconnect();
        }

        @Override
        protected void insertNode(String id, Skin skin, int hash, byte[] bytes) throws Exception {
            source.insertNode(id, skin, hash, bytes);
        }

        @Override
        protected byte[] queryNode(String id) throws Exception {
            try {
                return source.queryNode(id);
            } catch (FileNotFoundException exception) {
                // when the file exists in fallback, we will try using fallback version.
                if (fallbackSource.contains(id)) {
                    return fallbackSource.queryNode(id);
                }
                throw exception;
            }
        }

        protected List<String> queryNodes() throws Exception {
            return source.queryNodes();
        }

        @Override
        protected String searchNode(int hash, byte[] bytes) throws Exception {
            return source.searchNode(hash, bytes);
        }

        @Override
        public void remove(String id) throws Exception {
            source.remove(id);
        }

        @Override
        public boolean contains(String id) throws Exception {
            return source.contains(id);
        }

        @Override
        public long getCreationTime(String id) throws Exception {
            return source.getCreationTime(id);
        }

        @Override
        public long getLastModifiedTime(String id) throws Exception {
            return source.getLastModifiedTime(id);
        }

        private void migrateAllSkins() throws Exception {
            // find all fallback nodes.
            for (var id : fallbackSource.queryNodes()) {
                try {
                    ModLog.debug("Migrate skin '{}' from '{}' into '{}'", id, fallbackSource.name, source.name);
                    var bytes = fallbackSource.queryNode(id);
                    var skin = SkinSerializer.readFromStream(null, new ByteArrayInputStream(bytes));
                    var fileHash = Arrays.hashCode(bytes);
                    // when the target already exists, remove it.
                    if (contains(id)) {
                        source.remove(id);
                    }
                    source.insertNode(id, skin, fileHash, bytes);
                    fallbackSource.remove(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

