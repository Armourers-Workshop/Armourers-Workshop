package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface IBlockEntityBuilder<T extends BlockEntity> extends IEntryBuilder<IRegistryObject<BlockEntityType<T>>> {

    IBlockEntityBuilder<T> of(Supplier<Block> block);

    IBlockEntityBuilder<T> bind(Supplier<IBlockEntityRendererProvider<T>> provider);
}


