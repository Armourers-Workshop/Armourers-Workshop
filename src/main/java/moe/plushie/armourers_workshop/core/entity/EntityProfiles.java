package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.render.skin.*;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityProfiles {

    public static final EntityProfile PLAYER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::playerSlots)
            .add(SkinTypes.ARMOR_OUTFIT, EntityProfiles::playerSlots)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
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
            .add(SkinTypes.ARMOR_OUTFIT, 10)
            .build();

    public static final EntityProfile COMMON = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_OUTFIT, EntityProfiles::mobSlots)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
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
            .add(SkinTypes.ARMOR_OUTFIT, EntityProfiles::mobSlots)
            .build();

    public static final EntityProfile CHICKEN = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::mobSlots)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::mobSlots)
            .build();

    public static final EntityProfile SLIME = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::mobSlots)
            .build();

    public static final EntityProfile ARROW = Builder.create()
            .add(SkinTypes.ITEM_BOW, 1)
            .fixed()
            .build();

    private static final HashMap<EntityType<?>, EntityProfile> PROFILES = new HashMap<>();

    private static int playerSlots(ISkinType type) {
        return AWConfig.prefersWardrobeSlots;
    }

    private static int mobSlots(ISkinType type) {
        return AWConfig.prefersWardrobeMobSlots;
    }

    public static void init() {
        register(EntityType.PLAYER, EntityProfiles.PLAYER, () -> PlayerSkinRenderer::new);

        register(EntityType.VILLAGER, EntityProfiles.VILLAGER, () -> VillagerSkinRenderer::new);
        register(EntityType.WITCH, EntityProfiles.VILLAGER, () -> VillagerSkinRenderer::new);
        register(EntityType.WANDERING_TRADER, EntityProfiles.VILLAGER, () -> VillagerSkinRenderer::new);

        register(EntityType.SKELETON, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.STRAY, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.WITHER_SKELETON, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.ZOMBIE, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.HUSK, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.ZOMBIE_VILLAGER, EntityProfiles.COMMON, () -> ZombieVillagerSkinRenderer::new);
        register(EntityType.DROWNED, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);

        register(EntityType.EVOKER, EntityProfiles.COMMON, () -> IllagerSkinRenderer::new);
        register(EntityType.ILLUSIONER, EntityProfiles.COMMON, () -> IllagerSkinRenderer::new);
        register(EntityType.PILLAGER, EntityProfiles.COMMON, () -> IllagerSkinRenderer::new);
        register(EntityType.VINDICATOR, EntityProfiles.COMMON, () -> IllagerSkinRenderer::new);

        register(EntityType.VEX, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.PIGLIN, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.PIGLIN_BRUTE, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        register(EntityType.ZOMBIFIED_PIGLIN, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);

        register(EntityType.SLIME, EntityProfiles.SLIME, () -> SlimeSkinRenderer::new);
        register(EntityType.CHICKEN, EntityProfiles.CHICKEN, () -> ChickenSkinRenderer::new);
        register(EntityType.ARROW, EntityProfiles.ARROW, () -> ArrowSkinRenderer::new);

        register(EntityType.ARMOR_STAND, EntityProfiles.MANNEQUIN, () -> BipedSkinRenderer::new);
//        register(EntityType.IRON_GOLEM, EntityProfiles.MANNEQUIN, () -> IronGolemSkinRenderer::new);

        register(AWEntities.MANNEQUIN, EntityProfiles.MANNEQUIN, () -> PlayerSkinRenderer::new);

        // TODO: custom register
        register("customnpcs:customnpc", EntityProfiles.MANNEQUIN, () -> BipedSkinRenderer::new);
    }


    public static <T extends Entity, M extends Model> void register(EntityType<T> entityType, EntityProfile entityProfile, Supplier<Function<EntityProfile, SkinRenderer<T, M>>> provider) {
        PROFILES.put(entityType, entityProfile);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SkinRendererManager.getInstance().register(entityType, provider.get());
        });
    }

    public static <T extends Entity, M extends Model> void register(String registryName, EntityProfile entityProfile, Supplier<Function<EntityProfile, SkinRenderer<T, M>>> provider) {
        EntityType<?> entityType = EntityType.byString(registryName).orElse(null);
        if (entityType == null) {
            return;
        }
        PROFILES.put(entityType, entityProfile);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SkinRendererManager.getInstance().register((EntityType<T>) entityType, provider.get());
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
