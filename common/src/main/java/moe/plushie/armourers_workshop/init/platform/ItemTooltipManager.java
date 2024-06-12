package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemTooltipManager {

    public static ArrayList<Component> createSkinInfo(BakedSkin bakedSkin) {
        Skin skin = bakedSkin.getSkin();
        ArrayList<Component> tooltip = new ArrayList<>();
        if (Strings.isNotBlank(skin.getCustomName().trim())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
        }
        if (Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }
        tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", TranslateUtils.Name.of(skin.getType())));
        return tooltip;
    }

    public static ArrayList<Component> createSkinTooltip(ItemStack itemStack) {
        boolean isItemOwner = itemStack.is(ModItems.SKIN.get());
        ArrayList<Component> tooltip = new ArrayList<>();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            if (isItemOwner) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinInvalidItem"));
            }
            return tooltip;
        }
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.TOOLTIP);
        if (bakedSkin == null) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skindownloading", descriptor.getIdentifier()));
            return tooltip;
        }
        Skin skin = bakedSkin.getSkin();
        SkinOptions options = descriptor.getOptions();
        SkinUsedCounter counter = bakedSkin.getUsedCounter();

        if (!isItemOwner) {
            if (options.contains(SkinOptions.TooltipFlags.HAS_SKIN)) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hasSkin"));
            }
            if (options.contains(SkinOptions.TooltipFlags.NAME) && Strings.isNotBlank(skin.getCustomName())) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
            }
        }

        if (isItemOwner && options.contains(SkinOptions.TooltipFlags.FLAVOUR) && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (options.contains(SkinOptions.TooltipFlags.AUTHOR) && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }

        if (options.contains(SkinOptions.TooltipFlags.TYPE)) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", TranslateUtils.Name.of(skin.getType())));
        }

        if (!isItemOwner && options.contains(SkinOptions.TooltipFlags.FLAVOUR) && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (ModDebugger.tooltip && !Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        if (ModDebugger.tooltip && Screen.hasShiftDown()) {

            String totals = String.format("%d/%d/%d/%d",
                    counter.getCubeTotal(SkinCubeTypes.SOLID),
                    counter.getCubeTotal(SkinCubeTypes.GLOWING),
                    counter.getCubeTotal(SkinCubeTypes.GLASS),
                    counter.getCubeTotal(SkinCubeTypes.GLASS_GLOWING));

            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinIdentifier", descriptor.getIdentifier()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinTotalCubes", totals));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinDyeCount", counter.getDyeTotal()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinMarkerCount", counter.getMarkerTotal()));

            if (skin.getPaintData() != null) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinPaintData", "64x32"));
            }

            if (ModDebugger.properties && !skin.getProperties().isEmpty()) {
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

        if (options.contains(SkinOptions.TooltipFlags.OPEN_WARDROBE) && isItemOwner && skin.getType() instanceof ISkinEquipmentType) {
            Component keyName = ModKeyBindings.OPEN_WARDROBE_KEY.getKeyName();
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    public static void gatherSkinTooltip(ItemTooltipEvent.Gather event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> newTooltips = createSkinTooltip(itemStack);
        if (newTooltips.isEmpty()) {
            return;
        }
        List<Component> tooltips = event.getTooltips();
        if (event.getContext().getFlags().isAdvanced()) {
            String registryName = TypedRegistry.findKey(itemStack.getItem()).toString();
            for (int index = tooltips.size(); index > 0; --index) {
                Component text = tooltips.get(index - 1);
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
        var itemStack = event.getItemStack();
        var descriptor = SkinDescriptor.of(itemStack);
        var options = descriptor.getOptions();
        if (!options.contains(SkinOptions.TooltipFlags.PREVIEW)) {
            return;
        }
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.TOOLTIP);
        if (bakedSkin == null) {
            return;
        }
        var frame = event.getFrame();
        var context = event.getContext();
        float screenHeight = event.getScreenHeight();
        float screenWidth = event.getScreenWidth();
        float dx, dy;
        float size = ModConfig.Client.skinPreSize;
        if (ModConfig.Client.skinPreLocFollowMouse) {
            dx = frame.getX() - 28 - size;
            dy = frame.getY() - 4;
            if (frame.getX() < context.state().mousePos().getX()) {
                dx = frame.getX() + frame.getWidth() + 28;
            }
            dy = MathUtils.clamp(dy, 0, screenHeight - size);
        } else {
            dx = MathUtils.ceil((screenWidth - size) * ModConfig.Client.skinPreLocHorizontal);
            dy = MathUtils.ceil((screenHeight - size) * ModConfig.Client.skinPreLocVertical);
        }
        if (ModConfig.Client.skinPreDrawBackground) {
            context.drawTilableImage(ModTextures.GUI_PREVIEW, dx, dy, size, size, 0, 0, 62, 62, 4, 4, 4, 4, 400);
        }
        var colorScheme = descriptor.getColorScheme();
        var buffers = AbstractBufferSource.buffer();
        ExtendedItemRenderer.renderSkinInTooltip(bakedSkin, colorScheme, itemStack, dx, dy, 500, size, size, 30, 45, 0, 0, 0xf000f0, context.state().ctm(), buffers);
        buffers.endBatch();
    }
}
