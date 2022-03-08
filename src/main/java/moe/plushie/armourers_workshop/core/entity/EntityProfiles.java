package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.render.renderer.*;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
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

    private static final HashMap<EntityType<?>, EntityProfile> PROFILES = new HashMap<>();

    public static final EntityProfile PLAYER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, EntityProfiles::fromConfig)
            .add(SkinTypes.ARMOR_CHEST, EntityProfiles::fromConfig)
            .add(SkinTypes.ARMOR_LEGS, EntityProfiles::fromConfig)
            .add(SkinTypes.ARMOR_FEET, EntityProfiles::fromConfig)
            .add(SkinTypes.ARMOR_WINGS, EntityProfiles::fromConfig)
            .add(SkinTypes.ARMOR_OUTFIT, EntityProfiles::fromConfig)
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
            .add(SkinTypes.ARMOR_HEAD, 3)
            .add(SkinTypes.ARMOR_CHEST, 3)
            .add(SkinTypes.ARMOR_LEGS, 3)
            .add(SkinTypes.ARMOR_FEET, 3)
            .add(SkinTypes.ARMOR_WINGS, 3)
            .add(SkinTypes.ARMOR_OUTFIT, 3)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
            .add(SkinTypes.TOOL_AXE, 1)
            .add(SkinTypes.TOOL_HOE, 1)
            .add(SkinTypes.TOOL_PICKAXE, 1)
            .add(SkinTypes.TOOL_SHOVEL, 1)
            .build();

    public static final EntityProfile VILLAGER = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, 3)
            .add(SkinTypes.ARMOR_CHEST, 3)
            .add(SkinTypes.ARMOR_LEGS, 3)
            .add(SkinTypes.ARMOR_FEET, 3)
            .add(SkinTypes.ARMOR_WINGS, 3)
            .add(SkinTypes.ARMOR_OUTFIT, 3)
            .build();

    public static final EntityProfile SLIME = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, 3)
            .build();


    public static final EntityProfile CHICKEN = Builder.create()
            .add(SkinTypes.ARMOR_HEAD, 3)
            .add(SkinTypes.ARMOR_CHEST, 3)
            .add(SkinTypes.ARMOR_FEET, 3)
            .add(SkinTypes.ARMOR_LEGS, 3)
            .add(SkinTypes.ARMOR_WINGS, 3)
            .build();

    public static final EntityProfile ARROW = Builder.create()
            .add(SkinTypes.ITEM_BOW, 1)
            .fixed()
            .build();

    private static int fromConfig(ISkinType type) {
        return AWConfig.prefersWardrobeSlots;
    }

    public static void init() {
        EntityProfiles.register(EntityType.PLAYER, EntityProfiles.PLAYER, () -> BipedSkinRenderer::new);
        EntityProfiles.register(EntityType.ARROW, EntityProfiles.ARROW, () -> ArrowSkinRenderer::new);

        EntityProfiles.register(EntityType.VILLAGER, EntityProfiles.VILLAGER, () -> VillagerSkinRenderer::new);
        EntityProfiles.register(EntityType.WITCH, EntityProfiles.VILLAGER, () -> VillagerSkinRenderer::new);

        EntityProfiles.register(EntityType.SKELETON, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        EntityProfiles.register(EntityType.WITHER_SKELETON, EntityProfiles.COMMON, () -> BipedSkinRenderer::new); // 1.2x

        EntityProfiles.register(EntityType.PILLAGER, EntityProfiles.COMMON, () -> IllagerSkinRenderer::new);
        EntityProfiles.register(EntityType.ILLUSIONER, EntityProfiles.COMMON, () -> IllagerSkinRenderer::new);

        EntityProfiles.register(EntityType.ZOMBIE, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        EntityProfiles.register(EntityType.ZOMBIE_VILLAGER, EntityProfiles.COMMON, () -> ZombieVillagerSkinRenderer::new);

        EntityProfiles.register(EntityType.SLIME, EntityProfiles.SLIME, () -> SlimeSkinRenderer::new);
        EntityProfiles.register(EntityType.CHICKEN, EntityProfiles.CHICKEN, () -> ChickenSkinRenderer::new);

        EntityProfiles.register(EntityType.PIGLIN, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);
        EntityProfiles.register(EntityType.ZOMBIFIED_PIGLIN, EntityProfiles.COMMON, () -> BipedSkinRenderer::new);

        EntityProfiles.register(AWEntities.MANNEQUIN, EntityProfiles.MANNEQUIN, () -> BipedSkinRenderer::new);
    }


    public static <T extends Entity, M extends Model> void register(EntityType<T> entityType, EntityProfile entityProfile, Supplier<Function<EntityProfile, SkinRenderer<T, M>>> provider) {
        PROFILES.put(entityType, entityProfile);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SkinRendererManager.getInstance().register(entityType, provider.get());
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

        private boolean isFixed = false;
        private final HashMap<ISkinType, Function<ISkinType, Integer>> supports = new HashMap<>();

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
            isFixed = true;
            return this;
        }

        public EntityProfile build() {
            return new EntityProfile(supports, isFixed);
        }
    }
}
