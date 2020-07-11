package moe.plushie.armourers_workshop.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import moe.plushie.armourers_workshop.common.skin.data.Skin;
import net.minecraft.util.math.MathHelper;

public final class LumpStorage {

    public static final LumpStorage INSTANCE = new LumpStorage();

    // Max size of the lump file. (40MB)
    private final long maxFileSize = 1024 * 1024 * 40;

    // Max size of each lump (20KB)
    private final int lumpSize = 1024 * 20;

    private final HashMap<String, LumpFile> lumpFileMap;
    private final Lump[] lumps;

    public LumpStorage() {
        lumpFileMap = new HashMap<String, LumpFile>();
        lumps = new Lump[2048];
    }

    public boolean containsFile(File file) {
        return false;
    }

    public File[] getFiles() {
        File[] files = lumpFileMap.entrySet().toArray(new LumpFile[lumpFileMap.size()]);
        return files;
    }

    private File getLumpFile() {
        return null;

    }

    private void saveLumpFile(File file, ByteArrayInputStream inputStream) {
        int numberOfLumps = MathHelper.ceil((float) inputStream.available() / (float) lumpSize);
    }

    public void saveData(Skin skin) {
        File file = new File(String.valueOf(skin.lightHash()));
        if (containsFile(file)) {
            return;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            //SkinSerializer.writeToStream(skin, dataOutputStream, false);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveLumpFile(file, new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private class LumpFile extends File {

        private int[] lumps;
        private Date lastAccess;

        public LumpFile(String file) {
            super(file);
        }
    }

    private class Lump {
        private Date lastAccess;

        public Lump() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(1900, Calendar.JANUARY, 1);
            lastAccess = calendar.getTime();
        }
    }
}
