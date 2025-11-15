package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedList;
import java.util.function.Supplier;

public abstract class AbstractBlockEntityTypeBuilder<T extends BlockEntity> {

    protected final LinkedList<Supplier<Block>> blocks = new LinkedList<>();
    protected final IBlockEntityType.Serializer<T> serializer;

    public AbstractBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer) {
        this.serializer = serializer;
    }

    public void add(Supplier<Block> block) {
        this.blocks.add(block);
    }

    public abstract IBlockEntityType<T> build(OpenResourceLocation registryName);

    protected abstract static class Proxy<T extends BlockEntity> implements IBlockEntityType<T> {

        protected final BlockEntityType<T> entityType;

        public Proxy(BlockEntityType<T> entityType) {
            this.entityType = entityType;
        }

        @Override
        public BlockEntityType<T> get() {
            return entityType;
        }
    }
}
