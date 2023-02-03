package moe.plushie.armourers_workshop.core.data;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.SkinUUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class LocalDataService {

    private static LocalDataService RUNNING;

    private final Path rootPath;
    private final HashMap<String, Node> nodes = new HashMap<>();
    private String lastGenUUID = "";

    public LocalDataService(Path rootPath) {
        this.rootPath = rootPath;
        // data migration for the internal-test version, and will be removed in later versions.
        this.loadLegacyNodes();
        this.loadNodes();
    }

    public static LocalDataService getInstance() {
        return Objects.requireNonNull(RUNNING);
    }

    public static void start(MinecraftServer server) {
        if (RUNNING == null) {
            RUNNING = new LocalDataService(server.getWorldPath(Constants.Folder.LOCAL_DB));
            ModLog.info("start local service of '{}'", server.getWorldData().getLevelName());
        }
    }

    public static void stop() {
        if (RUNNING != null) {
            RUNNING = null;
            ModLog.info("stop local service");
        }
    }

    public static boolean isRunning() {
        return RUNNING != null;
    }

    protected void loadLegacyNodes() {
        File indexDB = rootPath.resolve("index.dat").toFile();
        if (!indexDB.exists()) {
            return;
        }
        File[] files = SkinFileUtils.listFiles(rootPath.toFile());
        if (files == null) {
            return;
        }
        ModLog.info("data fixer for db {} started", indexDB);
        for (File file : files) {
            String name = file.getName();
            if (name.equals("objects") || name.equals("index.dat")) {
                continue;
            }
            try {
                Node node = generateNode(name, file);
                if (node != null) {
                    ModLog.info("data fixer -> upgrade {} node to new db", name);
                    SkinFileUtils.deleteQuietly(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SkinFileUtils.deleteQuietly(indexDB);
        ModLog.info("data fixer for db {} completed", indexDB);
    }

    private void loadNodes() {
        File[] files = SkinFileUtils.listFiles(getRootFile());
        if (files != null) {
            for (File file : files) {
                loadNode(file);
            }
        }
    }

    private Node loadNode(File parent) {
        try {
            File indexFile = new File(parent, "0");
            CompoundTag nbt = SkinFileUtils.readNBT(indexFile);
            if (nbt != null) {
                Node node = new Node(nbt);
                nodes.put(node.id, node);
                return node;
            }
            Node node = generateNode(parent.getName(), new File(parent, "1"));
            if (node != null) {
                nodes.put(node.id, node);
                return node;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Node generateNode(String identifier, File skinFile) throws Exception {
       if (!skinFile.isFile()) {
            return null;
        }
        byte[] bytes = SkinFileUtils.readFileToByteArray(skinFile);
        FastByteArrayInputStream stream = new FastByteArrayInputStream(bytes);
        Skin skin = SkinIOUtils.loadSkinFromStream2(stream);
        if (skin == null) {
            return null;
        }
        Node node = new Node(identifier, skin.getType(), bytes, skin.getProperties());
        node.save(bytes);
        return node;
    }

    private File getRootFile() {
        return rootPath.resolve("objects").toFile();
    }

    public InputStream getFile(String identifier) throws IOException {
        Node node = nodes.get(identifier);
        if (node == null) {
            // when the identifier not found in the nodes,
            // we will check the file once.
            File parent = new File(getRootFile(), identifier);
            if (parent.isDirectory()) {
                node = loadNode(parent);
            }
        }
        if (node != null && node.isValid()) {
            // we can safely access the node now.
            return new FileInputStream(node.getFile());
        }
        throw new FileNotFoundException("the node '" + identifier + "' not found!");
    }

    public String addFile(Skin skin) {
        // save file first.
        FastByteArrayOutputStream stream = new FastByteArrayOutputStream(5 * 1024);
        SkinIOUtils.saveSkinToStream(stream, skin);
        // check whether the files are the same as those in the db.
        Node tmp = new Node(getFreeUUID(), skin.getType(), stream.array, skin.getProperties());
        for (Node node : nodes.values()) {
            if (node.isValid() && node.equals(tmp) && node.equalContents(stream.array)) {
                return node.id;
            }
        }
        ModLog.debug("Save skin into db {}", tmp.id);
        try {
            tmp.save(stream.array);
            nodes.put(tmp.id, tmp);
            return tmp.id;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void removeFile(String identifier) {
        Node node = nodes.get(identifier);
        if (node != null) {
            node.remove();
            nodes.remove(node.id);
        }
    }

    private String getFreeUUID() {
        String uuid = lastGenUUID;
        while (uuid.isEmpty() || nodes.containsKey(uuid)) {
            uuid = SkinUUID.randomUUID().toString();
        }
        lastGenUUID = uuid;
        return uuid;
    }

    public class Node {

        final String id;
        final ISkinType type;
        final int version;

        final int fileSize;
        final int fileHash;

        final SkinProperties properties;
        final int propertiesHash;

        Node(String id, ISkinType type, byte[] bytes, SkinProperties properties) {
            this.id = id;
            this.type = type;
            this.version = 2;
            // file
            this.fileSize = bytes.length;
            this.fileHash = Arrays.hashCode(bytes);
            // properties
            this.properties = properties;
            this.propertiesHash = properties.hashCode();
        }

        Node(CompoundTag nbt) {
            this.id = nbt.getString("UUID");
            this.type = SkinTypes.byName(nbt.getString("Type"));
            this.version = nbt.getInt("Version");
            // file
            this.fileSize = nbt.getInt("FileSize");
            this.fileHash = nbt.getInt("FileHash");
            // properties
            this.properties = SkinProperties.create();
            this.properties.readFromNBT(nbt.getCompound("Properties"));
            this.propertiesHash = nbt.getInt("PropertiesHash");
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("UUID", id);
            nbt.putString("Type", type.toString());
            nbt.putInt("Version", version);
            // file
            nbt.putInt("FileSize", fileSize);
            nbt.putInt("FileHash", fileHash);
            // properties
            CompoundTag props = new CompoundTag();
            properties.writeToNBT(props);
            nbt.put("Properties", props);
            nbt.putInt("PropertiesHash", propertiesHash);
            return nbt;
        }

        public void save(byte[] bytes) throws IOException {
            SkinFileUtils.forceMkdirParent(getFile());
            FileOutputStream fs = new FileOutputStream(getFile());
            fs.write(bytes);
            SkinFileUtils.writeNBT(serializeNBT(), getIndexFile());
        }

        public void remove() {
            SkinFileUtils.deleteQuietly(getFile().getParentFile());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return fileSize == node.fileSize && fileHash == node.fileHash && propertiesHash == node.propertiesHash && type.equals(node.type) && properties.equals(node.properties);
        }

        public boolean equalContents(byte[] bytes) {
            int index = 0;
            try (FileInputStream stream = new FileInputStream(getFile())) {
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
            return Objects.hash(type, fileSize, fileHash, propertiesHash);
        }

        public File getFile() {
            return rootPath.resolve("objects/" + id + "/1").toFile();
        }

        public File getIndexFile() {
            return rootPath.resolve("objects/" + id + "/0").toFile();
        }

        public boolean isValid() {
            return getFile().exists();
        }
    }
}
