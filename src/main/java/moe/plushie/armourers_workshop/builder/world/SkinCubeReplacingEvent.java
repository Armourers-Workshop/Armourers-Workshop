package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.utils.color.BlockPaintColor;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;

import java.util.HashMap;
import java.util.Objects;

public class SkinCubeReplacingEvent {

    public boolean keepColor = true;
    public boolean keepPaintType = true;

    public int changes = 0;
    public int blockChanges = 0;
    public int blockColorChanges = 0;

    public final ItemStack source;
    public final Block sourceBlock;
    public final BlockPaintColor sourceBlockColor;

    public final ItemStack destination;
    public final Block destinationBlock;
    public final BlockPaintColor destinationBlockColor;

    public final boolean isEmptySource;
    public final boolean isEmptyDestination;
    public final boolean isChangedBlock;

    public SkinCubeReplacingEvent(ItemStack source, ItemStack destination) {
        this.source = source;
        this.sourceBlock = getBlock(source);
        this.sourceBlockColor = getBlockColor(source);
        this.destination = destination;
        this.destinationBlock = getBlock(destination);
        this.destinationBlockColor = getBlockColor(destination);
        this.isEmptySource = sourceBlock == null && sourceBlockColor == null;
        this.isEmptyDestination = destinationBlock == null && destinationBlockColor == null;
        this.isChangedBlock = isChangedBlock();
    }

    public boolean accept(SkinCubeWrapper wrapper) {
        // security check, we only can modify the paintable block.
        if (!wrapper.is(IPaintable.class)) {
            return false;
        }
        // replace all block's to target block.
        if (source.isEmpty()) {
            return true;
        }
        // when specified block type we need to check matching.
        if (sourceBlock != null && !wrapper.is(sourceBlock)) {
            return false;
        }
        // when specified block color we need to check matching.
        if (sourceBlockColor != null) {
            int diff = 0;
            for (Direction dir : Direction.values()) {
                IPaintColor s = sourceBlockColor.getOrDefault(dir, PaintColor.WHITE);
                IPaintColor t = wrapper.getColor(dir);
                if (!Objects.equals(s, t)) {
                    diff += 1;
                }
            }
            // when changed block type we will require a strict color matching.
            if (isChangedBlock) {
                return diff < 1;
            }
            return diff < 6;
        }
        return true;
    }

    public void apply(SkinCubeWrapper wrapper) {
        // security check, we only can modify the paintable block.
        if (!wrapper.is(IPaintable.class)) {
            return;
        }
        int oldBlockChanges = blockChanges;
        int oldBlockColorChanges = blockColorChanges;
        // when specified new block color, we need to apply it first.
        if (!destination.isEmpty() && destinationBlockColor != null) {
            applyColor(wrapper);
        }
        // when specified new block type, we need to apply it.
        if (isChangedBlock) {
            applyBlock(wrapper);
        }
        // statistical change data.
        if (oldBlockChanges != blockChanges || oldBlockColorChanges != blockColorChanges) {
            changes += 1;
        }
    }

    private void applyColor(SkinCubeWrapper wrapper) {
        // when both keep color and keep paint type, we not need to color mix.
        if (keepColor && keepPaintType) {
            return;
        }
        // we just need to replace the matching block colors.
        HashMap<Direction, IPaintColor> newColors = new HashMap<>();
        for (Direction dir : Direction.values()) {
            IPaintColor targetColor = wrapper.getColor(dir);
            if (sourceBlockColor != null) {
                IPaintColor sourceColor = sourceBlockColor.getOrDefault(dir, PaintColor.WHITE);
                if (!Objects.equals(sourceColor, targetColor)) {
                    newColors.put(dir, targetColor);
                    continue;
                }
            }
            IPaintColor newColor = destinationBlockColor.getOrDefault(dir, PaintColor.WHITE);
            int color = newColor.getRGB();
            if (keepColor) {
                color = targetColor.getRGB();
            }
            ISkinPaintType paintType = newColor.getPaintType();
            if (keepPaintType) {
                paintType = targetColor.getPaintType();
            }
            newColor = PaintColor.of(color, paintType);
            newColors.put(dir, newColor);
        }
        // apply all block color changes into tile entity.
        wrapper.setColors(newColors);
        blockColorChanges += 1;
    }

    private void applyBlock(SkinCubeWrapper wrapper) {
        // security check, we only can modify the skin cube block.
        if (!wrapper.is(SkinCubeBlock.class)) {
            return;
        }
        CompoundNBT newNBT = null;
        BlockState oldState = wrapper.getBlockState();
        BlockState newState = Blocks.AIR.defaultBlockState();
        if (destinationBlock != null) {
            newNBT = wrapper.getBlockEntityNBT();
            newState = destinationBlock.defaultBlockState();
            for (Property<?> property : oldState.getProperties()) {
                newState = applyBlockState(newState, oldState, property);
            }
        }
        wrapper.setBlockState(newState, newNBT);
        blockChanges += 1;
    }

    private <T extends Comparable<T>> BlockState applyBlockState(BlockState newState, BlockState oldState, Property<T> property) {
        if (newState.hasProperty(property)) {
            return newState.setValue(property, oldState.getValue(property));
        }
        return newState;
    }

    private Block getBlock(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof SkinCubeItem) {
            return ((SkinCubeItem) item).getBlock();
        }
        return null;
    }

    private BlockPaintColor getBlockColor(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof SkinCubeItem) {
            return ((SkinCubeItem) item).getItemColors(itemStack);
        }
        if (item instanceof IItemColorProvider) {
            IPaintColor paintColor = ((IItemColorProvider) item).getItemColor(itemStack);
            if (paintColor != null) {
                return new BlockPaintColor(paintColor);
            }
        }
        return null;
    }

    private boolean isChangedBlock() {
        // replace all block's to target block.
        if (source.isEmpty() && destinationBlock != null) {
            return true;
        }
        // replace matching block's to air.
        if (destination.isEmpty() && sourceBlock != null) {
            return true;
        }
        // replace matching block's to target block.
        return destinationBlock != null && !destinationBlock.equals(sourceBlock);
    }
}
