package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.tileentity.AbstractTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.nbt.CompoundNBT;

@SuppressWarnings("NullableProblems")
public class ColorMixerTileEntity extends AbstractTileEntity implements IPaintProvider {

    private IPaintColor color = PaintColor.WHITE;

    public ColorMixerTileEntity() {
        super(ModTileEntities.COLOR_MIXER);
    }

    public void readFromNBT(CompoundNBT nbt) {
        color = AWDataSerializers.getPaintColor(nbt, AWConstants.NBT.COLOR, PaintColor.WHITE);
    }

    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putPaintColor(nbt, AWConstants.NBT.COLOR, color, PaintColor.WHITE);
    }

    @Override
    public IPaintColor getColor() {
        return color;
    }

    @Override
    public void setColor(IPaintColor color) {
        this.color = color;
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }
}
