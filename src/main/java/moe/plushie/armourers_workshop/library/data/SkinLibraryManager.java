package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateLibraryFilesPacket;
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

    public boolean shouldDownloadFile() {
        return true;
    }

    public boolean shouldUploadFile() {
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
            this.localSkinLibrary = new SkinLibrary(AWConstants.Namespace.LOCAL, AWCore.getSkinLibraryDirectory());
            this.publicSkinLibrary = new SkinLibrary.Proxy(AWConstants.Namespace.SERVER);
            this.privateSkinLibrary = new SkinLibrary.Proxy(AWConstants.Namespace.SERVER);
            this.localSkinLibrary.addListener(this);
            this.publicSkinLibrary.addListener(this);
            this.privateSkinLibrary.addListener(this);
        }

        @Override
        public void start() {
            this.localSkinLibrary.reload();
        }

        @Override
        public void stop() {
            this.publicSkinLibrary.reset();
            this.privateSkinLibrary.reset();
        }

        @Override
        public boolean shouldDownloadFile() {
            if (!LocalDataService.isRunning()) {
                return ModConfig.allowDownloadingSkins;
            }
            return true;
        }

        @Override
        public boolean shouldUploadFile() {
            if (!LocalDataService.isRunning()) {
                return ModConfig.allowUploadingSkins;
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
            this.skinLibrary = new SkinLibrary("ws", AWCore.getSkinLibraryDirectory());
            this.skinLibrary.addListener(this);
        }

        @Override
        public void start() {
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
            String privatePath = "/private";
            ArrayList<SkinLibraryFile> publicFiles = new ArrayList<>();
            HashMap<String, ArrayList<SkinLibraryFile>> privateFiles = new HashMap<>();
            for (SkinLibraryFile file : skinLibrary.getFiles()) {
                String path = file.getPath();
                if (path.startsWith(privatePath)) {
                    int index = path.indexOf('/', privatePath.length() + 1);
                    if (index >= 0) {
                        String key = path.substring(0, index);
                        privateFiles.computeIfAbsent(key, k -> new ArrayList<>()).add(file);
                    }
                } else {
                    publicFiles.add(file);
                }
            }
            this.isReady = true;
            if (this.publicFiles.equals(publicFiles) && this.privateFiles.equals(privateFiles)) {
                return;
            }
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
            String key = "/private/" + uuid;
            String name = player.getName().getString();
            ArrayList<SkinLibraryFile> privateFiles = this.privateFiles.getOrDefault(key, new ArrayList<>());
            UpdateLibraryFilesPacket packet = new UpdateLibraryFilesPacket(publicFiles, privateFiles);
            NetworkHandler.getInstance().sendTo(packet, player);
            ModLog.debug("Syncing library files {}/{} to '{}'.", publicFiles.size(), privateFiles.size(), name);
        }

        public SkinLibrary getLibrary() {
            return skinLibrary;
        }

        @Override
        public boolean shouldDownloadFile() {
            if (FMLEnvironment.dist.isDedicatedServer()) {
                return ModConfig.allowDownloadingSkins;
            }
            return true;
        }

        @Override
        public boolean shouldUploadFile() {
            if (FMLEnvironment.dist.isDedicatedServer()) {
                return ModConfig.allowUploadingSkins;
            }
            return true;
        }

        public boolean shouldModifierFile(PlayerEntity player) {
            // super op can manage the public folder.
            return ModConfig.enableLibraryManage && player.hasPermissions(4);
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
