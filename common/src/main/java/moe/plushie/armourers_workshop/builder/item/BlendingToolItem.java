package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubeApplier;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.builder.other.CubeSelector;
import moe.plushie.armourers_workshop.builder.other.CubeWrapper;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlendingToolItem extends AbstractColoredToolItem implements IBlockPaintViewer {

    public BlendingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.INTENSITY);
        builder.accept(PaintingToolOptions.RADIUS_SAMPLE);
        builder.accept(PaintingToolOptions.RADIUS_EFFECT);
        //toolOptionList.add(ToolOptions.CHANGE_HUE);
        //toolOptionList.add(ToolOptions.CHANGE_SATURATION);
        //toolOptionList.add(ToolOptions.CHANGE_BRIGHTNESS);
        builder.accept(PaintingToolOptions.PLANE_RESTRICT);
        builder.accept(PaintingToolOptions.FULL_BLOCK_MODE);
    }

    protected CubeSelector createColorApplierSelector(int radius, UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        boolean restrictPlane = PaintingToolOptions.PLANE_RESTRICT.get(itemStack);
        boolean isFullMode = shouldUseFullMode(context);
        return CubeSelector.touching(context.getClickedPos(), radius, isFullMode, restrictPlane);
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(BlockEntity blockEntity, UseOnContext context) {
        if (blockEntity instanceof ArmourerBlockEntity) {
            return null;
        }
        return super.createPaintToolSelector(blockEntity, context);
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        int radiusEffect = PaintingToolOptions.RADIUS_EFFECT.get(itemStack);
        return createColorApplierSelector(radiusEffect, context);
    }

    @Override
    public IPaintToolAction createPaintToolAction(UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        int intensity = PaintingToolOptions.INTENSITY.get(itemStack);
        int radiusSample = PaintingToolOptions.RADIUS_SAMPLE.get(itemStack);
        // we need to complete sampling before we can use blending tool.
        ArrayList<Integer> colors = new ArrayList<>();
        CubeApplier applier = new CubeApplier(context.getLevel());
        createColorApplierSelector(radiusSample, context).forEach(context, (targetPos, dir) -> {
            CubeWrapper wrapper = applier.wrap(targetPos);
            if (wrapper.shouldChangeColor(dir)) {
                IPaintColor paintColor = wrapper.getColor(dir);
                if (paintColor != null) {
                    colors.add(paintColor.getRGB());
                }
            }
        });
        IPaintColor paintColor = PaintColor.of(ColorUtils.getAverageColor(colors), SkinPaintTypes.NORMAL);
        return new CubePaintingEvent.BlendingAction(paintColor, intensity);
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        int intensity = PaintingToolOptions.INTENSITY.get(itemStack);
        int radiusSample = PaintingToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = PaintingToolOptions.RADIUS_EFFECT.get(itemStack);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.sampleRadius", radiusSample, radiusSample, 1));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.effectRadius", radiusEffect, radiusEffect, 1));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public IRegistryKey<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PAINT;
    }
}
