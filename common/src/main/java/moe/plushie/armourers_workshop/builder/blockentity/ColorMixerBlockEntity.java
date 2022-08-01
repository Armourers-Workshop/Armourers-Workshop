package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.init.ModBlockEntities;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ColorMixerBlockEntity extends AbstractBlockEntity implements IPaintProvider {

    private IPaintColor color = PaintColor.WHITE;

    public ColorMixerBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    public void readFromNBT(CompoundTag nbt) {
        color = DataSerializers.getPaintColor(nbt, Constants.Key.COLOR, PaintColor.WHITE);
    }

    public void writeToNBT(CompoundTag nbt) {
        DataSerializers.putPaintColor(nbt, Constants.Key.COLOR, color, PaintColor.WHITE);
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
