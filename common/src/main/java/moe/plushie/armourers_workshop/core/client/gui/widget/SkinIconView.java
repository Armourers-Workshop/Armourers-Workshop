package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.mojang.blaze3d.platform.Lighting;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinIconView extends UIControl {

    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private final Ticket loadTicket = Ticket.list();

    public SkinIconView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        auto bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
        if (bakedSkin == null) {
            return;
        }
        CGRect rect = bounds();
        float tx = rect.x;
        float ty = rect.y;
        float tw = rect.width;
        float th = rect.height;
        float si = Math.min(tw, th);
        auto poseStack = context.state().ctm();
        auto colorScheme = descriptor.getColorScheme();
        auto itemSource = SkinItemSource.EMPTY;
        auto buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(tx + tw / 2f, ty + th / 2f, 200);
        // we need do a vertical mirror, but normal matrix no needs.
        poseStack.mulPoseMatrix(OpenMatrix4f.createScaleMatrix(1, -1, 1));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(30));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(135));
        poseStack.scale(0.625f, 0.625f, 0.625f);
        poseStack.scale(si, si, si);
        ExtendedItemRenderer.renderSkinInBox(bakedSkin, colorScheme, Vector3f.ONE, 1, 1, 1, 0, 0xf000f0, itemSource, poseStack, buffers);
        poseStack.popPose();
        buffers.endBatch();
    }

    public SkinDescriptor skin() {
        return descriptor;
    }

    public void setSkin(SkinDescriptor descriptor) {
        this.loadTicket.invalidate();
        this.descriptor = descriptor;
    }
}
