package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.common.ModCompatible;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModEntities;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class EntityProfiles {

    public static final EntityProfile PLAYER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::playerSlots)
            .add(SkinTypes.OUTFIT, EntityProfiles::playerSlots)
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
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::mobSlots)
            .add(SkinTypes.OUTFIT, EntityProfiles::mobSlots)
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
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::mobSlots)
            .add(SkinTypes.OUTFIT, EntityProfiles::mobSlots)
            .build();

    public static final EntityProfile CHICKEN = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::mobSlots)
            .build();

    public static final EntityProfile ONLY_HEAD = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
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
        register(EntityType.PLAYER, EntityProfiles.PLAYER);

        register(EntityType.VILLAGER, EntityProfiles.VILLAGER);
        register(EntityType.WITCH, EntityProfiles.VILLAGER);
        register(EntityType.WANDERING_TRADER, EntityProfiles.VILLAGER);

        register(EntityType.SKELETON, EntityProfiles.COMMON);
        register(EntityType.STRAY, EntityProfiles.COMMON);
        register(EntityType.WITHER_SKELETON, EntityProfiles.COMMON);
        register(EntityType.ZOMBIE, EntityProfiles.COMMON);
        register(EntityType.HUSK, EntityProfiles.COMMON);
        register(EntityType.ZOMBIE_VILLAGER, EntityProfiles.COMMON);
        register(EntityType.DROWNED, EntityProfiles.COMMON);

        register(EntityType.EVOKER, EntityProfiles.COMMON);
        register(EntityType.ILLUSIONER, EntityProfiles.COMMON);
        register(EntityType.PILLAGER, EntityProfiles.COMMON);
        register(EntityType.VINDICATOR, EntityProfiles.COMMON);

        register(EntityType.VEX, EntityProfiles.COMMON);
        register(EntityType.PIGLIN, EntityProfiles.COMMON);
        register(EntityType.PIGLIN_BRUTE, EntityProfiles.COMMON);
        register(EntityType.ZOMBIFIED_PIGLIN, EntityProfiles.COMMON);

        register(EntityType.SLIME, EntityProfiles.ONLY_HEAD);
        register(EntityType.GHAST, EntityProfiles.ONLY_HEAD);
        register(EntityType.CHICKEN, EntityProfiles.CHICKEN);

        register(EntityType.ARROW, EntityProfiles.PROJECTING);
        register(EntityType.TRIDENT, EntityProfiles.PROJECTING);

//        register(EntityType.ARMOR_STAND, EntityProfiles.MANNEQUIN);
//        register(EntityType.IRON_GOLEM, EntityProfiles.MANNEQUIN);

        register(ModEntities.MANNEQUIN, EntityProfiles.MANNEQUIN);

        ModCompatible.registerCustomEntityType();
    }


    public static <T extends Entity, M extends Model> void register(EntityType<T> entityType, EntityProfile entityProfile) {
        PROFILES.put(entityType, entityProfile);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SkinRendererManager.getInstance().register(entityType, entityProfile);
        });
    }

    public static <T extends Entity, M extends Model> void register(String registryName, EntityProfile entityProfile) {
        EntityType<?> entityType = EntityType.byString(registryName).orElse(null);
        if (entityType == null) {
            return;
        }
        PROFILES.put(entityType, entityProfile);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SkinRendererManager.getInstance().register((EntityType<T>) entityType, entityProfile);
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
