package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IEntityTypeBuilder<T extends Entity> extends IRegistryBuilder<IEntityType<T>> {

    IEntityTypeBuilder<T> fixed(float f, float g);

    IEntityTypeBuilder<T> noSummon();

    IEntityTypeBuilder<T> noSave();

    IEntityTypeBuilder<T> fireImmune();

    IEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks);

    IEntityTypeBuilder<T> spawnableFarFromPlayer();

    IEntityTypeBuilder<T> clientTrackingRange(int i);

    IEntityTypeBuilder<T> updateInterval(int i);

    IEntityTypeBuilder<T> bind(Supplier<IEntityRenderer.Provider<T>> provider);
}
