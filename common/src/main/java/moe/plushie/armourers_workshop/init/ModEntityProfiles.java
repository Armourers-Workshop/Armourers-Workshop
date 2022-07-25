package moe.plushie.armourers_workshop.init;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentType;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class ModEntityProfiles {

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
            .build();

    public static final EntityProfile MANNEQUIN = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.OUTFIT, 10)
            .build();

    public static final EntityProfile COMMON = Builder.create()
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
            .build();

    public static final EntityProfile VILLAGER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.OUTFIT, ModEntityProfiles::mobSlots)
            .build();

    public static final EntityProfile CHICKEN = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, ModEntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, ModEntityProfiles::mobSlots)
            .build();

    public static final EntityProfile ONLY_HEAD = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, ModEntityProfiles::mobSlots)
            .build();

    public static final EntityProfile PROJECTING = Builder.create()
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_TRIDENT, 1)
            .fixed()
            .build();

    private static final HashMap<EntityType<?>, EntityProfile> PROFILES = new HashMap<>();

    private static int playerSlots(ISkinType type) {
        return ModConfig.Common.prefersWardrobePlayerSlots;
    }

    private static int mobSlots(ISkinType type) {
        return ModConfig.Common.prefersWardrobeMobSlots;
    }

    public static void init() {
        register(EntityType.PLAYER, ModEntityProfiles.PLAYER);

        register(EntityType.VILLAGER, ModEntityProfiles.VILLAGER);
        register(EntityType.WITCH, ModEntityProfiles.VILLAGER);
        register(EntityType.WANDERING_TRADER, ModEntityProfiles.VILLAGER);

        register(EntityType.SKELETON, ModEntityProfiles.COMMON);
        register(EntityType.STRAY, ModEntityProfiles.COMMON);
        register(EntityType.WITHER_SKELETON, ModEntityProfiles.COMMON);
        register(EntityType.ZOMBIE, ModEntityProfiles.COMMON);
        register(EntityType.HUSK, ModEntityProfiles.COMMON);
        register(EntityType.ZOMBIE_VILLAGER, ModEntityProfiles.COMMON);
        register(EntityType.DROWNED, ModEntityProfiles.COMMON);

        register(EntityType.EVOKER, ModEntityProfiles.COMMON);
        register(EntityType.ILLUSIONER, ModEntityProfiles.COMMON);
        register(EntityType.PILLAGER, ModEntityProfiles.COMMON);
        register(EntityType.VINDICATOR, ModEntityProfiles.COMMON);

        register(EntityType.VEX, ModEntityProfiles.COMMON);
        register(EntityType.PIGLIN, ModEntityProfiles.COMMON);
        register(EntityType.PIGLIN_BRUTE, ModEntityProfiles.COMMON);
        register(EntityType.ZOMBIFIED_PIGLIN, ModEntityProfiles.COMMON);

        register(EntityType.SLIME, ModEntityProfiles.ONLY_HEAD);
        register(EntityType.GHAST, ModEntityProfiles.ONLY_HEAD);
//        register(EntityType.CHICKEN, ModEntityProfiles.CHICKEN);

        register(EntityType.ARROW, ModEntityProfiles.PROJECTING);
        register(EntityType.TRIDENT, ModEntityProfiles.PROJECTING);

//        register(EntityType.ARMOR_STAND, EntityProfiles.MANNEQUIN);
//        register(EntityType.IRON_GOLEM, EntityProfiles.MANNEQUIN);

        register(ModEntities.MANNEQUIN.get(), ModEntityProfiles.MANNEQUIN);

        ModCompatible.registerCustomEntityType();
    }


    public static <T extends Entity, M extends Model> void register(EntityType<T> entityType, EntityProfile entityProfile) {
        PROFILES.put(entityType, entityProfile);
        EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, () -> () -> {
            SkinRendererManager.getInstance().register(entityType, entityProfile);
        });
    }

    public static <T extends Entity, M extends Model> void register(String registryName, EntityProfile entityProfile) {
        EntityType<?> entityType = EntityType.byString(registryName).orElse(null);
        if (entityType == null) {
            return;
        }
        PROFILES.put(entityType, entityProfile);
        EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, () -> () -> {
                SkinRendererManager.getInstance().register((EntityType<?>) entityType, entityProfile);
        });
    }


    @Nullable
    public static <T extends Entity> EntityProfile getProfile(T entity) {
        return getProfile(entity.getType());
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(EntityType<T> entityType) {
        return PROFILES.get(entityType);
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

        public EntityProfile build() {
            return new EntityProfile(supports, editable);
        }
    }
}
