package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.compat.core.AbstractGameProfileResolver;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TextureUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class EntityTextureLoader {

    private static final UUID NIL_UUID = new UUID(0, 0);
    private static final EntityTextureLoader LOADER = new EntityTextureLoader();

    private final TaskQueue<String, GameProfile> namedProfiles = new TaskQueue<>(this::loadGameProfile);
    private final TaskQueue<EntityTextureDescriptor, EntityTexture> namedTextures = new TaskQueue<>(this::loadTexture);
    private final TaskQueue<OpenResourceLocation, BakedEntityTexture> registeredModels = new TaskQueue<>(this::bakeTexture);

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
        if (location != null) {
            return registeredModels.getOrCreate(location).get();
        }
        return null;
    }

    public OpenResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof MannequinEntity mannequin) {
            var texture = loadTexture(mannequin.getTextureDescriptor());
            if (texture != null && texture.location() != null) {
                return texture.location();
            }
        }
        return entity.skin().body();
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
            AbstractGameProfileResolver.load(profile, task);
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
        ModLog.debug("load game profile: {}", name);
        loadGameProfile(name, (profile, exception) -> {
            if (profile != null) {
                ModLog.debug("accept game profile: {} => {}", name, profile);
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
        EntityTextureDownloader.downloadAndRegisterSkin(descriptor).thenAcceptAsync(texture -> {
            ModLog.debug("accept entity texture from vanilla loader: {}, {}", texture.location(), profile);
            bakeTexture(texture.location(), texture);
            task.accept(texture);
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
        EntityTextureDownloader.downloadAndRegisterSkin(descriptor).thenAcceptAsync(texture -> {
            ModLog.debug("accept entity texture from custom loader: {}", texture.url());
            bakeTexture(texture.location(), texture);
            task.accept(texture);
        });
    }

    private void bakeTexture(OpenResourceLocation location, Task<BakedEntityTexture> task) {
        var textureModel = getModelType(location.toString());
        if (textureModel == null) {
            task.accept(null);
            return;
        }
        workThread.execute(() -> {
            try {
                var resourceManager = EnvironmentManager.getClientResourceManager();
                var bakedTexture = new BakedEntityTexture(location, textureModel);
                bakedTexture.loadImage(resourceManager.readResource(location));
                task.accept(bakedTexture);
                ModLog.debug("baked a entity default texture: '{}', model: {}", bakedTexture.location(), textureModel);
            } catch (Exception e) {
                e.printStackTrace();
                task.abort(e);
            }
        });
    }

    private void bakeTexture(OpenResourceLocation location, EntityTexture texture) {
        var task = registeredModels.getOrCreate(location);
        workThread.execute(() -> {
            var bakedTexture = new BakedEntityTexture(location, texture.model());
            if (texture.image() != null) {
                bakedTexture.loadImage(texture.image());
            }
            task.accept(bakedTexture);
            ModLog.debug("baked a entity custom texture: '{}', model: {}, url: {}", bakedTexture.location(), texture.model(), texture.url());
        });
    }

    // minecraft:textures/entity/steve.png
    // minecraft:textures/entity/alex.png
    // minecraft:textures/entity/player/slim/kai.png
    // minecraft:textures/entity/player/wide/steve.png
    private EntityTextureDescriptor.Model getModelType(String name) {
        // is a slim model?
        if (name.equals("minecraft:textures/entity/alex.png") || name.startsWith("minecraft:textures/entity/player/slim")) {
            return EntityTextureDescriptor.Model.SLIM;
        }
        // is a wide model?
        if (name.equals("minecraft:textures/entity/steve.png") || name.startsWith("minecraft:textures/entity/player/wide")) {
            return EntityTextureDescriptor.Model.WIDE;
        }
        return null;
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
