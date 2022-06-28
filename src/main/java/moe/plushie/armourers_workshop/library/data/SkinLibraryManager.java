package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateLibraryFilesPacket;
import moe.plushie.armourers_workshop.core.permission.Permissions;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class SkinLibraryManager implements ISkinLibraryListener {

    protected final ArrayList<ISkinLibraryListener> listeners = new ArrayList<>();

    public static Client getClient() {
        return Client.INSTANCE;
    }

    public static Server getServer() {
        return Server.INSTANCE;
    }

    public abstract void start();

    public abstract void stop();

    public void addListener(ISkinLibraryListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISkinLibraryListener listener) {
        this.listeners.remove(listener);
    }

    public boolean shouldDownloadFile(PlayerEntity player) {
        return true;
    }

    public boolean shouldUploadFile(PlayerEntity player) {
        return true;
    }

    @Override
    public void libraryDidReload(ISkinLibrary library) {
        listeners.forEach(listener -> listener.libraryDidReload(library));
    }


    public static class Client extends SkinLibraryManager {

        private static final Client INSTANCE = new Client();

        private final SkinLibrary localSkinLibrary;
        private final SkinLibrary publicSkinLibrary;
        private final SkinLibrary privateSkinLibrary;

        public Client() {
            this.localSkinLibrary = new SkinLibrary(DataDomain.LOCAL, AWCore.getSkinLibraryDirectory());
            this.publicSkinLibrary = new SkinLibrary.Proxy(DataDomain.DEDICATED_SERVER);
            this.privateSkinLibrary = new SkinLibrary.Proxy(DataDomain.DEDICATED_SERVER);
            this.localSkinLibrary.addListener(this);
            this.publicSkinLibrary.addListener(this);
            this.privateSkinLibrary.addListener(this);
        }

        @Override
        public void start() {
            this.localSkinLibrary.markBaseDir();
            this.localSkinLibrary.reload();
        }

        @Override
        public void stop() {
            this.publicSkinLibrary.reset();
            this.privateSkinLibrary.reset();
        }

        @Override
        public boolean shouldDownloadFile(PlayerEntity player) {
            if (!Permissions.SKIN_LIBRARY_SKIN_DOWNLOAD.accept(player)) {
                return false;
            }
            if (!LocalDataService.isRunning()) {
                return ModConfig.Common.allowDownloadingSkins;
            }
            return true;
        }

        @Override
        public boolean shouldUploadFile(PlayerEntity player) {
            if (!Permissions.SKIN_LIBRARY_SKIN_UPLOAD.accept(player)) {
                return false;
            }
            if (!LocalDataService.isRunning()) {
                return ModConfig.Common.allowUploadingSkins;
            }
            return true;
        }

        public SkinLibrary getLocalSkinLibrary() {
            return localSkinLibrary;
        }

        public SkinLibrary getPublicSkinLibrary() {
            return publicSkinLibrary;
        }

        public SkinLibrary getPrivateSkinLibrary() {
            return privateSkinLibrary;
        }
    }

    public static class Server extends SkinLibraryManager {

        private static final Server INSTANCE = new Server();

        private final SkinLibrary skinLibrary;
        private final ArrayList<SkinLibraryFile> publicFiles = new ArrayList<>();
        private final HashMap<String, ArrayList<SkinLibraryFile>> privateFiles = new HashMap<>();
        private final HashSet<String> syncedPlayers = new HashSet<>();

        private int version = 0;
        private boolean isReady = false;

        public Server() {
            this.skinLibrary = new SkinLibrary(DataDomain.DEDICATED_SERVER, AWCore.getSkinLibraryDirectory());
            this.skinLibrary.addListener(this);
        }

        @Override
        public void start() {
            this.skinLibrary.markBaseDir();
            this.skinLibrary.reload();
        }

        @Override
        public void stop() {
            this.skinLibrary.reset();
            this.publicFiles.clear();
            this.privateFiles.clear();
            this.syncedPlayers.clear();
            this.version = 0;
            this.isReady = false;
        }

        public void remove(PlayerEntity player) {
            this.syncedPlayers.remove(player.getStringUUID());
        }

        @Override
        public void libraryDidReload(ISkinLibrary library) {
            // analyze all files
            ArrayList<SkinLibraryFile> publicFiles = new ArrayList<>();
            HashMap<String, ArrayList<SkinLibraryFile>> privateFiles = new HashMap<>();
            for (SkinLibraryFile file : skinLibrary.getFiles()) {
                String path = file.getPath();
                if (path.startsWith(AWConstants.PRIVATE)) {
                    int index = path.indexOf('/', AWConstants.PRIVATE.length() + 1);
                    if (index >= 0) {
                        String key = path.substring(0, index);
                        privateFiles.computeIfAbsent(key, k -> new ArrayList<>()).add(file);
                    }
                } else {
                    publicFiles.add(file);
                }
            }
            // when the first load action, we must notify the user the skin library is reloaded.
            if (this.isReady && this.publicFiles.equals(publicFiles) && this.privateFiles.equals(privateFiles)) {
                return;
            }
            this.isReady = true;
            this.syncedPlayers.clear();
            this.publicFiles.clear();
            this.publicFiles.addAll(publicFiles);
            this.privateFiles.clear();
            this.privateFiles.putAll(privateFiles);
            this.version += 1;
            super.libraryDidReload(library);
        }

        public void sendTo(ServerPlayerEntity player) {
            if (!isReady) {
                return;
            }
            String uuid = player.getStringUUID();
            if (syncedPlayers.contains(uuid)) {
                return;
            }
            syncedPlayers.add(uuid);
            String key = AWConstants.PRIVATE + "/" + uuid;
            String name = player.getName().getString();
            ArrayList<SkinLibraryFile> privateFiles = this.privateFiles.getOrDefault(key, new ArrayList<>());
            UpdateLibraryFilesPacket packet = new UpdateLibraryFilesPacket(publicFiles, privateFiles);
            NetworkHandler.getInstance().sendTo(packet, player);
            ModLog.debug("syncing library files {}/{} to '{}'.", publicFiles.size(), privateFiles.size(), name);
        }

        public SkinLibrary getLibrary() {
            return skinLibrary;
        }

        @Override
        public boolean shouldDownloadFile(PlayerEntity player) {
            if (!Permissions.SKIN_LIBRARY_SKIN_DOWNLOAD.accept(player)) {
                return false;
            }
            if (FMLEnvironment.dist.isDedicatedServer()) {
                return ModConfig.Common.allowDownloadingSkins;
            }
            return true;
        }

        @Override
        public boolean shouldUploadFile(PlayerEntity player) {
            if (!Permissions.SKIN_LIBRARY_SKIN_UPLOAD.accept(player)) {
                return false;
            }
            if (FMLEnvironment.dist.isDedicatedServer()) {
                return ModConfig.Common.allowUploadingSkins;
            }
            return true;
        }

        public boolean shouldModifierFile(PlayerEntity player) {
            // super op can manage the public folder.
            return ModConfig.Common.allowLibraryRemoteManage && player.hasPermissions(5);
        }

        public LocalDataService getDatabaseLibrary() {
            return LocalDataService.getInstance();
        }

        public int getVersion() {
            return version;
        }

        public boolean isReady() {
            return isReady;
        }
    }
}
