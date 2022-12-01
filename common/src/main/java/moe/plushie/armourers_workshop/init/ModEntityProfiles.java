package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModEntityProfiles {

    private static final HashMap<String, EntityProfile> ALL_PROFILES = new HashMap<>();
    private static final HashMap<EntityType<?>, EntityProfile> ENTITY_PROFILES = new HashMap<>();

    public static final EntityProfile PLAYER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_CHEST, ModEntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_LEGS, ModEntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_FEET, ModEntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_WINGS, ModEntityProfiles::playerSlots)
            .add(SkinTypes.OUTFIT, ModEntityProfiles::playerSlots)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
            .add(SkinTypes.ITEM_TRIDENT, 1)
            .add(SkinTypes.TOOL_AXE, 1)
            .add(SkinTypes.TOOL_HOE, 1)
            .add(SkinTypes.TOOL_PICKAXE, 1)
            .add(SkinTypes.TOOL_SHOVEL, 1)
            .build("player");

    public static final EntityProfile MANNEQUIN = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.OUTFIT, 10)
            .build("mannequin");

    public static final EntityProfile CUSTOM = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.OUTFIT, 10)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
            .add(SkinTypes.ITEM_TRIDENT, 1)
            .add(SkinTypes.TOOL_AXE, 1)
            .add(SkinTypes.TOOL_HOE, 1)
            .add(SkinTypes.TOOL_PICKAXE, 1)
            .add(SkinTypes.TOOL_SHOVEL, 1)
            .build("custom");

    public static final EntityProfile MOB = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.OUTFIT, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
            .add(SkinTypes.ITEM_TRIDENT, 1)
            .add(SkinTypes.TOOL_AXE, 1)
            .add(SkinTypes.TOOL_HOE, 1)
            .add(SkinTypes.TOOL_PICKAXE, 1)
            .add(SkinTypes.TOOL_SHOVEL, 1)
            .build("mob");

    public static final EntityProfile VILLAGER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.OUTFIT, ModEntityProfiles::mobSlots)
            .build("villager");

    public static final EntityProfile ONLY_HEAD = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::mobSlots)
            .build("only_head");

    public static final EntityProfile PROJECTING = Builder.create()
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_TRIDENT, 1)
            .fixed()
            .build("projecting");

    private static int playerSlots(ISkinType type) {
        return ModConfig.Common.prefersWardrobePlayerSlots;
    }

    private static int mobSlots(ISkinType type) {
        return ModConfig.Common.prefersWardrobeMobSlots;
    }

    public static void init() {
        register(EntityType.PLAYER, PLAYER);

        register(EntityType.VILLAGER, VILLAGER);
        register(EntityType.WITCH, VILLAGER);
        register(EntityType.WANDERING_TRADER, VILLAGER);

        register(EntityType.SKELETON, MOB);
        register(EntityType.STRAY, MOB);
        register(EntityType.WITHER_SKELETON, MOB);
        register(EntityType.ZOMBIE, MOB);
        register(EntityType.HUSK, MOB);
        register(EntityType.ZOMBIE_VILLAGER, MOB);
        register(EntityType.DROWNED, MOB);

        register(EntityType.EVOKER, MOB);
        register(EntityType.ILLUSIONER, MOB);
        register(EntityType.PILLAGER, MOB);
        register(EntityType.VINDICATOR, MOB);

        register(EntityType.VEX, MOB);
        register(EntityType.PIGLIN, MOB);
        register(EntityType.PIGLIN_BRUTE, MOB);
        register(EntityType.ZOMBIFIED_PIGLIN, MOB);

        register(EntityType.SLIME, ONLY_HEAD);
        register(EntityType.GHAST, ONLY_HEAD);
        register(EntityType.CHICKEN, ONLY_HEAD);
        register(EntityType.CREEPER, ONLY_HEAD);

        register(EntityType.ARROW, PROJECTING);
        register(EntityType.TRIDENT, PROJECTING);

//        register(EntityType.ARMOR_STAND, EntityProfiles.MANNEQUIN);
        register(EntityType.IRON_GOLEM, MANNEQUIN);

        register(ModEntityTypes.MANNEQUIN.get(), MANNEQUIN);

        ModCompatible.registerCustomEntityType();
    }

    public static void register(EntityType<?> entityType, EntityProfile entityProfile) {
        ModLog.debug("Registering Entity Profile '{}'", Registry.ENTITY_TYPE.getKey(entityType));
        ENTITY_PROFILES.put(entityType, entityProfile);
    }

    public static void register(String registryName, EntityProfile entityProfile) {
        register(registryName, entityProfile, null);
    }

    public static void register(String registryName, EntityProfile entityProfile, Consumer<EntityType<?>> consumer) {
        EntityType.byString(registryName).ifPresent(entityType -> {
            register(entityType, entityProfile);
            if (consumer != null) {
                consumer.accept(entityType);
            }
        });
    }

    public static void forEach(BiConsumer<EntityType<?>, EntityProfile> consumer) {
        ENTITY_PROFILES.forEach(consumer);
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(T entity) {
        return getProfile(entity.getType());
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(EntityType<T> entityType) {
        return ENTITY_PROFILES.get(entityType);
    }

    @Nullable
    public static EntityProfile getProfile(ResourceLocation registryName) {
        return ALL_PROFILES.get(registryName.toString());
    }

    public static class Builder<T> {

        private final HashMap<ISkinType, Function<ISkinType, Integer>> supports = new HashMap<>();
        private boolean editable = true;

        public static <T extends Entity> Builder<T> create() {
            return new Builder<>();
        }


        private Builder<T> add(ISkinType type, Function<ISkinType, Integer> f) {
            supports.put(type, f);
            return this;
        }

        private Builder<T> add(ISkinType type, int maxCount) {
            supports.put(type, t -> maxCount);
            return this;
        }

        private Builder<T> fixed() {
            editable = false;
            return this;
        }

        public EntityProfile build(String registryName) {
            EntityProfile profile = new EntityProfile(ModConstants.key(registryName), supports, editable);
            ALL_PROFILES.put(profile.getRegistryName().toString(), profile);
            return profile;
        }
    }
}
