package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class SkinCubeTileEntity extends TileEntity implements IPaintable {

    protected EnumMap<Direction, IPaintColor> colors = new EnumMap<>(Direction.class);
    protected boolean customRenderer = false;

    public SkinCubeTileEntity() {
        super(ModTileEntities.SKIN_CUBE);
    }

    public void readFromNBT(CompoundNBT nbt) {
        colors.clear();
        if (nbt.contains(AWConstants.NBT.COLOR, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT colorNBT = nbt.getCompound(AWConstants.NBT.COLOR);
            colors.put(Direction.DOWN, AWDataSerializers.getPaintColor(colorNBT, AWConstants.NBT.DOWN, PaintColor.WHITE));
            colors.put(Direction.UP, AWDataSerializers.getPaintColor(colorNBT, AWConstants.NBT.UP, PaintColor.WHITE));
            colors.put(Direction.NORTH, AWDataSerializers.getPaintColor(colorNBT, AWConstants.NBT.NORTH, PaintColor.WHITE));
            colors.put(Direction.SOUTH, AWDataSerializers.getPaintColor(colorNBT, AWConstants.NBT.SOUTH, PaintColor.WHITE));
            colors.put(Direction.WEST, AWDataSerializers.getPaintColor(colorNBT, AWConstants.NBT.WEST, PaintColor.WHITE));
            colors.put(Direction.EAST, AWDataSerializers.getPaintColor(colorNBT, AWConstants.NBT.EAST, PaintColor.WHITE));
        }
        customRenderer = checkRendererFromColors();
    }

    public void writeToNBT(CompoundNBT nbt) {
        CompoundNBT colorNBT = new CompoundNBT();
        AWDataSerializers.putPaintColor(colorNBT, AWConstants.NBT.DOWN, getColor(Direction.DOWN), PaintColor.WHITE);
        AWDataSerializers.putPaintColor(colorNBT, AWConstants.NBT.UP, getColor(Direction.UP), PaintColor.WHITE);
        AWDataSerializers.putPaintColor(colorNBT, AWConstants.NBT.NORTH, getColor(Direction.NORTH), PaintColor.WHITE);
        AWDataSerializers.putPaintColor(colorNBT, AWConstants.NBT.SOUTH, getColor(Direction.SOUTH), PaintColor.WHITE);
        AWDataSerializers.putPaintColor(colorNBT, AWConstants.NBT.WEST, getColor(Direction.WEST), PaintColor.WHITE);
        AWDataSerializers.putPaintColor(colorNBT, AWConstants.NBT.EAST, getColor(Direction.EAST), PaintColor.WHITE);
        if (colorNBT.size() != 0) {
            nbt.put(AWConstants.NBT.COLOR, colorNBT);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.readFromNBT(nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        this.writeToNBT(nbt);
        return nbt;
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeToNBT(nbt);
        return new SUpdateTileEntityPacket(this.worldPosition, 3, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        this.readFromNBT(nbt);
        this.sendBlockUpdates();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.writeToNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        this.readFromNBT(tag);
    }

    private void sendBlockUpdates() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE);
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
        return colors.getOrDefault(direction, PaintColor.WHITE);
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        this.colors.put(direction, color);
        this.customRenderer = checkRendererFromColors();
        this.setChanged();
        this.sendBlockUpdates();
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        this.colors.putAll(colors);
        this.customRenderer = checkRendererFromColors();
        this.setChanged();
        this.sendBlockUpdates();
    }

    @Override
    public TileEntityType<?> getType() {
        if (customRenderer) {
            return ModTileEntities.SKIN_CUBE_SR;
        }
        return ModTileEntities.SKIN_CUBE;
    }
}