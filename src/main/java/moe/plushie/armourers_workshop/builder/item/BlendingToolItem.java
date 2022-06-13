package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlendingToolItem extends AbstractPaintingToolItem implements IBlockPaintViewer {


    public BlendingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.INTENSITY);
        builder.accept(ToolOptions.RADIUS_SAMPLE);
        builder.accept(ToolOptions.RADIUS_EFFECT);
        //toolOptionList.add(ToolOptions.CHANGE_HUE);
        //toolOptionList.add(ToolOptions.CHANGE_SATURATION);
        //toolOptionList.add(ToolOptions.CHANGE_BRIGHTNESS);
        builder.accept(ToolOptions.PLANE_RESTRICT);
        //toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        int radiusSample = ToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.get(itemStack);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.sampleRadius", radiusSample, radiusSample, 1));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.effectRadius", radiusEffect, radiusEffect, 1));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public boolean applyColor(World world, BlockPos blockPos, Direction direction, IPaintUpdater updater, ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        TileEntity tileEntity = world.getBlockEntity(blockPos);
        if (!(tileEntity instanceof IPaintable)) {
            return false;
        }
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        int radiusSample = ToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.get(itemStack);
        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.get(itemStack);

        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(world, blockPos, direction, radiusSample, restrictPlane);
        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(world, blockPos, direction, radiusEffect, restrictPlane);

        if (blockSamples.size() == 0 | blockEffects.size() == 0) {
            return false;
        }

        int r = 0;
        int g = 0;
        int b = 0;

        int validSamples = 0;

        for (BlockPos posSample : blockSamples) {
            TileEntity targetEntity = world.getBlockEntity(posSample);
            if (targetEntity instanceof IPaintable) {
                IPaintColor color = ((IPaintable) targetEntity).getColor(direction);
                int rgb = color.getRGB();
                r += ColorUtils.getRed(rgb);
                g += ColorUtils.getGreen(rgb);
                b += ColorUtils.getBlue(rgb);
                validSamples++;
            }
        }

        if (validSamples == 0) {
            return false;
        }

        r = r / validSamples;
        g = g / validSamples;
        b = b / validSamples;

        for (BlockPos posEffect : blockEffects) {
            TileEntity targetEntity = world.getBlockEntity(posEffect);
            if (targetEntity instanceof IPaintable) {
                IPaintable target = (IPaintable) targetEntity;
                IPaintColor oldColor = target.getColor(direction);
                int oldRGB = oldColor.getRGB();
                int oldR = ColorUtils.getRed(oldRGB);
                int oldG = ColorUtils.getGreen(oldRGB);
                int oldB = ColorUtils.getBlue(oldRGB);

                float newR = r / 100F * intensity;
                newR += oldR / 100F * (100 - intensity);
                newR = MathHelper.clamp((int) newR, 0, 255);

                float newG = g / 100F * intensity;
                newG += oldG / 100F * (100 - intensity);
                newG = MathHelper.clamp((int) newG, 0, 255);

                float newB = b / 100F * intensity;
                newB += oldB / 100F * (100 - intensity);
                newB = MathHelper.clamp((int) newB, 0, 255);

                PaintColor newColor = PaintColor.of(ColorUtils.getRGB((int) newR, (int) newG, (int) newB), oldColor.getPaintType());
                updater.add(target, direction, newColor);
            }
        }
        return true;
    }

    @Override
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return ModSounds.PAINT;
    }
}
