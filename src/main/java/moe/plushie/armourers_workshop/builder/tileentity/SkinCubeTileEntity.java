package moe.plushie.armourers_workshop.builder.tileentity;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.tileentity.AbstractTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class SkinCubeTileEntity extends AbstractTileEntity implements IPaintable {

    // Assume the mapping for facing to the north.
    private static final ImmutableMap<Direction, String> SIDES = ImmutableMap.<Direction, String>builder()
            .put(Direction.DOWN, AWConstants.NBT.SIDE_DOWN)
            .put(Direction.UP, AWConstants.NBT.SIDE_UP)
            .put(Direction.NORTH, AWConstants.NBT.SIDE_FRONT)
            .put(Direction.SOUTH, AWConstants.NBT.SIDE_BACK)
            .put(Direction.WEST, AWConstants.NBT.SIDE_LEFT)
            .put(Direction.EAST, AWConstants.NBT.SIDE_RIGHT)
            .build();

    protected final HashMap<String, IPaintColor> colors = new HashMap<>();
    protected boolean customRenderer = false;

    public SkinCubeTileEntity() {
        super(ModTileEntities.SKIN_CUBE);
    }

    public void readFromNBT(CompoundNBT nbt) {
        colors.clear();
        if (nbt.contains(AWConstants.NBT.COLOR, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT colorNBT = nbt.getCompound(AWConstants.NBT.COLOR);
            for (String name : SIDES.values()) {
                colors.put(name, AWDataSerializers.getPaintColor(colorNBT, name, PaintColor.WHITE));
            }
        }
        customRenderer = checkRendererFromColors();
    }

    public void writeToNBT(CompoundNBT nbt) {
        CompoundNBT colorNBT = new CompoundNBT();
        colors.forEach((name, color) -> AWDataSerializers.putPaintColor(colorNBT, name, color, PaintColor.WHITE));
        if (colorNBT.size() != 0) {
            nbt.put(AWConstants.NBT.COLOR, colorNBT);
        }
    }

    private String getSideName(Direction dir) {
        switch (getBlockState().getValue(SkinCubeBlock.FACING)) {
            case SOUTH: {
                // when block facing to south, we need to rotate 180° get facing north direction.
                return SIDES.get(Rotation.CLOCKWISE_180.rotate(dir));
            }
            case WEST: {
                // when block facing to west, we need to rotate 90° get facing north direction.
                return SIDES.get(Rotation.CLOCKWISE_90.rotate(dir));
            }
            case EAST: {
                // when block facing to east, we need to rotate -90° get facing north direction.
                return SIDES.get(Rotation.COUNTERCLOCKWISE_90.rotate(dir));
            }
            default: {
                return SIDES.get(dir);
            }
        }
    }

    private boolean checkRendererFromColors() {
        for (IPaintColor color : colors.values()) {
            if (color.getPaintType() != SkinPaintTypes.NORMAL) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        return colors.getOrDefault(getSideName(direction), PaintColor.WHITE);
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        this.colors.put(getSideName(direction), color);
        this.customRenderer = checkRendererFromColors();
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        colors.forEach((direction, color) -> this.colors.put(getSideName(direction), color));
        this.customRenderer = checkRendererFromColors();
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    @Override
    public TileEntityType<?> getType() {
        if (customRenderer) {
            return ModTileEntities.SKIN_CUBE_SR;
        }
        return super.getType();
    }
}