package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.client.IBlockEntityRenderer;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IBlockEntityTypeBuilder<T extends BlockEntity> extends IRegistryBuilder<IBlockEntityType<T>> {

    IBlockEntityTypeBuilder<T> of(Supplier<Block> block);

    IBlockEntityTypeBuilder<T> bind(Supplier<IBlockEntityRenderer.Provider<T>> provider);
}



