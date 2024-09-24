package moe.plushie.armourers_workshop.library.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataEncryptMethod;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.menu.SkinLibraryMenu;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SaveSkinPacket extends CustomPacket {

    protected final Target source;
    protected final Target destination;

    protected Skin skin;
    protected Mode mode;

    public SaveSkinPacket(String source, SkinFileOptions sourceOptions, String destination, SkinFileOptions destinationOptions) {
        this.source = new Target(source, sourceOptions);
        this.destination = new Target(destination, destinationOptions);
        this.mode = Mode.NONE;
        boolean shouldUpload = this.source.isLocal() && !this.destination.isLocal();
        if (shouldUpload) {
            this.mode = Mode.UPLOAD;
            if (SkinLibraryManager.getClient().shouldUploadFile(null)) {
                this.skin = loadSkin(source, sourceOptions);
            }
        }
    }

    public SaveSkinPacket(IFriendlyByteBuf buffer) {
        this.source = Target.readFromStream(buffer);
        this.destination = Target.readFromStream(buffer);
        this.mode = buffer.readEnum(Mode.class);
        switch (mode) {
            case UPLOAD: {
                boolean shouldUpload = source.isLocal() && !destination.isLocal();
                if (shouldUpload && SkinLibraryManager.getServer().shouldUploadFile(null)) {
                    this.skin = decodeSkin(buffer);
                }
                break;
            }
            case DOWNLOAD: {
                boolean shouldDownload = !source.isLocal() && destination.isLocal();
                if (shouldDownload) {
                    this.skin = decodeSkin(buffer);
                }
                break;
            }
        }
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        source.writeToStream(buffer);
        destination.writeToStream(buffer);
        if (skin != null) {
            buffer.writeEnum(mode);
            encodeSkin(skin, buffer);
        } else {
            buffer.writeEnum(Mode.NONE);
        }
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        if (!destination.isLocal()) {
            return; // ignore
        }
        var skin = getSkin();
        if (skin == null) {
            abort(player, "load", "missing from skin loader");
            return;
        }
        var library = SkinLibraryManager.getClient().getLocalSkinLibrary();
        library.save(destination.getPath(), skin, destination.getOptions());
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // this is an unauthorized operation, ignore it
        if (!source.isAuthorized(player) || !destination.isAuthorized(player) || !(player.containerMenu instanceof SkinLibraryMenu container)) {
            abort(player, "unauthorized", "user status is incorrect or the path is invalid");
            return;
        }
        var server = SkinLibraryManager.getServer();
        if (source.isLocal() && !server.shouldUploadFile(player)) {
            abort(player, "upload", "uploading prohibited in the config file");
            return;
        }
        // load: fs -> db/ln/ws -> db/ln
        if (destination.isDatabase()) {
            if (!ModPermissions.SKIN_LIBRARY_SKIN_LOAD.accept(player)) {
                abort(player, "load", "load prohibited in the config file");
                return;
            }
            var skin = getSkin();
            if (skin == null) {
                abort(player, "load", "missing from skin loader");
                return;
            }
            if (container.shouldLoadStack()) {
                accept(player, "load");
                // TODO: fix db-link
                var identifier = SkinLoader.getInstance().saveSkin(source.getIdentifier(), skin);
                container.crafting(new SkinDescriptor(identifier, skin.getType()));
            }
            return;
        }
        // save: fs -> ws/db -> ws/ws -> ws
        if (destination.isServer()) {
            if (!ModPermissions.SKIN_LIBRARY_SKIN_SAVE.accept(player)) {
                abort(player, "save", "save prohibited in the config file");
                return;
            }
            var skin = getSkin();
            if (skin == null) {
                abort(player, "save", "missing from skin loader");
                return;
            }
            if (!skin.getSettings().isSavable()) {
                abort(player, "save", "save prohibited from the skin author");
                return;
            }
            if (container.shouldSaveStack()) {
                accept(player, "save");
                SkinLoader.getInstance().removeSkin(destination.getIdentifier()); // remove skin cache.
                server.getLibrary().save(destination.getPath(), skin, destination.getOptions());
                container.crafting(null);
            }
            return;
        }
        // download: ws -> fs/db -> fs/fs -> fs
        if (destination.isLocal()) {
            if (!source.isLocal() && !server.shouldDownloadFile(player)) {
                abort(player, "download", "download prohibited in the config file");
                return;
            }
            if (!source.isLocal()) {
                this.skin = getSkin();
                this.mode = Mode.DOWNLOAD;
            }
            if (skin != null && !skin.getSettings().isSavable()) {
                abort(player, "download", "download prohibited from the skin author");
                return;
            }
            if (container.shouldSaveStack()) {
                // send skin data to client again, except case: fs -> fs
                accept(player, "download");
                NetworkManager.sendTo(this, player);
                container.crafting(null);
            }
            return;
        }
        abort(player, "unknown", "dangerous operation");
    }

    public boolean isReady(Player player) {
        var libraryManager = SkinLibraryManager.getClient();
        // when a remote server, check the config.
        if (source.isLocal() && !destination.isLocal() && !libraryManager.shouldUploadFile(player)) {
            return false; // can't upload
        }
        if (!source.isLocal() && destination.isLocal() && !libraryManager.shouldDownloadFile(player)) {
            return false; // can't download
        }
        return mode == Mode.NONE || skin != null;
    }

    private void accept(Player player, String op) {
        ModLog.info("accept {} request of the '{}', from: '{}', to: '{}'", op, player.getScoreboardName(), source, destination);
    }

    private void abort(Player player, String op, String reason) {
        ModLog.info("abort {} request of the '{}', reason: '{}', from: '{}', to: '{}'", op, player.getScoreboardName(), reason, source, destination);
    }

    private void encodeSkin(Skin skin, IFriendlyByteBuf buffer) {
        try (var outputStream = new GZIPOutputStream(new ByteBufOutputStream(buffer.asByteBuf()))) {
            SkinFileStreamUtils.saveSkinToStream(outputStream, skin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Skin decodeSkin(IFriendlyByteBuf buffer) {
        try (var inputStream = new GZIPInputStream(new ByteBufInputStream(buffer.asByteBuf()))) {
            return SkinFileStreamUtils.loadSkinFromStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Skin loadSkin(String identifier, SkinFileOptions options) {
        try {
            var stream = SkinLoader.getInstance().loadSkinData(identifier);
            return SkinFileStreamUtils.loadSkinFromStream(stream, resolveLoadOptions(options));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Skin getSkin() {
        if (skin != null) {
            return skin;
        }
        skin = loadSkin(source.getIdentifier(), source.getOptions());
        return skin;
    }

    private SkinFileOptions resolveLoadOptions(SkinFileOptions options) {
        if (options != null) {
            var server = SkinLibraryManager.getServer();
            if (server.isRunning() && Objects.equals(options.getSecurityData(), server.getPublicKey())) {
                var fixedOptions = new SkinFileOptions();
                fixedOptions.merge(options);
                fixedOptions.setSecurityKey(DataEncryptMethod.AUTH.key(server.getPrivateKey()));
                fixedOptions.setSecurityData(server.getPublicKey());
                return fixedOptions;
            }
        }
        return options;
    }


    public enum Mode {
        NONE, UPLOAD, DOWNLOAD
    }

    public static class Target {

        protected final String identifier;
        protected final SkinFileOptions options;

        public Target(String identifier, SkinFileOptions options) {
            this.identifier = identifier;
            this.options = options;
        }

        public static Target readFromStream(IFriendlyByteBuf buffer) {
            String identifier = buffer.readUtf();
            int index = identifier.indexOf(':');
            if (index < 0) {
                throw new RuntimeException("illegal identifier!!!");
            }
            String path = SkinFileUtils.normalize(identifier.substring(index + 1), true); // security check
            if (path != null) {
                identifier = identifier.subSequence(0, index + 1) + path;
            }
            SkinFileOptions options = null;
            var optionsTag = buffer.readNbt();
            if (optionsTag != null) {
                options = new SkinFileOptions(optionsTag);
            }
            return new Target(identifier, options);
        }

        public void writeToStream(IFriendlyByteBuf buffer) {
            buffer.writeUtf(identifier);
            if (options != null) {
                buffer.writeNbt(options.serializeNBT());
            } else {
                buffer.writeNbt(null);
            }
        }

        public boolean isLocal() {
            return DataDomain.isLocal(identifier);
        }

        public boolean isServer() {
            return DataDomain.isServer(identifier);
        }

        public boolean isDatabase() {
            return DataDomain.isDatabase(identifier);
        }

        public boolean isUnknown() {
            return identifier.isEmpty();
        }

        public boolean isAuthorized(Player player) {
            if (isServer()) {
                var path = getPath();
                if (path.startsWith(Constants.PRIVATE + "/" + player.getStringUUID())) {
                    return true; // any operation has been accepted in the player's own directory.
                }
                if (path.startsWith(Constants.PRIVATE)) {
                    return false; // any operation has been rejected in the other player's private directory.
                }
                return path.startsWith("/");
//            if (SkinLibraryManager.getServer().getLibrary().get(path) != null) {
//                // required auth when on files already in public directory
//                return SkinLibraryManager.getServer().shouldModifierFile(player);
//            }
            }
            return !isUnknown();
        }

        public String getPath() {
            return DataDomain.getPath(identifier);
        }

        public String getIdentifier() {
            return identifier;
        }

        public SkinFileOptions getOptions() {
            return options;
        }

        @Override
        public String toString() {
            return identifier;
        }
    }
}
