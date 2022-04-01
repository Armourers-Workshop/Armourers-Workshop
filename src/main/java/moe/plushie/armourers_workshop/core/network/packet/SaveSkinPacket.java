package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.container.SkinLibraryContainer;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SaveSkinPacket extends CustomPacket {

    protected final String source;
    protected final String destination;

    protected Skin skin;
    protected Mode mode;

    public SaveSkinPacket(String source, String destination) {
        this.source = source;
        this.destination = destination;
        this.mode = Mode.NONE;
        boolean shouldUpload = isLocalTarget(source) && !isLocalTarget(destination);
        if (shouldUpload) {
            this.mode = Mode.UPLOAD;
            if (SkinLibraryManager.getClient().shouldUploadFile()) {
                this.skin = SkinLoader.getInstance().loadSkin(source);
            }
        }
    }

    public SaveSkinPacket(PacketBuffer buffer) {
        this.source = decodeResourceLocation(buffer);
        this.destination = decodeResourceLocation(buffer);
        this.mode = buffer.readEnum(Mode.class);
        switch (mode) {
            case UPLOAD: {
                boolean shouldUpload = isLocalTarget(source) && !isLocalTarget(destination);
                if (shouldUpload && SkinLibraryManager.getServer().shouldUploadFile()) {
                    this.skin = decodeSkin(buffer);
                }
                break;
            }
            case DOWNLOAD: {
                boolean shouldDownload = !isLocalTarget(source) && isLocalTarget(destination);
                if (shouldDownload) {
                    this.skin = decodeSkin(buffer);
                }
                break;
            }
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(source);
        buffer.writeUtf(destination);
        encodeSkin(buffer);
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        if (!isLocalTarget(destination)) {
            return; // ignore
        }
        Skin skin = getSkin();
        if (skin == null) {
            error(player, "load", "missing skin from the loader");
            return;
        }
        SkinLibrary library = SkinLibraryManager.getClient().getLocalSkinLibrary();
        library.save(getPath(destination), skin);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // this is an unauthorized operation, ignore it
        if (!isAuthorized(source, player) || !isAuthorized(destination, player) || !(player.containerMenu instanceof SkinLibraryContainer)) {
            error(player, "unauthorized", "user status is incorrect or the path is invalid");
            return;
        }
        SkinLibraryManager.Server server = SkinLibraryManager.getServer();
        if (isLocalTarget(source) && !server.shouldUploadFile()) {
            error(player, "upload", "uploading skin prohibited in the config file");
            return;
        }
        SkinLibraryContainer container = (SkinLibraryContainer) player.containerMenu;
        // load: fs -> db[-link]/ws -> db[-link]
        if (isDatabaseTarget(destination)) {
            Skin skin = getSkin();
            if (skin == null) {
                error(player, "load", "missing skin from the loader");
                return;
            }
            if (container.shouldLoadStack()) {
                // TODO: fix db-link
                String identifier = SkinLoader.getInstance().saveSkin(source, skin);
                container.crafting(new SkinDescriptor(identifier, skin.getType()));
                accept(player, "load");
            }
            return;
        }
        // save: fs -> ws/db -> ws/ws -> ws
        if (isServerTarget(destination)) {
            Skin skin = getSkin();
            if (skin == null) {
                error(player, "load", "missing skin from the loader");
                return;
            }
            if (container.shouldSaveStack()) {
                server.getLibrary().save(getPath(destination), skin);
                container.crafting(null);
                accept(player, "save");
            }
            return;
        }
        // download: ws -> fs/db -> fs/fs -> fs
        if (isLocalTarget(destination)) {
            if (!isLocalTarget(source) && !server.shouldDownloadFile()) {
                error(player, "download", "downloading skin prohibited in the config file");
                return;
            }
            if (!isLocalTarget(source)) {
                this.skin = getSkin();
                this.mode = Mode.DOWNLOAD;
            }
            if (container.shouldSaveStack()) {
                // send skin data to client again, except case: fs -> fs
                NetworkHandler.getInstance().sendTo(this, player);
                container.crafting(null);
                accept(player, "download");
            }
            return;
        }
        error(player, "unknown", "dangerous operation");
    }

    public boolean isReady() {
        SkinLibraryManager libraryManager = SkinLibraryManager.getClient();
        // when a remote server, check the config.
        if (isLocalTarget(source) && !isLocalTarget(destination) && !libraryManager.shouldUploadFile()) {
            return false; // can't upload
        }
        if (!isLocalTarget(source) && isLocalTarget(destination) && !libraryManager.shouldDownloadFile()) {
            return false; // can't download
        }
        return mode == Mode.NONE || skin != null;
    }

    private void accept(PlayerEntity player, String op) {
        String playerName = player.getName().getContents();
        ModLog.info("'{}' the {} request has been accepted, from: '{}', to: '{}'", playerName, op, source, destination);
    }

    private void error(PlayerEntity player, String op, String reason) {
        String playerName = player.getName().getContents();
        ModLog.info("'{}' the {} request was failure, reason: '{}', from: '{}', to: '{}'", playerName, op, reason, source, destination);
    }

    private void encodeSkin(PacketBuffer buffer) {
        if (skin == null) {
            buffer.writeEnum(Mode.NONE);
            return;
        }
        try {
            buffer.writeEnum(mode);
            GZIPOutputStream stream = new GZIPOutputStream(new ByteBufOutputStream(buffer));
            SkinIOUtils.saveSkinToStream(stream, skin);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Skin decodeSkin(PacketBuffer buffer) {
        Skin skin = null;
        try {
            GZIPInputStream stream = new GZIPInputStream(new ByteBufInputStream(buffer));
            skin = SkinIOUtils.loadSkinFromStream(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skin;
    }

    private String decodeResourceLocation(PacketBuffer buffer) {
        String location = buffer.readUtf();
        int index = location.indexOf(':');
        if (index < 0) {
            return "";
        }
        String path = FilenameUtils.normalize(location.substring(index + 1), true); // security check
        if (path != null) {
            return location.subSequence(0, index + 1) + path;
        }
        return "";
    }

    public String getPath(String location) {
        int index = location.indexOf(':');
        if (index < 0) {
            return "";
        }
        String path = FilenameUtils.normalize(location.substring(index + 1), true); // security check
        if (path != null) {
            return path;
        }
        return "";
    }

    private Skin getSkin() {
        if (skin != null) {
            return skin;
        }
        skin = SkinLoader.getInstance().loadSkin(source);
        return skin;
    }

    private boolean isAuthorized(String location, PlayerEntity player) {
        if (isServerTarget(location)) {
            String path = getPath(location);
            if (path.startsWith("/private/" + player.getStringUUID())) {
                return true; // any operation has been accepted in the player's own directory.
            }
            if (path.startsWith("/private")) {
                return false; // any operation has been rejected in the other player's private directory.
            }
            if (SkinLibraryManager.getServer().getLibrary().get(path) != null) {
                // required auth when on files already in public directory
                return SkinLibraryManager.getServer().shouldModifierFile(player);
            }
            return true;
        }
        return !location.isEmpty();
    }

    private boolean isLocalTarget(String path) {
        return path.startsWith(AWConstants.Namespace.LOCAL + ":");
    }

    private boolean isServerTarget(String path) {
        return path.startsWith(AWConstants.Namespace.SERVER + ":");
    }

    private boolean isDatabaseTarget(String path) {
        return path.startsWith(AWConstants.Namespace.DATABASE + ":") || path.startsWith(AWConstants.Namespace.DATABASE_LINK + ":");
    }

    public enum Mode {
        NONE, UPLOAD, DOWNLOAD
    }
}
