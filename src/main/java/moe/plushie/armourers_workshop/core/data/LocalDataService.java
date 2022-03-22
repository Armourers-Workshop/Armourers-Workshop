package moe.plushie.armourers_workshop.core.data;

import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalDataService {

    private static LocalDataService RUNNING;

    private final Path rootPath;
    private final HashMap<String, Node> nodes = new HashMap<>();
    private AtomicInteger counter = new AtomicInteger();
    private String lastGenUUID = "";

    public LocalDataService(Path rootPath) {
        this.rootPath = rootPath;
        this.loadConfig();

    }

    public static LocalDataService getInstance() {
        return Objects.requireNonNull(RUNNING);
    }

    public static void start(MinecraftServer server) {
        if (RUNNING == null) {
            RUNNING = new LocalDataService(server.getWorldPath(AWConstants.Folder.LOCAL_DB));
            AWLog.debug("start service: {}", RUNNING.rootPath);
        }
    }

    public static void stop() {
        if (RUNNING != null) {
            RUNNING.saveConfig();
            RUNNING = null;
            AWLog.debug("stop service");
        }
    }

    protected void loadConfig() {
        File rootDir = rootPath.toFile();
        if (!rootDir.exists() && !rootDir.mkdirs()) {
            AWLog.error("Init service config fail {}", rootDir);
            return;
        }
        File indexDB = rootPath.resolve("index.dat").toFile();
        if (!indexDB.exists()) {
            AWLog.debug("Setup new service with {}", indexDB);
            return;
        }
        AWLog.debug("Load service config from {}", indexDB);
        try {
            PushbackInputStream stream = new PushbackInputStream(new FileInputStream(indexDB), 2);
            CompoundNBT nbt = CompressedStreamTools.readCompressed(stream);
            ListNBT listNBT = nbt.getList("Nodes", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listNBT.size(); ++i) {
                Node node = new Node(listNBT.getCompound(i));
                if (node.isValid()) {
                    nodes.put(node.uuid, node);
                }
            }
            counter.set(nbt.getInt("Counter"));

        } catch (IOException e) {
            AWLog.error("Load service config fail {}", indexDB);
        }
    }

    protected void saveConfig() {
        File indexDB = rootPath.resolve("index.dat").toFile();
        AWLog.debug("Save service config into {}", indexDB);
        try {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT listNBT = new ListNBT();
            for (Node node : nodes.values()) {
                if (node.isValid()) {
                    listNBT.add(node.serializeNBT());
                }
            }
            nbt.put("Nodes", listNBT);
            nbt.putInt("Counter", counter.get());
            CompressedStreamTools.writeCompressed(nbt, indexDB);
        } catch (IOException e) {
            AWLog.error("Save service config fail for {}", indexDB);
        }
    }

    public InputStream getFile(String identifier) {
        AWLog.debug("Load data from db {}", identifier);
        Node node = nodes.get(identifier);
        if (node == null || !node.isValid()) {
            return null;
        }
        try {
            return new FileInputStream(rootPath.resolve(node.uuid).toFile());
        } catch (FileNotFoundException ignored) {
        }
        return null;
    }

//    public boolean checkFile(String identifier, byte[] array) {
//        try {
//            FileInputStream fi = new FileInputStream(rootPath.resolve(identifier).toFile());
//            int index = 0;
//            byte[] buff = new byte[1024];
//            while (index < array.length) {
//                int rl = fi.read(buff);
//                if (rl == -1) {
//                    return false;
//                }
//                for (int i = 0; i < rl; ++i) {
//                    if (array[index + i] != buff[i]) {
//                        return false;
//                    }
//                }
//                index += rl;
//            }
//            return true;
//
//        } catch (FileNotFoundException ignored) {
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public String addFile(Skin skin) {
        String name = skin.getCustomName();
        String flavour = skin.getFlavourText();
        String author = skin.getProperties().get(SkinProperty.ALL_AUTHOR_UUID);
        FastByteArrayOutputStream stream = new FastByteArrayOutputStream(5 * 1024);
        SkinIOUtils.saveSkinToStream(stream, skin);
        int length = stream.length;
        int hashCode = Arrays.hashCode(stream.array);
        String uuid = getFreeUUID();
        Node newNode = new Node(uuid, skin.getType().toString(), name, author, flavour, length, hashCode);
        for (Node node : nodes.values()) {
            if (node.sameNode(newNode)) {
                return node.uuid;
            }
        }
        AWLog.debug("Save data from db {}", uuid);
        try {
            FileOutputStream fs = new FileOutputStream(rootPath.resolve(uuid).toFile());
            fs.write(stream.array, 0, length);
            nodes.put(uuid, newNode);
            saveConfig();
            return uuid;
        } catch (IOException ignored) {
        }
        return null;
    }

    public void removeFile(String identifier) {

    }

    private String getFreeUUID() {
        String uuid = lastGenUUID;
        while (uuid.isEmpty() || nodes.containsKey(uuid)) {
            uuid = String.format("%08x", counter.incrementAndGet());
        }
        lastGenUUID = uuid;
        return uuid;
    }

    public class Node {
        final String uuid;
        final String type;
        final String name;
        final String flavour;
        final String author;

        int length;
        int hashCode;

        Node(String uuid, String type, String name, String author, String flavour, int length, int hashCode) {
            this.uuid = uuid;
            this.type = type;
            this.name = name;
            this.author = author;
            this.flavour = flavour;
            this.length = length;
            this.hashCode = hashCode;
        }

        Node(CompoundNBT nbt) {
            this.uuid = nbt.getString("UUID");
            this.type = nbt.getString("Type");
            this.name = nbt.getString("Name");
            this.author = nbt.getString("Author");
            this.flavour = nbt.getString("Flavour");
            this.length = nbt.getInt("Length");
            this.hashCode = nbt.getInt("HashCode");
        }

        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("UUID", uuid);
            nbt.putString("Type", type);
            nbt.putString("Name", name);
            nbt.putString("Author", author);
            nbt.putString("Flavour", flavour);
            nbt.putInt("Length", length);
            nbt.putInt("HashCode", hashCode);
            return nbt;
        }

        public boolean isValid() {
            return rootPath.resolve(uuid).toFile().exists();
        }

        public boolean sameNode(Node node) {
            return node.length == length
                    && node.hashCode == hashCode
                    && node.author.equals(author)
                    && node.flavour.equals(flavour)
                    && node.name.equals(name)
                    && node.type.equals(type);
        }
    }
}
