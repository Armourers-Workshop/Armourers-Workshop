package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ColorMixerBlockEntity extends UpdatableBlockEntity implements IPaintProvider {

    private static final DataSerializerKey<IPaintColor> COLOR_KEY = DataSerializerKey.create("Color", DataTypeCodecs.PAINT_COLOR, PaintColor.WHITE);

    private IPaintColor color = PaintColor.WHITE;

    public ColorMixerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void readAdditionalData(IDataSerializer serializer) {
        color = serializer.read(COLOR_KEY);
    }

    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.write(COLOR_KEY, color);
    }

    @Override
    public IPaintColor getColor() {
        return color;
    }

    @Override
    public void setColor(IPaintColor color) {
        this.color = color;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }
}
