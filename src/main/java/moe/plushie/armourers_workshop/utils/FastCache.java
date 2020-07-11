package moe.plushie.armourers_workshop.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.network.ByteBufHelper;
import moe.plushie.armourers_workshop.common.skin.data.Skin;

public final class FastCache {

    public static final FastCache INSTANCE = new FastCache();

    private static final String VERTEX_EXTENSION = ".vertex";
    private static final Object IO_LOCK = new Object();

    public FastCache() {
        if (!getCacheDirectory().exists()) {
            getCacheDirectory().mkdir();
        }
    }

    private File getCacheDirectory() {
        return new File(ArmourersWorkshop.getProxy().getModDirectory(), "fast-cache");
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
                    bus.write(data);
                    bus.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Skin loadSkin(ISkinIdentifier identifier) {
        File file = new File(getCacheDirectory(), identifier.hashCode() + VERTEX_EXTENSION);
        Skin skin = null;
        synchronized (IO_LOCK) {
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
                    byte[] data = IOUtils.toByteArray(bis);
                    ArrayUtils.reverse(data);
                    data = ByteBufHelper.decompressByteArray(data);
                    skin = ByteBufHelper.convertByteArrayToSkin(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return skin;
    }
}
