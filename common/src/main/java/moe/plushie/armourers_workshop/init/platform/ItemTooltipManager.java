package moe.plushie.armourers_workshop.init.platform;

import com.apple.library.coregraphics.CGRect;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentType;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.init.*;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@Environment(value = EnvType.CLIENT)
public class ItemTooltipManager {

    public static ArrayList<Component> createSkinInfo(BakedSkin bakedSkin) {
        Skin skin = bakedSkin.getSkin();
        ArrayList<Component> tooltip = new ArrayList<>();
        if (Strings.isNotBlank(skin.getCustomName().trim())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
        }
        if (ModConfig.Client.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }
        tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", TranslateUtils.Name.of(skin.getType())));
        return tooltip;
    }

    public static ArrayList<Component> createSkinTooltip(ItemStack itemStack) {
        boolean isItemOwner = itemStack.getItem() == ModItems.SKIN.get();
        ArrayList<Component> tooltip = new ArrayList<>();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            if (isItemOwner) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinInvalidItem"));
            }
            return tooltip;
        }
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skindownloading", descriptor.getIdentifier()));
            return tooltip;
        }
        Skin skin = bakedSkin.getSkin();
        SkinUsedCounter counter = bakedSkin.getUsedCounter();

        if (!isItemOwner) {
            if (ModConfig.Client.tooltipHasSkin) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hasSkin"));
            }
            if (ModConfig.Client.tooltipSkinName && Strings.isNotBlank(skin.getCustomName())) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
            }
        }

        if (isItemOwner && ModConfig.Client.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (ModConfig.Client.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }

        if (ModConfig.Client.tooltipSkinType) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", TranslateUtils.Name.of(skin.getType())));
        }

        if (!isItemOwner && ModConfig.Client.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (ModDebugger.tooltip && !Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        if (ModDebugger.tooltip && Screen.hasShiftDown()) {

            String totals = String.format("%d/%d/%d/%d",
                    counter.getCubeTotal(SkinCubes.SOLID),
                    counter.getCubeTotal(SkinCubes.GLOWING),
                    counter.getCubeTotal(SkinCubes.GLASS),
                    counter.getCubeTotal(SkinCubes.GLASS_GLOWING));

            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinIdentifier", descriptor.getIdentifier()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinTotalCubes", totals));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinDyeCount", counter.getDyeTotal()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinMarkerCount", counter.getMarkerTotal()));

            if (skin.getPaintData() != null) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinPaintData", "64x32"));
            }

            if (ModConfig.Client.tooltipProperties && !skin.getProperties().isEmpty()) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinProperties"));
                for (String prop : skin.getProperties().getPropertiesList()) {
                    tooltip.add(Component.literal(" " + prop));
                }
            }
        }

        // Skin ID error.
//        if (identifier.hasLocalId()) {
//            if (identifier.getSkinLocalId() != data.lightHash()) {
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError1"));
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError2"));
//            }
//        }

        if (ModConfig.Client.tooltipOpenWardrobe && isItemOwner && skin.getType() instanceof ISkinEquipmentType) {
            Component keyName = ModKeyBindings.OPEN_WARDROBE_KEY.getKeyName();
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    public static void appendHoverText(ItemStack itemStack, List<Component> tooltips, TooltipFlag flags) {
        List<Component> newTooltips = createSkinTooltip(itemStack);
        if (newTooltips.isEmpty()) {
            return;
        }
        if (flags.isAdvanced()) {
            String registryName = Registries.ITEM.getKey(itemStack.getItem()).toString();
            for (int index = tooltips.size(); index > 0; --index) {
                Component text = tooltips.get(index - 1);
                // FIXME: @SAGESSE Test 1.16/1.18
                if (registryName.equals(text.getString())) {
                    tooltips.addAll(index - 1, newTooltips);
                    return;
                }
            }
        }
        tooltips.addAll(newTooltips);
    }

    public static void renderHoverText(ItemStack itemStack, CGRect frame, int mouseX, int mouseY, int screenWidth, int screenHeight, PoseStack poseStackIn) {
        if (!ModConfig.Client.skinPreEnabled) {
            return;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            return;
        }
        int tx, ty;
        int size = ModConfig.Client.skinPreSize;
        if (ModConfig.Client.skinPreLocFollowMouse) {
            tx = frame.getX() - 28 - size;
            ty = frame.getY() - 4;
            if (frame.getX() < mouseX) {
                tx = frame.getX() + frame.getWidth() + 28;
            }
            ty = MathUtils.clamp(ty, 0, screenHeight - size);
        } else {
            tx = MathUtils.ceil((screenWidth - size) * ModConfig.Client.skinPreLocHorizontal);
            ty = MathUtils.ceil((screenHeight - size) * ModConfig.Client.skinPreLocVertical);
        }

        if (ModConfig.Client.skinPreDrawBackground) {
            RenderSystem.enableDepthTest();
            RenderSystem.drawContinuousTexturedBox(poseStack, ModTextures.GUI_PREVIEW, tx, ty, 0, 0, size, size, 62, 62, 4, 400);
        }
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        ExtendedItemRenderer.renderSkin(descriptor, itemStack, tx, ty, 500, size, size, 30, 45, 0, poseStack, buffers);
        buffers.endBatch();
    }
}
