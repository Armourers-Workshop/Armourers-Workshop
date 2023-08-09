package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.skin.ISkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.init.ModLog;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class SkinIOUtils {

    public static boolean saveSkinToFile(File file, Skin skin) {
        ModLog.debug("save skin into '{}'", file);
        try {
            SkinFileUtils.forceMkdirParent(file);
            if (file.exists()) {
                SkinFileUtils.deleteQuietly(file);
            }
            FileOutputStream fos = new FileOutputStream(file);
            saveSkinToStream(fos, skin);
            fos.close();
        } catch (FileNotFoundException e) {
            ModLog.warn("skin file not found.");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            ModLog.error("skin file save failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveSkinToStream(OutputStream outputStream, Skin skin) {
        try (BufferedOutputStream bos = new BufferedOutputStream(outputStream); DataOutputStream dos = new DataOutputStream(bos)) {
            SkinSerializer.writeToStream(skin, dos);
            dos.flush();
            bos.flush();
        } catch (IOException e) {
            ModLog.error("Skin file save failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Skin loadSkinFromStream(InputStream inputStream) {
        Skin skin = null;
        try {
            skin = loadSkinFromStream2(inputStream);
        } catch (IOException e) {
            ModLog.error("Skin file load failed.");
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            ModLog.error("Can not load skin file it was saved in newer version.");
            e.printStackTrace();
        } catch (InvalidCubeTypeException e) {
            ModLog.error("Unable to load skin. Unknown cube types found.");
            e.printStackTrace();
        } catch (Exception e) {
            ModLog.error("Unable to load skin. Unknown error.");
            e.printStackTrace();
        }

        return skin;
    }

    public static Skin loadSkinFromStream2(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return null;
        }
        Skin skin = null;
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        DataInputStream dis = new DataInputStream(bis);
        try {
            skin = SkinSerializer.readSkinFromStream(dis);
        } finally {
            StreamUtils.closeQuietly(dis, bis);
        }
        return skin;
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
        } catch (NewerFileVersionException e) {
            ModLog.error("{} in {}", e, file.getName());
        } catch (Exception e) {
            ModLog.error("Unable to load skin name. Unknown error.");
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(stream);
        }

//        if (skinType == null) {
//            Skin skin = loadSkinRecovery(file);
//            if (skin != null) {
//                ModLog.warn("Loaded skin with recovery system.");
//                skinType = Pair.of(skin.getType(), SkinProperties.create());
//            }
//        }

        return header;
    }

    public static String makeFileNameValid(String fileName) {
        fileName = fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
        return fileName;
    }
}
