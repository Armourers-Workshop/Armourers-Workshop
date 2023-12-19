package moe.plushie.armourers_workshop.library.network;

import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.permission.BlockPermission;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class UpdateLibraryFilePacket extends CustomPacket {

    private final Mode mode;
    private final String source;
    private final String destination;

    public UpdateLibraryFilePacket(Mode mode, String source, String destination) {
        this.mode = mode;
        this.source = source;
        this.destination = destination;
    }

    public UpdateLibraryFilePacket(FriendlyByteBuf buffer) {
        this.mode = buffer.readEnum(Mode.class);
        if ((mode.flag & 1) != 0) {
            this.destination = decodePath(buffer);
        } else {
            this.destination = "";
        }
        if ((mode.flag & 2) != 0) {
            this.source = decodePath(buffer);
        } else {
            this.source = destination;
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(mode);
        if ((mode.flag & 1) != 0) {
            buffer.writeUtf(destination);
        }
        if ((mode.flag & 2) != 0) {
            buffer.writeUtf(source);
        }
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        String playerName = player.getScoreboardName();
        if (!mode.permission.accept(player)) {
            return;
        }
        SkinLibrary library = SkinLibraryManager.getServer().getLibrary();
        if (mode == Mode.RELOAD) {
            library.reload();
            return;
        }
        if (!isAuthorized(player)) {
            ModLog.info("the {} operation rejected for '{}', dest: '{}'", mode, playerName, destination);
            return;
        }
        ModLog.info("the {} operation accepted for '{}', dest: '{}'", mode, playerName, destination);
        switch (mode) {
            case MKDIR: {
                library.mkdir(destination);
                break;
            }
            case RENAME: {
                getFile(source).ifPresent(file -> library.rename(file, destination));
                break;
            }
            case DELETE: {
                getFile(source).ifPresent(library::delete);
                break;
            }
        }
    }

    private String decodePath(FriendlyByteBuf buffer) {
        String path = SkinFileUtils.normalize(buffer.readUtf(Short.MAX_VALUE), true); // security check
        if (path != null) {
            return path;
        }
        return "";
    }

    private Optional<SkinLibraryFile> getFile(String path) {
        return Optional.of(SkinLibraryManager.getServer().getLibrary().get(path));
    }

    private boolean isAuthorized(Player player) {
        if (destination.isEmpty()) {
            return false;
        }
        String key = "/private/" + player.getStringUUID();
        if (destination.startsWith(key) && source.startsWith(key)) {
            return true;
        }
        return SkinLibraryManager.getServer().shouldMaintenanceFile(player);
    }

    public enum Mode {
        RELOAD(0, ModPermissions.SKIN_LIBRARY_RELOAD),
        MKDIR(1, ModPermissions.SKIN_LIBRARY_MKDIR),
        RENAME(3, ModPermissions.SKIN_LIBRARY_RENAME),
        DELETE(1, ModPermissions.SKIN_LIBRARY_DELETE);

        final int flag;
        final BlockPermission permission;

        Mode(int flag, BlockPermission permission) {
            this.flag = flag;
            this.permission = permission;
        }
    }
}
