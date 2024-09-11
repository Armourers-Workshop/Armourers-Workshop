package moe.plushie.armourers_workshop.library.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SkinLibrarySetting {

    public static final SkinLibrarySetting DEFAULT = new SkinLibrarySetting();

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
        this.publicKey = manager.getPublicKey();
    }

    public SkinLibrarySetting(CompoundTag tag) {
        this.flags = tag.getOptionalInt("Flags", 0);
        this.publicKey = tag.getOptionalString("PublicKey", null);
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

    public String getPublicKey() {
        return publicKey;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putOptionalInt("Flags", flags, 0);
        tag.putOptionalString("PublicKey", publicKey, null);
        return tag;
    }
}
