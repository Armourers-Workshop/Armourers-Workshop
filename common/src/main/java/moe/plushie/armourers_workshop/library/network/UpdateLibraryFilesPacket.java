package moe.plushie.armourers_workshop.library.network;

import com.google.common.collect.Iterables;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.data.SkinLibrarySetting;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.world.entity.player.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UpdateLibraryFilesPacket extends CustomPacket {

    private final ArrayList<SkinLibraryFile> publicFiles;
    private final ArrayList<SkinLibraryFile> privateFiles;

    private final SkinLibrarySetting setting;

    public UpdateLibraryFilesPacket(ArrayList<SkinLibraryFile> publicFiles, ArrayList<SkinLibraryFile> privateFiles, SkinLibrarySetting setting) {
        this.setting = setting;
        this.publicFiles = publicFiles;
        this.privateFiles = privateFiles;
    }

    public UpdateLibraryFilesPacket(IFriendlyByteBuf buffer) {
        this.setting = new SkinLibrarySetting(buffer.readNbt());
        this.publicFiles = new ArrayList<>();
        this.privateFiles = new ArrayList<>();
        for (SkinLibraryFile file : readCompressedBuffer(new ByteBufInputStream(buffer.asByteBuf()))) {
            if (file.getPath().startsWith(Constants.PRIVATE)) {
                privateFiles.add(file);
            } else {
                publicFiles.add(file);
            }
        }
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        int totalSize = publicFiles.size() + privateFiles.size();
        buffer.writeNbt(setting.serializeNBT());
        writeCompressedBuffer(new ByteBufOutputStream(buffer.asByteBuf()), Iterables.concat(publicFiles, privateFiles), totalSize);
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
            DataOutputStream dataStream = new DataOutputStream(new GZIPOutputStream(stream));
            IOutputStream outputStream = IOutputStream.of(dataStream);
            for (SkinLibraryFile file : files) {
                SkinProperties properties = ObjectUtils.safeCast(file.getSkinProperties(), SkinProperties.class);
                outputStream.writeString(file.getPath());
                outputStream.writeBoolean(properties == null); // is directory
                if (properties != null) {
                    outputStream.writeType(file.getSkinType());
                    outputStream.writeInt(file.getSkinVersion());
                    outputStream.writeInt(file.getLastModified());
                    properties.writeToStream(outputStream);
                }
            }
            dataStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Iterable<SkinLibraryFile> readCompressedBuffer(ByteBufInputStream stream) {
        ArrayList<SkinLibraryFile> files = new ArrayList<>();
        try {
            int totalSize = stream.readInt();
            DataInputStream dataStream = new DataInputStream(new GZIPInputStream(stream));
            IInputStream inputStream = IInputStream.of(dataStream);
            for (int index = 0; index < totalSize; ++index) {
                String path = inputStream.readString();
                String basename = SkinFileUtils.getBaseName(path);
                if (inputStream.readBoolean()) { // is directory
                    files.add(new SkinLibraryFile(DataDomain.DEDICATED_SERVER, basename, path));
                    continue;
                }
                ISkinType skinType = inputStream.readType(SkinTypes::byName);
                int fileVersion = inputStream.readInt();
                int lastModified = inputStream.readInt();
                SkinProperties properties = new SkinProperties();
                properties.readFromStream(inputStream);
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
