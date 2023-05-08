package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

@Environment(value= EnvType.CLIENT)
public class SkinPreviewView extends UIControl {

    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private final Ticket loadTicket = Ticket.wardrobe();

    public SkinPreviewView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
        if (bakedSkin == null) {
            return;
        }
        CGRect rect = bounds();
        int x = rect.x;
        int y = rect.y;
        int z = 200;
        int width = rect.width;
        int height = rect.height;
        PoseStack poseStack = context.poseStack;
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        ExtendedItemRenderer.renderSkinInBox(bakedSkin, descriptor.getColorScheme(), ItemStack.EMPTY, x, y, z, width, height, 20, 45, 0, poseStack, buffers);
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
