package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.SeatEntityRenderer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ModEntities {

    public static final IRegistryObject<EntityType<MannequinEntity>> MANNEQUIN = normal(MannequinEntity::new).fixed(0.6f, 1.88f).bind(() -> MannequinEntityRenderer::new).build("mannequin");
    public static final IRegistryObject<EntityType<SeatEntity>> SEAT = normal(SeatEntity::new).fixed(0.0f, 0.0f).noSummon().bind(() -> SeatEntityRenderer::new).build("seat");

    public static boolean noBlockEntitiesAround(Entity entity) {
        if (entity.level instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) entity.level;
            AABB alignedBB = entity.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D);
            return world.getEntityCollisions(entity, alignedBB, e -> e instanceof MannequinEntity).allMatch(VoxelShape::isEmpty);
        }
        return true;
    }

    private static <T extends Entity> IEntityTypeBuilder<T> normal(EntityType.EntityFactory<T> entityFactory) {
        return BuilderManager.getInstance().createEntityTypeBuilder(entityFactory, MobCategory.MISC);
    }

    public static void init() {
    }
}
