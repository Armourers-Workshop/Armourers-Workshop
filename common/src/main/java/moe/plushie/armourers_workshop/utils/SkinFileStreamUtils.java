package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.skin.ISkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModLog;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class SkinFileStreamUtils {

    public static void saveSkinToStream(OutputStream outputStream, Skin skin) throws Exception {
        saveSkinToStream(outputStream, skin, null);
    }

    public static void saveSkinToStream(OutputStream outputStream, Skin skin, SkinFileOptions options) throws Exception {
        try (var bos = new BufferedOutputStream(outputStream); var dos = new DataOutputStream(bos)) {
            SkinSerializer.writeToStream(skin, dos, options);
            dos.flush();
            bos.flush();
        }
    }

    public static Skin loadSkinFromStream(InputStream inputStream) throws Exception {
        return loadSkinFromStream(inputStream, null);
    }

    public static Skin loadSkinFromStream(InputStream inputStream, SkinFileOptions options) throws Exception {
        try (var bis = new BufferedInputStream(inputStream); var dis = new DataInputStream(bis)) {
            return SkinSerializer.readSkinFromStream(dis, options);
        }
    }

    @Nullable
    public static ISkinFileHeader readHeaderFromFile(File file) {
        DataInputStream stream = null;
        ISkinFileHeader header = null;
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            header = SkinSerializer.readSkinInfoFromStream(stream);
        } catch (IOException e) {
            ModLog.error("{} in {}", e, file.getName());
        } catch (Exception e) {
            ModLog.error("Unable to load skin name. Unknown error.");
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(stream);
        }

        return header;
    }

    public static String makeFileNameValid(String fileName) {
        fileName = fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
        return fileName;
    }
}
