package moe.plushie.armourers_workshop.library.network;

import com.google.common.collect.Iterables;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.data.SkinLibrarySetting;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UpdateLibraryFilesPacket extends CustomPacket {

    private final ArrayList<SkinLibraryFile> publicFiles;
    private final ArrayList<SkinLibraryFile> privateFiles;
    private SkinLibrarySetting setting;

    public UpdateLibraryFilesPacket(ArrayList<SkinLibraryFile> publicFiles, ArrayList<SkinLibraryFile> privateFiles, SkinLibrarySetting setting) {
        this.setting = setting;
        this.publicFiles = publicFiles;
        this.privateFiles = privateFiles;
    }

    public UpdateLibraryFilesPacket(FriendlyByteBuf buffer) {
        this.setting = new SkinLibrarySetting(buffer.readNbt());
        this.publicFiles = new ArrayList<>();
        this.privateFiles = new ArrayList<>();
        for (SkinLibraryFile file : readCompressedBuffer(new ByteBufInputStream(buffer))) {
            if (file.getPath().startsWith(Constants.PRIVATE)) {
                privateFiles.add(file);
            } else {
                publicFiles.add(file);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        int totalSize = publicFiles.size() + privateFiles.size();
        buffer.writeNbt(setting.serializeNBT());
        writeCompressedBuffer(new ByteBufOutputStream(buffer), Iterables.concat(publicFiles, privateFiles), totalSize);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        SkinLibraryManager.Client client = SkinLibraryManager.getClient();
        client.setSetting(setting);
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
                if (values.size() != 0) {
                    oo.writeInt(file.getSkinVersion());
                    oo.writeInt(file.getLastModified());
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
                ISkinProperties properties = SkinProperties.create();
                properties.put(SkinProperty.ALL_CUSTOM_NAME, oi.readUTF());
                properties.put(SkinProperty.ALL_AUTHOR_NAME, oi.readUTF());
                properties.put(SkinProperty.ALL_FLAVOUR_TEXT, oi.readUTF());
                int fileVersion = oi.readInt();
                int lastModified = oi.readInt();
                SkinFileHeader header = SkinFileHeader.of(fileVersion, skinType, properties);
                header.setLastModified(lastModified);
                files.add(new SkinLibraryFile(DataDomain.DEDICATED_SERVER, basename, path, header));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
