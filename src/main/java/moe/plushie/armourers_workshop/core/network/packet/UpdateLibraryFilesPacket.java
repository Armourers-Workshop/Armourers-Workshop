package moe.plushie.armourers_workshop.core.network.packet;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UpdateLibraryFilesPacket extends CustomPacket {

    private final ArrayList<SkinLibraryFile> publicFiles;
    private final ArrayList<SkinLibraryFile> privateFiles;

    public UpdateLibraryFilesPacket(ArrayList<SkinLibraryFile> publicFiles, ArrayList<SkinLibraryFile> privateFiles) {
        this.publicFiles = publicFiles;
        this.privateFiles = privateFiles;
    }

    public UpdateLibraryFilesPacket(PacketBuffer buffer) {
        this.publicFiles = new ArrayList<>();
        this.privateFiles = new ArrayList<>();
        for (SkinLibraryFile file : readCompressedBuffer(buffer)) {
            if (file.getPath().startsWith("/private")) {
                privateFiles.add(file);
            } else {
                publicFiles.add(file);
            }
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        writeCompressedBuffer(buffer, Iterables.concat(publicFiles, privateFiles));
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        SkinLibraryManager.Client client = SkinLibraryManager.getClient();
        client.getPublicSkinLibrary().reloadFiles(publicFiles);
        client.getPrivateSkinLibrary().reloadFiles(privateFiles);
    }

    private void writeCompressedBuffer(PacketBuffer buffer, Iterable<SkinLibraryFile> files) {
        try {
            int totalSize = 0;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            GZIPOutputStream go = new GZIPOutputStream(bo);
            ObjectOutputStream oo = new ObjectOutputStream(go);
            for (SkinLibraryFile file : files) {
                ArrayList<String> values = new ArrayList<>();
                ISkinProperties properties = file.getSkinProperties();
                if (properties != null) {
                    values.add(file.getSkinType().toString());
                    values.add(properties.get(SkinProperty.ALL_CUSTOM_NAME));
                    values.add(properties.get(SkinProperty.ALL_AUTHOR_NAME));
                    values.add(properties.get(SkinProperty.ALL_FLAVOUR_TEXT));
                }
                totalSize += 1;
                oo.writeUTF(file.getPath());
                oo.writeByte(values.size());
                for (String value : values) {
                    oo.writeUTF(value);
                }
            }
            oo.close();
            buffer.writeInt(totalSize);
            buffer.writeByteArray(bo.toByteArray());
        } catch (Exception e) {
            buffer.writeInt(0);
            e.printStackTrace();
        }
    }

    private Iterable<SkinLibraryFile> readCompressedBuffer(PacketBuffer buffer) {
        ArrayList<SkinLibraryFile> files = new ArrayList<>();
        try {
            int totalSize = buffer.readInt();
            ByteArrayInputStream bi = new ByteArrayInputStream(buffer.readByteArray());
            GZIPInputStream gi = new GZIPInputStream(bi);
            ObjectInputStream oi = new ObjectInputStream(gi);
            for (int index = 0; index < totalSize; ++index) {
                String path = oi.readUTF();
                String basename = FilenameUtils.getBaseName(path);
                if (oi.readByte() == 0) {
                    files.add(new SkinLibraryFile("ws", basename, path));
                    continue;
                }
                ISkinType skinType = SkinTypes.byName(oi.readUTF());
                ISkinProperties properties = new SkinProperties();
                properties.put(SkinProperty.ALL_CUSTOM_NAME, oi.readUTF());
                properties.put(SkinProperty.ALL_AUTHOR_NAME, oi.readUTF());
                properties.put(SkinProperty.ALL_FLAVOUR_TEXT, oi.readUTF());
                files.add(new SkinLibraryFile("ws", basename, path, Pair.of(skinType, properties)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
