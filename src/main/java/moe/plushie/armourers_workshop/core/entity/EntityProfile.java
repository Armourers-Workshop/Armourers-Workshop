package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class EntityProfile<T extends Entity> {

    private static final HashMap<EntityType<?>, EntityProfile<?>> PROFILES = new HashMap<>();

    public static final EntityProfile<PlayerEntity> PLAYER = register(EntityType.PLAYER)
            .add(SkinTypes.ARMOR_HEAD, EntityProfile::fromConfig)
            .add(SkinTypes.ARMOR_CHEST, EntityProfile::fromConfig)
            .add(SkinTypes.ARMOR_LEGS, EntityProfile::fromConfig)
            .add(SkinTypes.ARMOR_FEET, EntityProfile::fromConfig)
            .add(SkinTypes.ARMOR_WINGS, EntityProfile::fromConfig)
            .add(SkinTypes.ARMOR_OUTFIT, EntityProfile::fromConfig)
            .add(SkinTypes.ITEM_BOW, 1)
            .add(SkinTypes.ITEM_SWORD, 1)
            .add(SkinTypes.ITEM_SHIELD, 1)
            .add(SkinTypes.TOOL_AXE, 1)
            .add(SkinTypes.TOOL_HOE, 1)
            .add(SkinTypes.TOOL_PICKAXE, 1)
            .add(SkinTypes.TOOL_SHOVEL, 1);

    public static final EntityProfile<MannequinEntity> MANNEQUIN = register(AWEntities.MANNEQUIN)
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.ARMOR_OUTFIT, 10);

    public static final EntityProfile<VillagerEntity> VILLAGER = register(EntityType.VILLAGER)
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.ARMOR_OUTFIT, 10);

    public static final EntityProfile<ZombieEntity> ZOMBIE = register(EntityType.ZOMBIE)
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.ARMOR_OUTFIT, 10);

    public static final EntityProfile<SkeletonEntity> SKELETON = register(EntityType.SKELETON)
            .add(SkinTypes.ARMOR_HEAD, 10)
            .add(SkinTypes.ARMOR_CHEST, 10)
            .add(SkinTypes.ARMOR_LEGS, 10)
            .add(SkinTypes.ARMOR_FEET, 10)
            .add(SkinTypes.ARMOR_WINGS, 10)
            .add(SkinTypes.ARMOR_OUTFIT, 10)
            .add(SkinTypes.ITEM_BOW, 1);

    public static final EntityProfile<SlimeEntity> SLIME = register(EntityType.SLIME)
            .add(SkinTypes.ARMOR_HEAD, 10);


    public static final EntityProfile<ArrowEntity> ARROW = register(EntityType.ARROW)
            .add(SkinTypes.ITEM_BOW, 1)
            .fixed();
    private final HashMap<ISkinType, Function<ISkinType, Integer>> supports = new HashMap<>();
    private EntityType<T> entityType;
    private boolean isFixed = false;

    private static int fromConfig(ISkinType type) {
        return AWConfig.prefersWardrobeSlots;
    }

    private static <T extends Entity> EntityProfile<T> register(EntityType<T> entityType) {
        EntityProfile<T> profile = new EntityProfile<>();
        profile.entityType = entityType;
        PROFILES.put(entityType, profile);
        return profile;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends Entity> EntityProfile<T> getProfile(T entity) {
        return (EntityProfile<T>) PROFILES.get(entity.getType());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends Entity> EntityProfile<T> getProfile(EntityType<T> entityType) {
        return (EntityProfile<T>) PROFILES.get(entityType);
    }

    private EntityProfile<T> add(ISkinType type, Function<ISkinType, Integer> f) {
        supports.put(type, f);
        return this;
    }

    private EntityProfile<T> add(ISkinType type, int maxCount) {
        supports.put(type, t -> maxCount);
        return this;
    }

    private EntityProfile<T> fixed() {
        isFixed = true;
        return this;
    }

    public EntityType<T> getEntityType() {
        return entityType;
    }

    public boolean canCustomize() {
        return !isFixed;
    }

    public boolean canSupport(ISkinType type) {
        return supports.containsKey(type);
    }

    public int getMaxCount(ISkinType type) {
        Function<ISkinType, Integer> provider = supports.get(type);
        if (provider != null) {
            return provider.apply(type);
        }
        return 0;
    }

}
