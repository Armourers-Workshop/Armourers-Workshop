package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class ModEntityProfiles {

    private static final ArrayList<BiConsumer<IRegistryHolder<?>, EntityProfile>> INSERT_HANDLERS = new ArrayList<>();
    private static final ArrayList<BiConsumer<IRegistryHolder<?>, EntityProfile>> REMOVE_HANDLERS = new ArrayList<>();
    private static final ArrayList<BiConsumer<IRegistryHolder<?>, EntityProfile>> UPDATE_HANDLERS = new ArrayList<>();

    private static final Map<OpenResourceKey, EntityProfile> USING_PROFILES = new LinkedHashMap<>();
    private static final Map<OpenResourceKey, EntityProfile> CUSTOM_PROFILES = new LinkedHashMap<>();
    private static final Map<OpenResourceKey, EntityProfile> BUILTIN_PROFILES = new LinkedHashMap<>();

    private static final Map<IRegistryHolder<?>, EntityProfile> USING_ENTITIES = new LinkedHashMap<>();
    private static final Map<IRegistryHolder<?>, EntityProfile> CUSTOM_ENTITIES = new LinkedHashMap<>();
    private static final Map<IRegistryHolder<?>, EntityProfile> BUILTIN_ENTITIES = new LinkedHashMap<>();
    private static final Map<IRegistryHolder<?>, EntityProfile> SERVER_ENTITIES = new LinkedHashMap<>();

    public static void init() {
        DataPackManager.register(DataPackType.SERVER_DATA, "skin/profiles", SimpleLoader::custom, null, SimpleLoader::freezeCustom, 1);
        DataPackManager.register(DataPackType.BUNDLED_DATA, "skin/profiles", SimpleLoader::builtin, null, SimpleLoader::freezeBuiltin, 1);
    }

    public static void addListener(BiConsumer<IRegistryHolder<?>, EntityProfile> changeHandler) {
        REMOVE_HANDLERS.add((entityType, entityProfile) -> changeHandler.accept(entityType, null));
        INSERT_HANDLERS.add(changeHandler);
        UPDATE_HANDLERS.add(changeHandler);
        // if it add listener after the loading, we need manual send a notification.
        USING_ENTITIES.forEach(changeHandler);
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(T entity) {
        return getProfile(entity.getType());
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(EntityType<T> entityType) {
        //
        for (var entry : USING_ENTITIES.entrySet()) {
            if (entityType.equals(entry.getKey().get())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    public static EntityProfile getProfile(OpenResourceKey registryName) {
        return USING_PROFILES.get(registryName);
    }

    public static void setCustomProfiles(Map<IRegistryHolder<?>, EntityProfile> snapshot) {
        // ignore when no changes.
        if (SERVER_ENTITIES.equals(snapshot)) {
            return;
        }
        ModLog.debug("apply entity profile changes from server");
        SERVER_ENTITIES.clear();
        SERVER_ENTITIES.putAll(snapshot);
        SimpleLoader.freeze();
    }

    public static Map<IRegistryHolder<?>, EntityProfile> getCustomProfiles() {
        return CUSTOM_ENTITIES;
    }

    public static EntityProfile getEmptyProfile() {
        var builder = new SimpleBuilder(ModConstants.key("builtin/empty"));
        builder.isLocked = true;
        return builder.build();
    }

    private static class SimpleLoader implements DataPackBuilder {

        private static final Map<OpenResourceKey, SimpleBuilder> CUSTOM_PROFILE_BUILDERS = new LinkedHashMap<>();
        private static final Map<OpenResourceKey, SimpleBuilder> BUILTIN_PROFILE_BUILDERS = new LinkedHashMap<>();

        private final SimpleBuilder builder;

        public SimpleLoader(SimpleBuilder builder) {
            this.builder = builder;
        }

        public static SimpleLoader builtin(OpenResourceKey registryName) {
            return new SimpleLoader(BUILTIN_PROFILE_BUILDERS.computeIfAbsent(registryName, SimpleBuilder::builtin));
        }

        public static SimpleLoader custom(OpenResourceKey registryName) {
            return new SimpleLoader(CUSTOM_PROFILE_BUILDERS.computeIfAbsent(registryName, SimpleBuilder::custom));
        }

        @Override
        public void append(IODataObject object, OpenResourceKey key) {
            if (object.get("replace").boolValue()) {
                builder.isLocked = false;
                builder.supports.clear();
                builder.transformers.clear();
                builder.entities.clear();
            }
            object.get("locked").ifPresent(o -> {
                builder.isLocked = o.boolValue();
            });
            object.get("slots").entrySet().forEach(it -> {
                var type = SkinSlotType.byName(it.getKey());
                var name = it.getValue().stringValue();
                if (type != null) {
                    builder.supports.put(type, name);
                }
            });
            object.get("transformers").allValues().forEach(o -> {
                builder.transformers.add(OpenResourceKey.parse(o.stringValue()));
            });
            object.get("entities").allValues().forEach(o -> {
                builder.entities.add(AbstractEntityTypeBuilder.lazy(o.stringValue()));
            });
        }

        @Override
        public void build() {
            // ignore.
        }

        private static void freezeCustom() {
            // regenerate all entity profile.
            var newEntities = new LinkedHashMap<IRegistryHolder<?>, EntityProfile>();
            CUSTOM_ENTITIES.clear();
            CUSTOM_PROFILE_BUILDERS.forEach((key, builder) -> {
                var profile = builder.build();
                builder.entities.forEach(entityType -> newEntities.put(entityType, profile));
            });
            CUSTOM_PROFILE_BUILDERS.clear();
            // only use when custom profile changed.
            var usedProfiles = new LinkedHashMap<OpenResourceKey, EntityProfile>();
            newEntities.forEach((entityType, profile) -> {
                var oldProfile = BUILTIN_ENTITIES.get(entityType);
                if (oldProfile != null && EntityProfile.same(oldProfile, profile)) {
                    return; // not any change.
                }
                CUSTOM_ENTITIES.put(entityType, profile);
                usedProfiles.put(profile.registryName(), profile);
            });
            // apply the patch
            difference(CUSTOM_PROFILES, usedProfiles, (registryName, entityProfile) -> {
                CUSTOM_PROFILES.remove(registryName);
                ModLog.debug("Unregistering Entity Profile '{}'", registryName);
            }, (registryName, entityProfile) -> {
                ModLog.debug("Registering Entity Profile '{}'", registryName);
                CUSTOM_PROFILES.put(registryName, entityProfile);
            }, null);
            // freeze all data.
            freeze();
        }

        private static void freezeBuiltin() {
            // regenerate all entity profile.
            var newProfiles = new LinkedHashMap<OpenResourceKey, EntityProfile>();
            BUILTIN_ENTITIES.clear();
            BUILTIN_PROFILE_BUILDERS.forEach((key, builder) -> {
                var profile = builder.build();
                newProfiles.put(builder.registryName, profile);
                builder.entities.forEach(entityType -> BUILTIN_ENTITIES.put(entityType, profile));
            });
            BUILTIN_PROFILE_BUILDERS.clear();
            // apply the patch
            difference(BUILTIN_PROFILES, newProfiles, (registryName, entityProfile) -> {
                BUILTIN_PROFILES.remove(registryName);
                ModLog.debug("Unregistering Entity Profile '{}'", registryName);
            }, (registryName, entityProfile) -> {
                ModLog.debug("Registering Entity Profile '{}'", registryName);
                BUILTIN_PROFILES.put(registryName, entityProfile);
            }, null);
            // freeze all data.
            freeze();
        }

        private static void freeze() {
            // apply the patch.
            var entities = new LinkedHashMap<IRegistryHolder<?>, EntityProfile>();
            entities.putAll(BUILTIN_ENTITIES);
            entities.putAll(CUSTOM_ENTITIES);
            entities.putAll(SERVER_ENTITIES);
            difference(USING_ENTITIES, entities, (entityType, entityProfile) -> {
                USING_ENTITIES.remove(entityType);
                REMOVE_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            }, (entityType, entityProfile) -> {
                USING_ENTITIES.put(entityType, entityProfile);
                INSERT_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            }, (entityType, entityProfile) -> {
                USING_ENTITIES.put(entityType, entityProfile);
                UPDATE_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            });
            USING_PROFILES.clear();
            entities.values().forEach(profile -> USING_PROFILES.put(profile.registryName(), profile));
        }

        private static <K, V> void difference(Map<K, V> oldValue, Map<K, V> newValue, BiConsumer<K, V> removeHandler, BiConsumer<K, V> insertHandler, BiConsumer<K, V> updateHandler) {
            var removedEntities = new LinkedHashMap<>(oldValue);
            newValue.forEach((key, value) -> {
                var oldEntry = removedEntities.remove(key);
                if (oldEntry == null) {
                    if (insertHandler != null) {
                        insertHandler.accept(key, value);
                    }
                } else if (oldEntry != value) {
                    if (updateHandler != null) {
                        updateHandler.accept(key, value);
                    }
                }
            });
            if (removeHandler != null) {
                removedEntities.forEach(removeHandler);
            }
        }
    }

    private static class SimpleBuilder {

        private final OpenResourceKey registryName;

        private final List<IRegistryHolder<?>> entities = new ArrayList<>();
        private final List<OpenResourceKey> transformers = new ArrayList<>();

        private final Map<SkinSlotType, String> supports = new LinkedHashMap<>();

        private boolean isLocked = false;

        public SimpleBuilder(OpenResourceKey registryName) {
            this.registryName = registryName;
        }

        public static SimpleBuilder builtin(OpenResourceKey key) {
            var path = FileUtils.getRegistryName(key.path(), "skin/profiles/");
            return new SimpleBuilder(key.withPath("builtin/" + path));
        }

        public static SimpleBuilder custom(OpenResourceKey key) {
            var path = FileUtils.getRegistryName(key.path(), "skin/profiles/");
            return new SimpleBuilder(key.withPath(path));
        }

        public EntityProfile build() {
            return new EntityProfile(registryName, transformers, supports, isLocked);
        }
    }
}
