package moe.plushie.armourers_workshop.core.network.packet;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
        for (SkinLibraryFile file : readCompressedBuffer(new ByteBufInputStream(buffer))) {
            if (file.getPath().startsWith(AWConstants.PRIVATE)) {
                privateFiles.add(file);
            } else {
                publicFiles.add(file);
            }
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        int totalSize = publicFiles.size() + privateFiles.size();
        writeCompressedBuffer(new ByteBufOutputStream(buffer), Iterables.concat(publicFiles, privateFiles), totalSize);
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        SkinLibraryManager.Client client = SkinLibraryManager.getClient();
        client.getPublicSkinLibrary().reloadFiles(publicFiles);
        client.getPrivateSkinLibrary().reloadFiles(privateFiles);
    }

    private void writeCompressedBuffer(ByteBufOutputStream stream, Iterable<SkinLibraryFile> files, int totalSize) {
        try {
            stream.writeInt(totalSize);
            GZIPOutputStream go = new GZIPOutputStream(stream);
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
                oo.writeUTF(file.getPath());
                oo.writeByte(values.size());
                for (String value : values) {
                    oo.writeUTF(value);
                }
            }
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Iterable<SkinLibraryFile> readCompressedBuffer(ByteBufInputStream stream) {
        ArrayList<SkinLibraryFile> files = new ArrayList<>();
        try {
            int totalSize = stream.readInt();
            GZIPInputStream gi = new GZIPInputStream(stream);
            ObjectInputStream oi = new ObjectInputStream(gi);
            for (int index = 0; index < totalSize; ++index) {
                String path = oi.readUTF();
                String basename = SkinFileUtils.getBaseName(path);
                if (oi.readByte() == 0) {
                    files.add(new SkinLibraryFile(DataDomain.DEDICATED_SERVER, basename, path));
                    continue;
                }
                ISkinType skinType = SkinTypes.byName(oi.readUTF());
                ISkinProperties properties = new SkinProperties();
                properties.put(SkinProperty.ALL_CUSTOM_NAME, oi.readUTF());
                properties.put(SkinProperty.ALL_AUTHOR_NAME, oi.readUTF());
                properties.put(SkinProperty.ALL_FLAVOUR_TEXT, oi.readUTF());
                files.add(new SkinLibraryFile(DataDomain.DEDICATED_SERVER, basename, path, Pair.of(skinType, properties)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
