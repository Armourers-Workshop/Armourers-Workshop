package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Environment(EnvType.CLIENT)
public class ItemTooltipManager {

    public static List<Component> createSkinInfo(BakedSkin bakedSkin) {
        var skin = bakedSkin.skin();
        var tooltip = new ArrayList<Component>();
        if (Strings.isNotBlank(skin.customName().trim())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.customName().trim()));
        }
        if (Strings.isNotBlank(skin.authorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.authorName().trim()));
        }
        tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", TranslateUtils.Name.of(skin.type())));
        if (ModDebugger.tooltip) {
            appendSettingInfo(tooltip, bakedSkin);
        }
        return tooltip;
    }

    public static List<Component> createSkinTooltip(ItemStack itemStack) {
        var isItemOwner = itemStack.is(ModItems.SKIN.get());
        var tooltip = new ArrayList<Component>();
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            if (isItemOwner) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinInvalidItem"));
            }
            return tooltip;
        }
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.TOOLTIP);
        if (bakedSkin == null) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skindownloading", descriptor.identifier()));
            return tooltip;
        }
        var skin = bakedSkin.skin();
        var options = descriptor.options();
        var counter = bakedSkin.usedCounter();

        if (!isItemOwner) {
            if (options.contains(SkinDescriptor.TooltipFlags.HAS_SKIN)) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hasSkin"));
            }
            if (options.contains(SkinDescriptor.TooltipFlags.NAME) && Strings.isNotBlank(skin.customName())) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.customName().trim()));
            }
        }

        if (isItemOwner && options.contains(SkinDescriptor.TooltipFlags.FLAVOUR) && Strings.isNotBlank(skin.flavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.flavourText().trim()));
        }

        if (options.contains(SkinDescriptor.TooltipFlags.AUTHOR) && Strings.isNotBlank(skin.authorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.authorName().trim()));
        }

        if (options.contains(SkinDescriptor.TooltipFlags.TYPE)) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", TranslateUtils.Name.of(skin.type())));
        }

        if (!isItemOwner && options.contains(SkinDescriptor.TooltipFlags.FLAVOUR) && Strings.isNotBlank(skin.flavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.flavourText().trim()));
        }

        if (ModDebugger.tooltip && !Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        if (ModDebugger.tooltip && Screen.hasShiftDown()) {
            var totals = String.format("%d/%d/%d/%d",
                    counter.getGeometryTotal(SkinGeometryTypes.BLOCK_SOLID),
                    counter.getGeometryTotal(SkinGeometryTypes.BLOCK_GLOWING),
                    counter.getGeometryTotal(SkinGeometryTypes.BLOCK_GLASS),
                    counter.getGeometryTotal(SkinGeometryTypes.BLOCK_GLASS_GLOWING));

            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinIdentifier", descriptor.identifier()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinTotalCubes", totals));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinDyeCount", counter.dyeTotal()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinMarkerCount", counter.markerTotal()));

            if (skin.paintData() != null) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinPaintData", "64x32"));
            }

            if (ModDebugger.properties && !skin.properties().isEmpty()) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinProperties"));
                for (var prop : skin.properties().getPropertiesList()) {
                    tooltip.add(Component.literal(" " + prop));
                }
            }
            appendSettingInfo(tooltip, bakedSkin);
        }

        // Skin ID error.
//        if (identifier.hasLocalId()) {
//            if (identifier.getSkinLocalId() != data.lightHash()) {
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError1"));
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError2"));
//            }
//        }

        if (options.contains(SkinDescriptor.TooltipFlags.OPEN_WARDROBE) && isItemOwner && skin.type().isEquipment()) {
            var keyName = ModKeyBindings.OPEN_WARDROBE_KEY.keyName();
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    private static void appendSettingInfo(List<Component> tooltip, BakedSkin bakedSkin) {
        var flags = new StringJoiner(",", "[", "]");
        var settings = bakedSkin.skin().settings();
        if (!settings.isEditable()) {
            flags.add("NE");
        }
        if (!settings.isSavable()) {
            flags.add("NS");
        }
        if (!settings.isExportable()) {
            flags.add("NP");
        }
        if (settings.isEncrypted()) {
            flags.add("S");
        }
        if (settings.isCompressed()) {
            flags.add("C");
        }
        tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinSettings", flags.toString()));
    }

    public static void gatherSkinTooltip(ItemTooltipEvent.Gather event) {
        var itemStack = event.itemStack();
        var newTooltips = createSkinTooltip(itemStack);
        if (newTooltips.isEmpty()) {
            return;
        }
        var tooltips = event.tooltips();
        if (event.context().flags().isAdvanced()) {
            var registryName = TypedRegistry.findKey(itemStack.getItem()).toString();
            for (int index = tooltips.size(); index > 0; --index) {
                var text = tooltips.get(index - 1);
                if (registryName.equals(text.getString())) {
                    tooltips.addAll(index - 1, newTooltips);
                    return;
                }
            }
        }
        tooltips.addAll(newTooltips);
    }

    public static void renderSkinTooltip(ItemTooltipEvent.Render event) {
        if (!ModConfig.Client.skinPreEnabled) {
            return;
        }
        var itemStack = event.itemStack();
        var descriptor = SkinDescriptor.of(itemStack);
        var options = descriptor.options();
        if (!options.contains(SkinDescriptor.TooltipFlags.PREVIEW)) {
            return;
        }
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.TOOLTIP);
        if (bakedSkin == null) {
            return;
        }
        var frame = event.frame();
        var context = event.context();
        float screenHeight = event.screenHeight();
        float screenWidth = event.screenWidth();
        float dx, dy;
        float size = ModConfig.Client.skinPreSize;
        if (ModConfig.Client.skinPreLocFollowMouse) {
            dx = frame.x - 28 - size;
            dy = frame.y - 4;
            if (frame.x < context.state().mousePos().x()) {
                dx = frame.x + frame.width + 28;
            }
            dy = OpenMath.clamp(dy, 0, screenHeight - size);
        } else {
            dx = OpenMath.ceili((screenWidth - size) * ModConfig.Client.skinPreLocHorizontal);
            dy = OpenMath.ceili((screenHeight - size) * ModConfig.Client.skinPreLocVertical);
        }
        if (ModConfig.Client.skinPreDrawBackground) {
            context.drawTilableImage(ModTextures.GUI_PREVIEW, dx, dy, size, size, 0, 0, 62, 62, 4, 4, 4, 4, 400);
        }
        var colorScheme = descriptor.paintScheme();
        var buffers = AbstractBufferSource.buffer();
        ExtendedItemRenderer.renderSkinInTooltip(bakedSkin, colorScheme, itemStack, dx, dy, 500, size, size, 30, 45, 0, 0, 0xf000f0, context.state().ctm(), buffers);
        buffers.endBatch();
    }
}
