package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.network.UpdateLibraryFilesPacket;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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

    public static void startClient() {
        getClient().start();
    }

    public static void startServer() {
        getServer().start();
    }

    public abstract void start();

    public abstract void stop();

    public void addListener(ISkinLibraryListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISkinLibraryListener listener) {
        this.listeners.remove(listener);
    }

    public boolean shouldDownloadFile(Player player) {
        return true;
    }

    public boolean shouldUploadFile(Player player) {
        return true;
    }

    public boolean shouldMaintenanceFile(Player player) {
        return true;
    }

    @Override
    public void libraryDidReload(ISkinLibrary library) {
        listeners.forEach(listener -> listener.libraryDidReload(library));
    }

    @Override
    public void libraryDidChanges(ISkinLibrary library, ISkinLibrary.Difference difference) {
        listeners.forEach(listener -> listener.libraryDidChanges(library, difference));
    }

    public static class Client extends SkinLibraryManager {

        private static final Client INSTANCE = new Client();

        private final SkinLibrary localSkinLibrary;
        private final SkinLibrary publicSkinLibrary;
        private final SkinLibrary privateSkinLibrary;

        private SkinLibrarySetting setting = SkinLibrarySetting.DEFAULT;

        public Client() {
            this.localSkinLibrary = new SkinLibrary(DataDomain.LOCAL, EnvironmentManager.getSkinLibraryDirectory());
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
        public boolean shouldDownloadFile(Player player) {
            if (!LocalDataService.isRunning()) {
                if (shouldMaintenanceFile(player)) {
                    return true;
                }
                return ModConfig.Common.allowDownloadingSkins && setting.allowsDownload();
            }
            return true;
        }

        @Override
        public boolean shouldUploadFile(Player player) {
            if (!LocalDataService.isRunning()) {
                if (shouldMaintenanceFile(player)) {
                    return true;
                }
                return ModConfig.Common.allowUploadingSkins && setting.allowsUpload();
            }
            return true;
        }

        @Override
        public boolean shouldMaintenanceFile(Player player) {
            return ModConfig.Common.allowLibraryRemoteManage && setting.allowsMaintenance();
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

        public void setSetting(SkinLibrarySetting setting) {
            this.setting = setting;
        }

        public SkinLibrarySetting getSetting() {
            return setting;
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
            this.skinLibrary = new SkinLibrary(DataDomain.DEDICATED_SERVER, EnvironmentManager.getSkinLibraryDirectory());
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

        public void remove(Player player) {
            ModLog.debug("remove synced player {}", player.getStringUUID());
            this.syncedPlayers.remove(player.getStringUUID());
        }

        @Override
        public void libraryDidReload(ISkinLibrary library) {
            // analyze all files
            ArrayList<SkinLibraryFile> publicFiles = new ArrayList<>();
            HashMap<String, ArrayList<SkinLibraryFile>> privateFiles = new HashMap<>();
            for (SkinLibraryFile file : skinLibrary.getFiles()) {
                String path = file.getPath();
                if (path.startsWith(Constants.PRIVATE)) {
                    int index = path.indexOf('/', Constants.PRIVATE.length() + 1);
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

        public void sendTo(ServerPlayer player) {
            if (!isReady) {
                return;
            }
            String uuid = player.getStringUUID();
            if (syncedPlayers.contains(uuid)) {
                return;
            }
            syncedPlayers.add(uuid);
            String key = Constants.PRIVATE + "/" + uuid;
            String name = player.getScoreboardName();
            SkinLibrarySetting setting = new SkinLibrarySetting(player);
            ArrayList<SkinLibraryFile> privateFiles = this.privateFiles.getOrDefault(key, new ArrayList<>());
            UpdateLibraryFilesPacket packet = new UpdateLibraryFilesPacket(publicFiles, privateFiles, setting);
            NetworkManager.sendTo(packet, player);
            ModLog.debug("syncing library files {}/{} to '{}'.", publicFiles.size(), privateFiles.size(), name);
        }

        public SkinLibrary getLibrary() {
            return skinLibrary;
        }

        @Override
        public boolean shouldDownloadFile(Player player) {
            if (!ModPermissions.SKIN_LIBRARY_SKIN_DOWNLOAD.accept(player)) {
                return false;
            }
            if (EnvironmentManager.isDedicatedServer()) {
                if (shouldMaintenanceFile(player)) {
                    return true;
                }
                return ModConfig.Common.allowDownloadingSkins;
            }
            return true;
        }

        @Override
        public boolean shouldUploadFile(Player player) {
            if (!ModPermissions.SKIN_LIBRARY_SKIN_UPLOAD.accept(player)) {
                return false;
            }
            if (EnvironmentManager.isDedicatedServer()) {
                if (shouldMaintenanceFile(player)) {
                    return true;
                }
                return ModConfig.Common.allowUploadingSkins;
            }
            return true;
        }

        @Override
        public boolean shouldMaintenanceFile(Player player) {
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
