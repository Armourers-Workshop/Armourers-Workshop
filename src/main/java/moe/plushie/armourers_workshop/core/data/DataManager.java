package moe.plushie.armourers_workshop.core.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.utils.ResultHandler;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private final ExecutorService executor = Executors.newFixedThreadPool(1, r -> {
        Thread thread = new Thread(r, "AW-SKIN-DM");
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    public static DataManager getInstance() {
        return INSTANCE;
    }

//    @OnlyIn(Dist.CLIENT)
//    public void addCache(String identifier, Skin skin) {
//        File cachedFile = getSkinCacheFile(identifier);
//        byte[] x0 = ModContext.x0();
//        byte[] x1 = ModContext.x1();
//        if (cachedFile == null || x0 == null || x1 == null) {
//            return;
//        }
//        ModLog.debug("add cache of '{}'", identifier);
//        executor.execute(() -> {
//            FileOutputStream fileOutputStream = null;
//            CipherOutputStream cipherOutputStream = null;
//            GZIPOutputStream gzipOutputStream = null;
//            try {
//                FileUtils.forceMkdirParent(cachedFile);
//                if (cachedFile.exists()) {
//                    FileUtils.forceDelete(cachedFile);
//                }
//                fileOutputStream = new FileOutputStream(cachedFile);
//                if (x1.length != 0) {
//                    fileOutputStream.write(x0);
//                    SecretKeySpec key = new SecretKeySpec(x1, "AES");
//                    Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
//                    aes.init(Cipher.ENCRYPT_MODE, key);
//                    cipherOutputStream = new CipherOutputStream(fileOutputStream, aes);
//                    gzipOutputStream = new GZIPOutputStream(cipherOutputStream);
//                    SkinIOUtils.saveSkinToStream(gzipOutputStream, skin);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            IOUtils.closeQuietly(gzipOutputStream, cipherOutputStream, fileOutputStream);
//        });
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public Skin getCache(String identifier) {
//        File cachedFile = getSkinCacheFile(identifier);
//        if (cachedFile == null || !cachedFile.exists()) {
//            return null;
//        }
//        byte[] x0 = ModContext.x0();
//        byte[] x1 = ModContext.x1();
//        if (x0 == null || x1 == null) {
//            return null;
//        }
//        Skin skin = null;
//        FileInputStream fileInputStream = null;
//        CipherInputStream cipherInputStream = null;
//        GZIPInputStream gzipInputStream = null;
//        try {
//            fileInputStream = new FileInputStream(cachedFile);
//            byte[] target = new byte[x0.length];
//            int targetSize = fileInputStream.read(target, 0, target.length);
//            if (targetSize == x0.length && Arrays.equals(x0, target)) {
//                SecretKeySpec key = new SecretKeySpec(x1, "AES");
//                Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
//                aes.init(Cipher.DECRYPT_MODE, key);
//                cipherInputStream = new CipherInputStream(fileInputStream, aes);
//                gzipInputStream = new GZIPInputStream(cipherInputStream);
//                skin = SkinIOUtils.loadSkinFromStream(gzipInputStream);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        IOUtils.closeQuietly(gzipInputStream, cipherInputStream, fileInputStream);
//        return skin;
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public void removeCache(String identifier) {
//        File cachedFile = getSkinCacheFile(identifier);
//        if (cachedFile == null || !cachedFile.exists()) {
//            return;
//        }
//        ModLog.debug("remove cache of '{}'", identifier);
//        executor.execute(() -> {
//            FileUtils.deleteQuietly(cachedFile);
//        });
//    }

    public Optional<ByteBuf> loadSkinData(String identifier) {
        ModLog.debug("Load skin data: {} ", identifier);
        try {
            InputStream stream;
            if (DataDomain.isDatabase(identifier)) {
                stream = LocalDataService.getInstance().getFile(identifier.substring(3));
            } else {
                String path = identifier;
                if (DataDomain.isServer(path) || DataDomain.isLocal(path)) {
                    path = FilenameUtils.normalize(path.substring(3));
                }
                stream = loadStreamFromPath(path);
            }
            if (stream == null) {
                return Optional.empty();
            }
            int size = stream.available();
            ByteBuf buf = Unpooled.buffer(size);
            buf.writeBytes(stream, size);
            buf.resetReaderIndex();
            return Optional.of(buf);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public InputStream loadSkinData3(String identifier) throws IOException {
        ModLog.debug("'{}' => get skin input stream from data manager", identifier);
        if (DataDomain.isDatabase(identifier)) {
            String path = DataDomain.getPath(identifier);
            return LocalDataService.getInstance().getFile(path);
        } else {
            String path = FilenameUtils.normalize(DataDomain.getPath(identifier));
            return loadStreamFromPath(path);
        }
    }

    public void loadSkinData3(String identifier, ResultHandler<InputStream> handler) {
        executor.submit(() -> {
            try {
                handler.accept(loadSkinData3(identifier));
            } catch (Exception exception) {
                handler.reject(exception);
            }
        });
    }

    private InputStream loadStreamFromPath(String identifier) throws IOException {
        File file = new File(AWCore.getSkinLibraryDirectory(), identifier);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        file = new File(AWCore.getSkinLibraryDirectory(), identifier + AWConstants.EXT);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        throw new FileNotFoundException(identifier);
    }

//    private File getSkinCacheFile(String identifier) {
//        File rootPath = getSkinCacheDirectory();
//        if (rootPath != null) {
//            String namespace = DataDomain.getNamespace(identifier);
//            String path = DataDomain.getPath(identifier);
//            return new File(rootPath, namespace + "/" + ModContext.md5(path) + ".dat");
//        }
//        return null;
//    }
//
//    private File getSkinCacheDirectory() {
//        UUID t0 = ModContext.t0();
//        if (t0 != null) {
//            return new File(AWCore.getSkinCacheDirectory(), t0.toString());
//        }
//        return null;
//    }
}


