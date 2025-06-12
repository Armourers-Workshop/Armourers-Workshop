package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkinningTableBlockEntity extends UpdatableBlockEntity {

    private final SimpleContainer container = new SimpleContainer(3);

    private SkinDescriptor.Options options = SkinDescriptor.Options.DEFAULT;

    public SkinningTableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        container.deserialize(serializer);
        options = serializer.read(CodingKeys.OPTIONS);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        container.serialize(serializer);
        serializer.write(CodingKeys.OPTIONS, options);
    }

    public void setOptions(SkinDescriptor.Options options) {
        this.options = options;
    }

    public SkinDescriptor.Options options() {
        return options;
    }

    public SimpleContainer getContainer() {
        return container;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<SkinDescriptor.Options> OPTIONS = IDataSerializerKey.create("Options", SkinDescriptor.Options.CODEC, SkinDescriptor.Options.DEFAULT, SkinDescriptor.Options.DEFAULT::copy);
    }
}
