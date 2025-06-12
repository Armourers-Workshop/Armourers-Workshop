package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ColorMixerBlockEntity extends UpdatableBlockEntity implements IPaintProvider {

    private SkinPaintColor color = SkinPaintColor.WHITE;

    public ColorMixerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        color = serializer.read(CodingKeys.COLOR);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.write(CodingKeys.COLOR, color);
    }

    @Override
    public SkinPaintColor color() {
        return color;
    }

    @Override
    public void setColor(SkinPaintColor color) {
        this.color = color;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<SkinPaintColor> COLOR = IDataSerializerKey.create("Color", SkinPaintColor.CODEC, SkinPaintColor.WHITE);
    }
}
