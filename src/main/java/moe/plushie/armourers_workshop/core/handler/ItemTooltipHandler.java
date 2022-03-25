package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.init.common.AWConfig;
import moe.plushie.armourers_workshop.init.common.AWItems;
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

    public static ArrayList<ITextComponent> createSkinTooltip(ItemStack itemStack) {
        boolean isItemOwner = itemStack.getItem() == AWItems.SKIN;
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
            if (AWConfig.tooltipSkinName && Strings.isNotBlank(skin.getCustomName())) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
            }
        }

        if (isItemOwner && AWConfig.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (AWConfig.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }

        if (AWConfig.tooltipSkinType) {
            TextComponent textComponent = TranslateUtils.subtitle("skinType." + skin.getType().getRegistryName());
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", textComponent));
        }

        if (!isItemOwner && AWConfig.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (AWConfig.debugTooltip && !Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        if (AWConfig.debugTooltip && Screen.hasShiftDown()) {

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

            if (AWConfig.tooltipProperties && !skin.getProperties().isEmpty()) {
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

        if (AWConfig.tooltipOpenWardrobe && isItemOwner) {
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
        if (!AWConfig.skinPreEnabled) {
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
        int size = AWConfig.skinPreSize;
        if (AWConfig.skinPreLocFollowMouse) {
            x = event.getX() - 28 - size;
            y = event.getY() - 4;
            if (event.getX() < mouseX) {
                x = event.getX() + event.getWidth() + 28;
            }
            y = MathHelper.clamp(y, 0, screenHeight - size);
        } else {
            x = MathHelper.ceil((screenWidth - size) * AWConfig.skinPreLocHorizontal);
            y = MathHelper.ceil((screenHeight - size) * AWConfig.skinPreLocVertical);
        }

        if (AWConfig.skinPreDrawBackground) {
            GuiUtils.drawContinuousTexturedBox(matrixStack, RenderUtils.TEX_GUI_PREVIEW, x, y, 0, 0, size, size, 62, 62, 4, 400);
        }
        int t = (int) System.currentTimeMillis();
        IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        matrixStack.pushPose();
        matrixStack.translate(x + size / 2f, y + size / 2f, 500f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(150));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (float) (t / 10 % 360)));
        matrixStack.scale(0.625f, 0.625f, 0.625f);
        matrixStack.scale(size, size, size);
        matrixStack.scale(-1, 1, 1);
        SkinItemRenderer.renderSkin(bakedSkin, descriptor.getColorScheme(), 0, 0xf000f0, matrixStack, buffers);
        matrixStack.popPose();
        buffers.endBatch();
    }
}
