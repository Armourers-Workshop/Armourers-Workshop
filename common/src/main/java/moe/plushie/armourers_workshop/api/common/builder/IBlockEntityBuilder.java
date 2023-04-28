package moe.plushie.armourers_workshop.api.common.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityKey;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public interface IBlockEntityBuilder<T extends BlockEntity> extends IEntryBuilder<IBlockEntityKey<T>> {

    IBlockEntityBuilder<T> of(Supplier<Block> block);

    IBlockEntityBuilder<T> bind(Supplier<AbstractBlockEntityRendererProvider<T>> provider);
}



