package moe.plushie.armourers_workshop.client.skin.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.network.ByteBufHelper;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.utils.SerializeHelper;

public final class FastCache {

    public static final FastCache INSTANCE = new FastCache();

    private static final String VERTEX_EXTENSION = ".vertex";
    private static final String DATA_FILE_NAME = "cache.json";
    private static final Object IO_LOCK = new Object();
    private static final Object FILE_MAP_LOCK = new Object();

    private final LinkedHashMap<String, CacheFile> fileMap = new LinkedHashMap<String, CacheFile>();

    public FastCache() {
        if (!getCacheDirectory().exists()) {
            getCacheDirectory().mkdir();
        }
    }

    private void removeOldFiles() {
        synchronized (FILE_MAP_LOCK) {
            int removeCount = fileMap.size() - ConfigHandlerClient.fastCacheSize;
            if (removeCount > 0) {
                CacheFile[] cacheFiles = fileMap.values().toArray(new CacheFile[fileMap.size()]);
                Arrays.sort(cacheFiles);
                for (int i = 0; i < removeCount; i++) {
                    CacheFile cacheFile = cacheFiles[i];
                    File file = new File(getCacheDirectory(), cacheFile.fileName + VERTEX_EXTENSION);
                    if (file.exists()) {
                        try {
                            file.delete();
                            fileMap.remove(cacheFile.fileName);
                        } catch (Exception e) {
                            // NO-OP
                        }
                    }
                }
            }
        }
    }

    private void saveCacheData() {
        synchronized (FILE_MAP_LOCK) {
            File dataFile = getDataFile();
            JsonArray json = new JsonArray();
            for (CacheFile cacheFile : fileMap.values()) {
                json.add(CacheFile.Storage.serialize(cacheFile));
            }
            SerializeHelper.writeJsonFile(dataFile, StandardCharsets.UTF_8, json);
        }
    }

    public void loadCacheData() {
        synchronized (FILE_MAP_LOCK) {
            fileMap.clear();
            File dataFile = getDataFile();
            if (dataFile.exists()) {
                try {
                    JsonArray json = SerializeHelper.readJsonFile(dataFile, StandardCharsets.UTF_8).getAsJsonArray();
                    for (int i = 0; i < json.size(); i++) {
                        CacheFile cacheFile = CacheFile.Storage.deserialize(json.get(i).getAsJsonObject());
                        if (cacheFile != null) {
                            fileMap.put(cacheFile.fileName, cacheFile);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getCacheDirectory() {
        return new File(ArmourersWorkshop.getProxy().getModDirectory(), "fast-cache");
    }

    private File getDataFile() {
        return new File(getCacheDirectory(), DATA_FILE_NAME);
    }

    public boolean containsFile(ISkinIdentifier identifier) {
        boolean exists;
        synchronized (IO_LOCK) {
            exists = new File(getCacheDirectory(), identifier.hashCode() + VERTEX_EXTENSION).exists();
        }
        return exists;
    }

    public void saveSkin(Skin skin) {
        if (skin.requestId == null) {
            return;
        }
        if (skin.requestId.hasLibraryFile()) {
            return;
        }
        File file = new File(getCacheDirectory(), skin.requestId.hashCode() + VERTEX_EXTENSION);
        synchronized (IO_LOCK) {
            if (!file.exists()) {
                byte[] data = ByteBufHelper.convertSkinToByteArray(skin);
                data = ByteBufHelper.compressedByteArray(data);
                try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream bus = new BufferedOutputStream(fos)) {
                    ArrayUtils.reverse(data);
                    bus.write(data);
                    bus.flush();
                    synchronized (FILE_MAP_LOCK) {
                        CacheFile cacheFile = new CacheFile(String.valueOf(skin.requestId.hashCode()));
                        fileMap.put(cacheFile.fileName, cacheFile);
                    }
                    removeOldFiles();
                    saveCacheData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Skin loadSkin(ISkinIdentifier identifier) {
        if (ConfigHandlerClient.fastCacheSize < 1) {
            return null;
        }
        File file = new File(getCacheDirectory(), identifier.hashCode() + VERTEX_EXTENSION);
        Skin skin = null;
        synchronized (IO_LOCK) {
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
                    byte[] data = IOUtils.toByteArray(bis);
                    ArrayUtils.reverse(data);
                    data = ByteBufHelper.decompressByteArray(data);
                    skin = ByteBufHelper.convertByteArrayToSkin(data);
                    synchronized (FILE_MAP_LOCK) {
                        CacheFile cacheFile = new CacheFile(String.valueOf(identifier.hashCode()));
                        fileMap.put(cacheFile.fileName, cacheFile);
                    }
                    saveCacheData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return skin;
    }

    private static class CacheFile implements Comparable<CacheFile> {

        private final String fileName;
        private Date lastAccess;

        public CacheFile(String fileName) {
            this.fileName = fileName;
            updateLastAccess();
        }
        
        public CacheFile(String fileName, Date lastAccess) {
            this.fileName = fileName;
            this.lastAccess = lastAccess;
        }

        public void setLastAccess(Date lastAccess) {
            this.lastAccess = lastAccess;
        }

        public void updateLastAccess() {
            lastAccess = Calendar.getInstance().getTime();
        }

        @Override
        public int hashCode() {
            return fileName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheFile other = (CacheFile) obj;
            if (fileName == null) {
                if (other.fileName != null)
                    return false;
            } else if (!fileName.equals(other.fileName))
                return false;
            return true;
        }

        @Override
        public int compareTo(CacheFile o) {
            return lastAccess.compareTo(o.lastAccess);
        }

        private static class Storage {

            private static final String TAG_FILENAME = "filename";
            private static final String TAG_LAST_ACCESS = "lastAccess";
            private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.ENGLISH);
            
            public static CacheFile deserialize(JsonElement json) throws JsonParseException {
                String fileName = null;
                Date lastAccess = null;
                fileName = json.getAsJsonObject().get(TAG_FILENAME).getAsString();
                try {
                    lastAccess = SDF.parse(json.getAsJsonObject().get(TAG_LAST_ACCESS).getAsString());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return new CacheFile(fileName, lastAccess);
            }

            public static JsonElement serialize(CacheFile src) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(TAG_FILENAME, src.fileName);
                jsonObject.addProperty(TAG_LAST_ACCESS, SDF.format(src.lastAccess));
                return jsonObject;
            }
        }
    }
}
