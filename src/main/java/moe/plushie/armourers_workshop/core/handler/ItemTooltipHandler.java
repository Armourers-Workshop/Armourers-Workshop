package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ItemTooltipHandler {

    private int mouseX = 0;
    private int mouseY = 0;
    private int screenHeight = 0;
    private int screenWidth = 0;

    private final Vector3f scale = new Vector3f(0.625f, 0.625f, 0.625f);


    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (event.isCanceled()) {
            return;
        }
        ArrayList<ITextComponent> tooltip = SkinItem.getTooltip(event.getItemStack());
        if (tooltip.size() != 0) {
            event.getToolTip().addAll(tooltip);
        }
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
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(SkinDescriptor.of(itemStack));
        if (bakedSkin == null) {
            return;
        }

        int x, y;
        int t = (int) System.currentTimeMillis();
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
        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        matrixStack.pushPose();
        matrixStack.translate(x + size / 2f, y + size / 2f, 500f);
        matrixStack.scale(-scale.x(), scale.y(), scale.z());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(150));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (float) (t / 10 % 360)));
        SkinItemRenderer.renderSkin(bakedSkin, scale, AWConstants.ONE, size, size, size, 0, 0xf000f0, matrixStack, renderTypeBuffer);
        matrixStack.popPose();
        renderTypeBuffer.endBatch();
    }
}
