package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import net.minecraft.world.entity.player.Player;

public class SkinLibrarySetting implements IDataSerializable.Immutable {

    public static final SkinLibrarySetting DEFAULT = new SkinLibrarySetting();

    public static final IDataCodec<SkinLibrarySetting> CODEC = IDataCodec.COMPOUND_TAG.serializer(SkinLibrarySetting::new);

    private final int flags;
    private final String publicKey;

    public SkinLibrarySetting() {
        this.flags = 0x0f;
        this.publicKey = null;
    }

    public SkinLibrarySetting(Player player) {
        int flags = 0;
        var manager = SkinLibraryManager.getServer();
        if (manager.shouldUploadFile(player)) {
            flags |= 0x01;
        }
        if (manager.shouldDownloadFile(player)) {
            flags |= 0x02;
        }
        if (manager.shouldMaintenanceFile(player)) {
            flags |= 0x04;
        }
        this.flags = flags;
        this.publicKey = manager.publicKey();
    }

    public SkinLibrarySetting(IDataSerializer serializer) {
        this.flags = serializer.read(CodingKeys.FLAGS);
        this.publicKey = serializer.read(CodingKeys.PUBLIC_KEY);
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.FLAGS, flags);
        serializer.write(CodingKeys.PUBLIC_KEY, publicKey);
    }

    public boolean allowsUpload() {
        return (flags & 0x01) != 0;
    }

    public boolean allowsDownload() {
        return (flags & 0x02) != 0;
    }

    public boolean allowsMaintenance() {
        return (flags & 0x04) != 0;
    }

    public String publicKey() {
        return publicKey;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<Integer> FLAGS = IDataSerializerKey.create("Flags", IDataCodec.INT, 0);
        public static final IDataSerializerKey<String> PUBLIC_KEY = IDataSerializerKey.create("PublicKey", IDataCodec.STRING, null);
    }
}
