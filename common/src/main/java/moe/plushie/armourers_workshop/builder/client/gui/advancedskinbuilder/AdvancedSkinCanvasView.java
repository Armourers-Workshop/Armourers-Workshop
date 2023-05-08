package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.guide.AdvancedChestGuideRenderer;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class AdvancedSkinCanvasView extends UIView {

//    private final GuideRendererManager rendererManager = new GuideRendererManager();
//    private final AdvancedSkinChestModel model = new AdvancedSkinChestModel();
    private final AdvancedChestGuideRenderer renderer = new AdvancedChestGuideRenderer();

    OrbitControls orbitControls = new OrbitControls();

    public AdvancedSkinCanvasView(CGRect frame) {
        super(frame);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        orbitControls.clientWidth = rect.getWidth();
        orbitControls.clientHeight = rect.getHeight();
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        CGRect rect = bounds();

        PoseStack poseStack = context.poseStack;
        poseStack.pushPose();

        poseStack.translate(rect.getWidth() / 2f, rect.getHeight() / 2f, 500f);

        ModDebugger.translate(poseStack);
        ModDebugger.scale(poseStack);
        ModDebugger.rotate(poseStack);

        poseStack.scale(16, 16, 16);

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.drawPoint(poseStack, buffers);
        RenderSystem.drawBoundingBox(context.poseStack, -1, -1, -1, 1, 1, 1, UIColor.RED, buffers);

        buffers.endBatch();


//        RenderSystem.disableCull();
//
//        renderer.render(poseStack, 0xf000f0, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, buffers);
////        for (ISkinPartType partType : SkinTypes.ARMOR_CHEST.getParts()) {
////            IGuideRenderer renderer = rendererManager.getRenderer(partType);
////            if (renderer != null) {
////                renderer.render(poseStack, () -> false, 0xf000f0, OverlayTexture.NO_OVERLAY, buffers);
////            }
////        }
//
//        RenderSystem.enableCull();
//
        poseStack.popPose();
    }


    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        orbitControls.startRotate(event.locationInWindow());
    }

    @Override
    public void mouseDragged(UIEvent event) {
        super.mouseDragged(event);
        orbitControls.updateRotate(event.locationInWindow());
    }

    @Override
    public void mouseUp(UIEvent event) {
        super.mouseUp(event);
        orbitControls.endRotate(event.locationInWindow());
    }

    //    @Override
//    public void render(PoseStack poseStack, int i, int j, float f) {
//    }
//

    //        mc.renderEngine.bindTexture(DefaultPlayerSkin.getDefaultSkinLegacy());
//        GlStateManager.pushMatrix();
//
//        // GlStateManager.scale(16F / 1F, 1F, 1F);
//        ModelPlayer modelPlayer = new ModelPlayer(1, false);
//        if (skinType == SkinTypeRegistry.skinHead) {
//            modelPlayer.bipedHead.render(1F / 16F);
//        }
//        if (skinType == SkinTypeRegistry.skinChest) {
//            modelPlayer.bipedBody.render(1F / 16F);
//            GlStateManager.translate(2F * (1F / 16F), 0, 0);
//            modelPlayer.bipedLeftArm.render(1F / 16F);
//            GlStateManager.translate(-2F * (1F / 16F), 0, 0);
//
//            GlStateManager.translate(-2F * (1F / 16F), 0, 0);
//            modelPlayer.bipedRightArm.render(1F / 16F);
//            GlStateManager.translate(2F * (1F / 16F), 0, 0);
//        }
//        if (skinType == SkinTypeRegistry.skinLegs) {
//            modelPlayer.bipedLeftLeg.render(1F / 16F);
//            modelPlayer.bipedRightLeg.render(1F / 16F);
//        }
//        if (skinType == SkinTypeRegistry.skinFeet) {
//            modelPlayer.bipedLeftLeg.render(1F / 16F);
//            modelPlayer.bipedRightLeg.render(1F / 16F);
//        }
//        // ArmourerRenderHelper.renderBuildingGrid(skinType, 1F / 16F, true, new
//        // SkinProperties(), false);
//        GlStateManager.popMatrix();

}
