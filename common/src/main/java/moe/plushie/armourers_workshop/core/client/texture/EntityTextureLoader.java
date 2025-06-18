package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.compatibility.client.AbstractCustomProfileTextureLoader;
import moe.plushie.armourers_workshop.compatibility.core.AbstractCustomProfileLoader;
import moe.plushie.armourers_workshop.core.client.other.SkinRemoteTexture;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TextureUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class EntityTextureLoader {

    public static final OpenResourceLocation STEVE_SKIN_LOCATION = OpenResourceLocation.parse("textures/entity/steve.png");
    public static final OpenResourceLocation ALEX_SKIN_LOCATION = OpenResourceLocation.parse("textures/entity/alex.png");

    private static final UUID NIL_UUID = new UUID(0, 0);
    private static final EntityTextureLoader LOADER = new EntityTextureLoader();

    private final TaskQueue<String, GameProfile> namedProfiles = new TaskQueue<>(this::loadGameProfile);
    private final TaskQueue<EntityTextureDescriptor, EntityTexture> namedTextures = new TaskQueue<>(this::loadTexture);

    private final TaskQueue<OpenResourceLocation, BakedEntityTexture> registeredModels = new TaskQueue<>(this::loadTexture);
    private final HashMap<String, BakedEntityTexture> downloadedModels = new HashMap<>();

    private final Executor workThread = Executors.newFixedThreadPool(1, "AW-SKIN/T-LD");

    public static EntityTextureLoader getInstance() {
        return LOADER;
    }

    public void start() {
    }

    public void stop() {
        namedProfiles.clear();
        namedTextures.clear();
        registeredModels.clear();
    }

    public GameProfile getGameProfile(EntityTextureDescriptor descriptor) {
        var profile = descriptor.profile();
        if (profile != null) {
            return profile;
        }
        var name = descriptor.name();
        if (name != null) {
            return namedProfiles.getOrCreate(descriptor.name()).get();
        }
        return null;
    }

    @Nullable
    public BakedEntityTexture getTextureModel(OpenResourceLocation location) {
        if (location == null) {
            return null;
        }
        return registeredModels.getOrCreate(location).get();
    }

    public OpenResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof MannequinEntity mannequin) {
            var descriptor = mannequin.getTextureDescriptor();
            var texture = loadTexture(descriptor);
            if (texture != null && texture.location() != null) {
                return texture.location();
            }
        }
        return TextureUtils.getTexture(entity);
    }

    public OpenResourceLocation getTextureLocation(EntityTextureDescriptor descriptor) {
        if (!descriptor.isEmpty()) {
            var texture1 = loadTexture(descriptor);
            if (texture1 != null) {
                return texture1.location();
            }
        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    @Nullable
    public EntityTexture loadTexture(EntityTextureDescriptor descriptor) {
        if (descriptor.isEmpty()) {
            return null;
        }
        return namedTextures.getOrCreate(descriptor).get();
    }

    public void loadGameProfile(String name, IResultHandler<GameProfile> handler) {
        namedProfiles.getOrCreate(name).listen(handler);
    }

    private void loadGameProfile(String name, Task<GameProfile> task) {
        workThread.execute(() -> {
            var profile = new GameProfile(NIL_UUID, name);
            AbstractCustomProfileLoader.load(profile, task);
        });
    }

    public void loadTexture(EntityTextureDescriptor descriptor, IResultHandler<EntityTexture> handler) {
        namedTextures.getOrCreate(descriptor).listen(handler);
    }

    private void loadTexture(EntityTextureDescriptor descriptor, Task<EntityTexture> task) {
        // ignore empty descriptor.
        if (descriptor.isEmpty()) {
            task.accept(EntityTexture.EMPTY);
            return;
        }
        // load from url
        var url = descriptor.url();
        if (url != null) {
            try {
                var ignored = new URL(url);
                loadTextureWithURL(url, task);
            } catch (MalformedURLException e) {
                task.abort(new RuntimeException("Invalid URL: " + url));
            }
            return;
        }
        // load from profile.
        var profile = descriptor.profile();
        if (profile != null) {
            loadTextureWithProfile(profile, task);
            return;
        }
        // load from username.
        var name = descriptor.name();
        if (name != null) {
            loadTextureWithName(name, task);
            return;
        }
        // ignore
        task.abort(new RuntimeException("Invalid Descriptor: " + descriptor));
    }

    private void loadTextureWithName(String name, Task<EntityTexture> task) {
        loadGameProfile(name, (profile, exception) -> {
            if (profile != null) {
                loadTextureWithProfile(profile, task);
            } else {
                exception.printStackTrace();
                task.abort(new RuntimeException("Invalid User: " + name));
            }
        });
    }

    private void loadTextureWithProfile(GameProfile profile, Task<EntityTexture> task) {
        var descriptor = EntityTextureDescriptor.fromProfile(profile);
        var owner = namedTextures.getOrCreate(descriptor);
        if (owner != task) {
            owner.listen(task); // we need wait owner complete.
            return;
        }
        ModLog.debug("load entity texture: {}", profile);
        AbstractCustomProfileTextureLoader.load(profile, (location, url, modelType) -> {
            ModLog.debug("accept entity texture from vanilla loader: {}, {}", location, profile);
            task.accept(buildEntityTexture(descriptor, location, url, modelType));
        });
    }

    private void loadTextureWithURL(String url, Task<EntityTexture> task) {
        var descriptor = EntityTextureDescriptor.fromURL(url);
        var owner = namedTextures.getOrCreate(descriptor);
        if (owner != task) {
            owner.listen(task); // we need wait owner complete.
            return;
        }
        ModLog.debug("load entity texture: {}", url);
        var identifier = Objects.md5(url);
        var location = OpenResourceLocation.parse("skins/aw-" + identifier);
        var textureManager = Minecraft.getInstance().getTextureManager();
        //var processingTexture = textureManager.getTexture(location.toLocation(), null);
        //if (processingTexture != null) {
        //    return; // wait the texture download complete.
        //}
        var prefix = identifier.substring(0, 2);
        var path = new File(EnvironmentManager.getRootDirectory() + "/skin-textures/" + prefix + "/" + identifier);
        var downloadingTexture = new SkinRemoteTexture(url, path, ModTextures.MANNEQUIN_DEFAULT, true, () -> {
            ModLog.debug("accept entity texture from custom loader => {}", location);
            task.accept(buildEntityTexture(descriptor, location, url, null));
        });
        textureManager.register(location.toLocation(), downloadingTexture);
    }

    private void loadTexture(OpenResourceLocation location, Task<BakedEntityTexture> task) {
        var steve = location.equals(STEVE_SKIN_LOCATION);
        var alex = location.equals(ALEX_SKIN_LOCATION);
        if (!steve && !alex) {
            return;
        }
        workThread.execute(() -> {
            var texture = new BakedEntityTexture(location, alex);
            task.accept(texture);
        });
    }


    public void receivePlayerTexture(String url, OpenNativeImage image, boolean slim) {
        if (image == null) {
            return;
        }
        var newImage = image.clone();
        workThread.execute(() -> {
            var bakedTexture = getDownloadedTexture(url);
            if (bakedTexture.modelType() == null) {
                bakedTexture.setModelType("default");
                if (slim) {
                    bakedTexture.setModelType("slim");
                }
            }
            bakedTexture.loadImage(newImage, Objects.equals(bakedTexture.modelType(), "slim"));
            ModLog.debug("baked a player texture => {}, url: {}, slim: {}", bakedTexture.location(), url, slim);
        });
    }

    private synchronized BakedEntityTexture getDownloadedTexture(String url) {
        return downloadedModels.computeIfAbsent(url, k -> new BakedEntityTexture());
    }

    private synchronized EntityTexture buildEntityTexture(EntityTextureDescriptor descriptor, OpenResourceLocation location, String url, String modelType) {
        var texture = new EntityTexture(descriptor, location, url, modelType);
        var model = getDownloadedTexture(url);
        model.setResourceLocation(location);
        model.setModelType(modelType);
        texture.setTexture(model);
        registeredModels.getOrCreate(location).accept(model);
        return texture;
    }

    private static class Task<V> implements IResultHandler<V> {

        private V value;
        private Exception exception;
        private boolean isCompleted = false;

        private ConcurrentLinkedDeque<IResultHandler<V>> handlers = new ConcurrentLinkedDeque<>();

        @Override
        public void apply(V value, Exception exception) {
            this.value = value;
            this.exception = exception;
            this.isCompleted = true;
            this.invoke();
        }

        public void invoke() {
            if (this.handlers.isEmpty()) {
                return;
            }
            var handlers = this.handlers;
            this.handlers = new ConcurrentLinkedDeque<>();
            handlers.forEach(handler -> handler.apply(value, exception));
        }

        public void listen(@Nullable IResultHandler<V> handler) {
            if (isCompleted) {
                if (handler != null) {
                    handler.apply(value, exception);
                }
                return;
            }
            if (handler != null) {
                handlers.add(handler);
            }
        }

        public V get() {
            return value;
        }
    }

    private static class TaskQueue<K, V> {

        private final BiConsumer<K, Task<V>> handler;
        private final ConcurrentHashMap<K, Task<V>> values = new ConcurrentHashMap<>();

        public TaskQueue(BiConsumer<K, Task<V>> handler) {
            this.handler = handler;
        }

        public void clear() {
            values.clear();
        }

        public Task<V> getOrCreate(K key) {
            var task = values.get(key);
            if (task != null) {
                return task;
            }
            task = new Task<V>();
            values.put(key, task);
            handler.accept(key, task);
            return task;
        }
    }
}
