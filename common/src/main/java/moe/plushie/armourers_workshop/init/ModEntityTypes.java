package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.SeatEntityRenderer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

public final class ModEntityTypes {

    public static final IRegistryKey<IEntityType<MannequinEntity>> MANNEQUIN = normal(MannequinEntity::new).fixed(0.6f, 1.88f).bind(() -> MannequinEntityRenderer::new).build(ModConstants.ENTITY_MANNEQUIN);
    public static final IRegistryKey<IEntityType<SeatEntity>> SEAT = normal(SeatEntity::new).fixed(0.0f, 0.0f).noSummon().bind(() -> SeatEntityRenderer::new).build(ModConstants.ENTITY_SEAT);

    private static <T extends Entity> IEntityTypeBuilder<T> normal(IEntityType.Serializer<T> entityFactory) {
        return BuilderManager.getInstance().createEntityTypeBuilder(entityFactory, MobCategory.MISC);
    }

    public static void init() {
    }
}
