package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModItems;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.utils.KeyBindings;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ItemTooltipHandler {

    private int mouseX = 0;
    private int mouseY = 0;
    private int screenHeight = 0;
    private int screenWidth = 0;

    public static ArrayList<ITextComponent> createSkinInfo(BakedSkin bakedSkin) {
        Skin skin = bakedSkin.getSkin();
        ArrayList<ITextComponent> tooltip = new ArrayList<>();
        if (Strings.isNotBlank(skin.getCustomName().trim())){
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
        }
        if (ModConfig.Client.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }
        TextComponent textComponent = TranslateUtils.subtitle("skinType." + skin.getType().getRegistryName());
        tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", textComponent));
        return tooltip;
    }

    public static ArrayList<ITextComponent> createSkinTooltip(ItemStack itemStack) {
        boolean isItemOwner = itemStack.getItem() == ModItems.SKIN;
        ArrayList<ITextComponent> tooltip = new ArrayList<>();
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
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hasSkin"));
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
            TextComponent textComponent = TranslateUtils.subtitle("skinType." + skin.getType().getRegistryName());
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", textComponent));
        }

        if (!isItemOwner && ModConfig.Client.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (ModConfig.Client.debugTooltip && !Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        if (ModConfig.Client.debugTooltip && Screen.hasShiftDown()) {

            String totals = String.format("%d/%d/%d/%d",
                    counter.getCubeTotal(SkinCubes.SOLID),
                    counter.getCubeTotal(SkinCubes.GLOWING),
                    counter.getCubeTotal(SkinCubes.GLASS),
                    counter.getCubeTotal(SkinCubes.GLASS_GLOWING));

            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinIdentifier", descriptor.getIdentifier()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinTotalCubes", totals));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinDyeCount", counter.getDyeTotal()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinMarkerCount", counter.getMarkerTotal()));

            if (skin.hasPaintData()) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinPaintData", "64x32"));
            }

            if (ModConfig.Client.tooltipProperties && !skin.getProperties().isEmpty()) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinProperties"));
                for (String prop : skin.getProperties().getPropertiesList()) {
                    tooltip.add(TranslateUtils.literal(" " + prop));
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

        if (ModConfig.Client.tooltipOpenWardrobe && isItemOwner) {
            ITextComponent keyName = KeyBindings.OPEN_WARDROBE_KEY.getTranslatedKeyMessage();
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (event.isCanceled()) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        ArrayList<ITextComponent> newTooltips = createSkinTooltip(itemStack);
        if (newTooltips.isEmpty()) {
            return;
        }
        List<ITextComponent> tooltips = event.getToolTip();
        if (event.getFlags().isAdvanced() && itemStack.getItem().getRegistryName() != null) {
            String registryName = itemStack.getItem().getRegistryName().toString();
            for (int index = tooltips.size(); index > 0; --index) {
                ITextComponent text = tooltips.get(index - 1);
                if (text instanceof StringTextComponent && registryName.equals(text.getContents())) {
                    tooltips.addAll(index - 1, newTooltips);
                    return;
                }
            }
        }
        tooltips.addAll(newTooltips);
    }

    @SubscribeEvent
    public void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        mouseX = event.getX();
        mouseY = event.getY();
        screenWidth = event.getScreenWidth();
        screenHeight = event.getScreenHeight();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderTooltip(RenderTooltipEvent.PostText event) {
        if (event.isCanceled()) {
            return;
        }
        if (!ModConfig.Client.skinPreEnabled) {
            return;
        }
        ItemStack itemStack = event.getStack();
        MatrixStack matrixStack = event.getMatrixStack();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            return;
        }
        int x, y;
        int size = ModConfig.Client.skinPreSize;
        if (ModConfig.Client.skinPreLocFollowMouse) {
            x = event.getX() - 28 - size;
            y = event.getY() - 4;
            if (event.getX() < mouseX) {
                x = event.getX() + event.getWidth() + 28;
            }
            y = MathHelper.clamp(y, 0, screenHeight - size);
        } else {
            x = MathHelper.ceil((screenWidth - size) * ModConfig.Client.skinPreLocHorizontal);
            y = MathHelper.ceil((screenHeight - size) * ModConfig.Client.skinPreLocVertical);
        }

        if (ModConfig.Client.skinPreDrawBackground) {
            RenderSystem.enableDepthTest();
            GuiUtils.drawContinuousTexturedBox(matrixStack, RenderUtils.TEX_GUI_PREVIEW, x, y, 0, 0, size, size, 62, 62, 4, 400);
            RenderSystem.disableDepthTest();
        }
        IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        SkinItemRenderer.renderSkin(descriptor, x, y, 500, size, size, 150, 45, 0, matrixStack, buffers);
        buffers.endBatch();
    }

}
