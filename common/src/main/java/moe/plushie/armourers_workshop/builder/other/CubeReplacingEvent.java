package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Objects;

public class CubeReplacingEvent {

    public final ItemStack source;
    public final Block sourceBlock;
    public final BlockPaintColor sourceBlockColor;
    public final ItemStack destination;
    public final Block destinationBlock;
    public final BlockPaintColor destinationBlockColor;
    public final boolean isEmptySource;
    public final boolean isEmptyDestination;
    public final boolean isChangedBlock;
    public boolean keepColor = true;
    public boolean keepPaintType = true;
    public int changes = 0;
    public int blockChanges = 0;
    public int blockColorChanges = 0;

    public CubeReplacingEvent(ItemStack source, ItemStack destination) {
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

    public boolean accept(CubeWrapper cube) {
        // security check, we only can modify the paintable block.
        if (!cube.is(IPaintable.class)) {
            return false;
        }
        // replace all block's to target block.
        if (source.isEmpty()) {
            return true;
        }
        // when specified block type we need to check matching.
        if (sourceBlock != null && !cube.is(sourceBlock)) {
            return false;
        }
        // when specified block color we need to check matching.
        if (sourceBlockColor != null) {
            var diff = 0;
            for (var dir : Direction.values()) {
                var s = sourceBlockColor.getOrDefault(dir, PaintColor.WHITE);
                var t = cube.getColor(dir);
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

    public void apply(CubeWrapper cube) {
        // security check, we only can modify the paintable block.
        if (!cube.is(IPaintable.class)) {
            return;
        }
        var oldBlockChanges = blockChanges;
        var oldBlockColorChanges = blockColorChanges;
        // when specified new block color, we need to apply it first.
        if (!destination.isEmpty() && destinationBlockColor != null) {
            applyColor(cube);
        }
        // when specified new block type, we need to apply it.
        if (isChangedBlock) {
            applyBlock(cube);
        }
        // statistical change data.
        if (oldBlockChanges != blockChanges || oldBlockColorChanges != blockColorChanges) {
            changes += 1;
        }
    }

    private void applyColor(CubeWrapper cube) {
        // when both keep color and keep paint type, we not need to color mix.
        if (keepColor && keepPaintType) {
            return;
        }
        // we just need to replace the matching block colors.
        var newColors = new HashMap<Direction, IPaintColor>();
        for (var dir : Direction.values()) {
            var targetColor = cube.getColor(dir);
            if (sourceBlockColor != null) {
                var sourceColor = sourceBlockColor.getOrDefault(dir, PaintColor.WHITE);
                if (!Objects.equals(sourceColor, targetColor)) {
                    newColors.put(dir, targetColor);
                    continue;
                }
            }
            var newColor = destinationBlockColor.getOrDefault(dir, PaintColor.WHITE);
            var color = newColor.getRGB();
            if (keepColor) {
                color = targetColor.getRGB();
            }
            var paintType = newColor.getPaintType();
            if (keepPaintType) {
                paintType = targetColor.getPaintType();
            }
            newColor = PaintColor.of(color, paintType);
            newColors.put(dir, newColor);
        }
        // apply all block color changes into tile entity.
        cube.setColors(newColors);
        blockColorChanges += 1;
    }

    private void applyBlock(CubeWrapper cube) {
        // security check, we only can modify the skin cube block.
        if (!cube.is(SkinCubeBlock.class)) {
            return;
        }
        CompoundTag newNBT = null;
        var oldState = cube.getBlockState();
        var newState = Blocks.AIR.defaultBlockState();
        if (destinationBlock != null) {
            newNBT = cube.getBlockTag();
            newState = destinationBlock.defaultBlockState();
            for (var property : oldState.getProperties()) {
                newState = applyBlockState(newState, oldState, property);
            }
        }
        cube.setBlockStateAndTag(newState, newNBT);
        blockChanges += 1;
    }

    private <T extends Comparable<T>> BlockState applyBlockState(BlockState newState, BlockState oldState, Property<T> property) {
        if (newState.hasProperty(property)) {
            return newState.setValue(property, oldState.getValue(property));
        }
        return newState;
    }

    private Block getBlock(ItemStack itemStack) {
        var item = itemStack.getItem();
        if (item instanceof SkinCubeItem cubeItem) {
            return cubeItem.getBlock();
        }
        return null;
    }

    private BlockPaintColor getBlockColor(ItemStack itemStack) {
        var item = itemStack.getItem();
        if (item instanceof SkinCubeItem cubeItem) {
            return cubeItem.getItemColors(itemStack);
        }
        if (item instanceof IItemColorProvider provider) {
            var paintColor = provider.getItemColor(itemStack);
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
