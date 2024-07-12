package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.skin.ISkinFileManager;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.SkinUUID;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class LocalDataService implements ISkinFileManager {

    private static final int NODE_DATA_VERSION = 2;
    private static final int REFERENCE_DATA_VERSION = 1;

    private static LocalDataService RUNNING;

    private final File nodeRootPath;
    private final File nodeInfoRootPath;

    private final Map<String, Node> nodes = new ConcurrentHashMap<>();
    private final Map<String, NodeInfo> nodeInfos = new ConcurrentHashMap<>();

    private final ExecutorService thread = ThreadUtils.newFixedThreadPool(1, "AW-SKIN-IO");

    private String lastGenUUID = "";

    public LocalDataService(File rootPath) {
        this.nodeRootPath = new File(rootPath, "objects");
        this.nodeInfoRootPath = new File(rootPath, "references");
        this.loadNodes();
        this.loadNodeInfos();
    }

    public static LocalDataService getInstance() {
        return Objects.requireNonNull(RUNNING);
    }

    public static void start(File path) {
        if (RUNNING == null) {
            RUNNING = new LocalDataService(path);
            ModLog.info("start local service of '{}'", path.getParentFile().getName());
        }
    }

    public static void stop() {
        if (RUNNING != null) {
            RUNNING.thread.shutdown();
            RUNNING = null;
            ModLog.info("stop local service");
        }
    }

    public static boolean isRunning() {
        return RUNNING != null;
    }

    private void loadNodes() {
        // objects/<skin-id>/0|1
        for (var file : SkinFileUtils.listFiles(nodeRootPath)) {
            loadNode(file);
        }
    }

    private Node loadNode(File parent) {
        try {
            File indexFile = new File(parent, "0");
            CompoundTag tag = SkinFileUtils.readNBT(indexFile);
            if (tag != null) {
                Node node = new Node(tag);
                nodes.put(node.id, node);
                return node;
            }
            Node node = generateNode(parent.getName(), new File(parent, "1"));
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
        byte[] bytes = SkinFileUtils.readFileToByteArray(skinFile);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        Skin skin = SkinFileStreamUtils.loadSkinFromStream2(stream);
        if (skin == null) {
            return null;
        }
        Node node = new Node(identifier, skin.getType(), bytes, skin.getProperties());
        node.save(new ByteArrayInputStream(bytes));
        return node;
    }

    private void loadNodeInfos() {
        // references/<domain>/<skin-id>
        for (var parentFile : SkinFileUtils.listFiles(nodeInfoRootPath)) {
            for (var infoFile : SkinFileUtils.listFiles(parentFile)) {
                loadNodeInfo(parentFile.getName(), infoFile);
            }
        }
    }

    private void loadNodeInfo(String namespace, File referenceFile) {
        try {
            var tag = SkinFileUtils.readNBT(referenceFile);
            if (tag != null) {
                String id = namespace + ":" + referenceFile.getName();
                nodeInfos.put(id, new NodeInfo(tag));
            }
        } catch (Exception e) {
            ModLog.error("can't load file: {}, pls try fix or remove it.", referenceFile);
        }
    }

    @Nullable
    public LocalDataReference getSkinInfo(String identifier) {
        return nodeInfos.get(identifier);
    }

    public LocalDataReference addSkinInfo(String identifier, Skin skin) {
        ModLog.debug("Save skin info into db {}", identifier);
        var info = new NodeInfo(identifier, skin);
        nodeInfos.put(identifier, info);
        info.save();
        return info;
    }

    public void removeSkinInfo(String identifier) {
        var reference = nodeInfos.remove(identifier);
        ModLog.debug("Remove skin info from db {}", identifier);
        if (reference != null) {
            reference.remove();
        }
    }

    public String saveSkinFile(Skin skin) {
        // save file first.
        ByteArrayOutputStream stream = new ByteArrayOutputStream(5 * 1024);
        SkinFileStreamUtils.saveSkinToStream(stream, skin);
        byte[] bytes = stream.toByteArray();
        // check whether the files are the same as those in the db.
        Node newNode = new Node(getFreeUUID(), skin.getType(), bytes, skin.getProperties());
        for (Node node : nodes.values()) {
            if (node.isValid() && node.equals(newNode) && node.equalContents(bytes)) {
                return node.id;
            }
        }
        try {
            saveSkinFile(newNode.id, new ByteArrayInputStream(bytes), newNode);
            nodes.put(newNode.id, newNode);
            return newNode.id;
        } catch (Exception exception) {
            ModLog.error("can't save file: {}, pls try fix or remove it.", newNode.getFile().getParentFile());
        }
        return null;
    }

    @Override
    public void saveSkinFile(String identifier, InputStream inputStream, Object context) throws Exception {
        ModLog.debug("Save skin into db {}", identifier);
        // fast save the skin data.
        if (context instanceof Node node) {
            node.save(inputStream);
            return;
        }
        // we need to read some skin info and check it is valid.
        byte[] bytes = SkinFileUtils.readStreamToByteArray(inputStream);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        Skin skin = SkinFileStreamUtils.loadSkinFromStream2(stream);
        if (skin == null) {
            return;
        }
        // we without checking it again, overwrite it when node exists.
        Node newNode = new Node(identifier, skin.getType(), bytes, skin.getProperties());
        newNode.save(new ByteArrayInputStream(bytes));
        nodes.put(newNode.id, newNode);
    }

    @Override
    public InputStream loadSkinFile(String identifier, Object context) throws Exception {
        ModLog.debug("Load skin from db {}", identifier);
        Node node = nodes.get(identifier);
        if (node == null) {
            // when the identifier not found in the nodes,
            // we will check the file once.
            File parent = new File(nodeRootPath, identifier);
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

    @Override
    public void removeSkinFile(String identifier, Object context) throws Exception {
        ModLog.debug("Remove skin from db {}", identifier);
        Node node = nodes.get(identifier);
        if (node != null) {
            node.remove();
            nodes.remove(node.id);
        }
    }

    private String getFreeUUID() {
        String uuid = lastGenUUID;
        while (uuid.isEmpty() || nodes.containsKey(uuid)) {
            uuid = SkinUUID.randomUUIDString();
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
            this.version = NODE_DATA_VERSION;
            // file
            this.fileSize = bytes.length;
            this.fileHash = Arrays.hashCode(bytes);
            // properties
            this.properties = properties;
            this.propertiesHash = properties.hashCode();
        }

        Node(CompoundTag tag) {
            this.id = tag.getString("UUID");
            this.type = SkinTypes.byName(tag.getString("Type"));
            this.version = tag.getInt("Version");
            // file
            this.fileSize = tag.getInt("FileSize");
            this.fileHash = tag.getInt("FileHash");
            // properties
            this.properties = new SkinProperties();
            this.properties.readFromNBT(tag.getCompound("Properties"));
            this.propertiesHash = tag.getInt("PropertiesHash");
        }

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("UUID", id);
            tag.putString("Type", type.getRegistryName().toString());
            tag.putInt("Version", version);
            // file
            tag.putInt("FileSize", fileSize);
            tag.putInt("FileHash", fileHash);
            // properties
            CompoundTag props = new CompoundTag();
            properties.writeToNBT(props);
            tag.put("Properties", props);
            tag.putInt("PropertiesHash", propertiesHash);
            return tag;
        }

        public void save(InputStream inputStream) {
            thread.execute(() -> {
                try {
                    var skinFile = getFile();
                    var indexFile = getIndexFile();
                    SkinFileUtils.forceMkdirParent(skinFile);
                    FileOutputStream fs = new FileOutputStream(skinFile);
                    SkinFileUtils.transferTo(inputStream, fs);
                    SkinFileUtils.writeNBT(serializeNBT(), indexFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public void remove() {
            thread.execute(() -> {
                var skinFile = getFile();
                SkinFileUtils.deleteQuietly(skinFile.getParentFile());
            });
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node that)) return false;
            return fileSize == that.fileSize && fileHash == that.fileHash && propertiesHash == that.propertiesHash && type.equals(that.type) && properties.equals(that.properties);
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
            return new File(nodeRootPath, id + "/1");
        }

        public File getIndexFile() {
            return new File(nodeRootPath, id + "/0");
        }

        public boolean isValid() {
            return getFile().exists();
        }
    }

    public class NodeInfo extends LocalDataReference {

        protected final int version;

        public NodeInfo(String id, Skin skin) {
            super(id, skin);
            this.version = REFERENCE_DATA_VERSION;
        }

        public NodeInfo(CompoundTag tag) {
            super(tag);
            this.version = tag.getInt("Version");
        }

        @Override
        public CompoundTag serializeNBT() {
            var tag = super.serializeNBT();
            tag.putInt("Version", version);
            return tag;
        }

        public void save() {
            thread.execute(() -> {
                try {
                    var infoFile = getFile();
                    SkinFileUtils.forceMkdirParent(infoFile);
                    SkinFileUtils.writeNBT(serializeNBT(), infoFile);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
        }

        public void remove() {
            thread.execute(() -> {
                var infoFile = getFile();
                SkinFileUtils.deleteQuietly(infoFile);
            });
        }

        private File getFile() {
            return new File(nodeInfoRootPath, id.replace(':', '/'));
        }
    }
}
