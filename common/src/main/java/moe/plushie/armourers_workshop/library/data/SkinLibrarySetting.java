package moe.plushie.armourers_workshop.library.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SkinLibrarySetting {

    public static final SkinLibrarySetting DEFAULT = new SkinLibrarySetting();

    private final int flags;
    private final String token;

    public SkinLibrarySetting() {
        this.flags = 0x0f;
        this.token = null;
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
        this.token = manager.getToken();
    }

    public SkinLibrarySetting(CompoundTag tag) {
        this.flags = tag.getOptionalInt("Flags", 0);
        this.token = tag.getOptionalString("Token", null);
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

    public String getToken() {
        return token;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putOptionalInt("Flags", flags, 0);
        tag.putOptionalString("Token", token, null);
        return tag;
    }
}
