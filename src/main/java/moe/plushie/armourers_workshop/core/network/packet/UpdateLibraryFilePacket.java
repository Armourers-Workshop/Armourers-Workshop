package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import org.apache.commons.io.FilenameUtils;

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

    public UpdateLibraryFilePacket(PacketBuffer buffer) {
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
    public void encode(PacketBuffer buffer) {
        buffer.writeEnum(mode);
        if ((mode.flag & 1) != 0) {
            buffer.writeUtf(destination);
        }
        if ((mode.flag & 2) != 0) {
            buffer.writeUtf(source);
        }
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        String playerName = player.getName().getContents();
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

    private String decodePath(PacketBuffer buffer) {
        String path = FilenameUtils.normalize(buffer.readUtf(), true); // security check
        if (path != null) {
            return path;
        }
        return "";
    }

    private Optional<SkinLibraryFile> getFile(String path) {
        return Optional.of(SkinLibraryManager.getServer().getLibrary().get(path));
    }

    private boolean isAuthorized(PlayerEntity player) {
        if (destination.isEmpty()) {
            return false;
        }
        String key = "/private/" + player.getStringUUID();
        if (destination.startsWith(key) && source.startsWith(key)) {
            return true;
        }
        return SkinLibraryManager.getServer().shouldModifierFile(player);
    }

    public enum Mode {
        RELOAD(0), MKDIR(1), RENAME(3), DELETE(1);

        final int flag;

        Mode(int flag) {
            this.flag = flag;
        }
    }
}
