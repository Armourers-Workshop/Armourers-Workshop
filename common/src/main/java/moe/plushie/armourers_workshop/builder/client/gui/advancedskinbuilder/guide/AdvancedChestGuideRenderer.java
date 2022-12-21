package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.guide;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class AdvancedChestGuideRenderer extends AbstractAdvancedGuideRenderer {

    public ModelPart body;
    public ModelPart rightArm;
    public ModelPart leftArm;

    public AdvancedChestGuideRenderer() {
//        this.body = new ModelPart(64, 64, 16, 16);
//        this.body.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f);
//        this.body.setPos(0.0f, 0.0f, 0.0f);
//        this.rightArm = new ModelPart(64, 64, 40, 16);
//        this.rightArm.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f);
//        this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
//        this.leftArm = new ModelPart(64, 64, 32, 48);
//        this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f);
//        this.leftArm.setPos(5.0f, 2.0f, 0.0f);
//        // setup
////        this.rightArm.xRot = MathUtils.toRadians(-15);
//        this.rightArm.zRot = MathUtils.toRadians(10);
////        this.leftArm.xRot = MathUtils.toRadians(-10);
//        this.leftArm.zRot = MathUtils.toRadians(-10);
    }

    @Override
    public void render(IPoseStack poseStack, int light, int overlay, float r, float g, float b, float alpha, MultiBufferSource buffers) {
//        VertexConsumer builder = buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL);
//        body.render(poseStack, builder, light, overlay, r, g, b, alpha);
//        leftArm.render(poseStack, builder, light, overlay, r, g, b, alpha);
//        rightArm.render(poseStack, builder, light, overlay, r, g, b, alpha);
    }
}
