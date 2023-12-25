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
import net.minecraft.nbt.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class LocalDataService implements ISkinFileManager {

    private static LocalDataService RUNNING;

    private final File rootPath;
    private final HashMap<String, Node> nodes = new HashMap<>();
    private String lastGenUUID = "";

    public LocalDataService(File rootPath) {
        this.rootPath = rootPath;
        this.loadNodes();
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
            RUNNING = null;
            ModLog.info("stop local service");
        }
    }

    public static boolean isRunning() {
        return RUNNING != null;
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

    private File getRootFile() {
        return new File(rootPath, "objects");
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
        if (context instanceof Node) {
            ((Node) context).save(inputStream);
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
            this.properties = new SkinProperties();
            this.properties.readFromNBT(nbt.getCompound("Properties"));
            this.propertiesHash = nbt.getInt("PropertiesHash");
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("UUID", id);
            nbt.putString("Type", type.getRegistryName().toString());
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

        public void save(InputStream inputStream) throws IOException {
            SkinFileUtils.forceMkdirParent(getFile());
            FileOutputStream fs = new FileOutputStream(getFile());
            SkinFileUtils.transferTo(inputStream, fs);
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
            return new File(rootPath, "objects/" + id + "/1");
        }

        public File getIndexFile() {
            return new File(rootPath, "objects/" + id + "/0");
        }

        public boolean isValid() {
            return getFile().exists();
        }
    }
}
