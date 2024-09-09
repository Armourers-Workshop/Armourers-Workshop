package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import moe.plushie.armourers_workshop.utils.NonNullItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkinningTableBlockEntity extends UpdatableContainerBlockEntity {

    private static final DataSerializerKey<SkinDescriptor.Options> OPTIONS_KEY = DataSerializerKey.create("Options", DataTypeCodecs.SKIN_OPTIONS, SkinDescriptor.Options.DEFAULT, SkinDescriptor.Options::new);

    private final NonNullItemList items = new NonNullItemList(3);

    private SkinDescriptor.Options options = SkinDescriptor.Options.DEFAULT;

    public SkinningTableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        items.deserialize(serializer);
        options = serializer.read(OPTIONS_KEY);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        items.serialize(serializer);
        serializer.write(OPTIONS_KEY, options);
    }

    public void setOptions(SkinDescriptor.Options options) {
        this.options = options;
    }

    public SkinDescriptor.Options getOptions() {
        return options;
    }

    @Override
    protected NonNullItemList getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return 3;
    }
}
