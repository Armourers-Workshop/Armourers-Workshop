package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IEntityModelProvider;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.model.LinkedModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.auto;

public class BoatModelArmaturePlugin extends ArmaturePlugin {

    private IEntityModelProvider<Entity, EntityModel<Entity>> modelProvider;
    private final LinkedModel placeholderModel = new LinkedModel(null);

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        float g = context.getPartialTicks();
        float f = MathUtils.lerp(g, entity.yRotO, entity.getYRot());
        auto poseStack = context.pose();
        auto model = modelProvider.getModel(entity);
        model.setupAnim(entity, g, 0.0f, -0.1f, 0.0f, 0.0f);
        placeholderModel.linkTo(ModelHolder.of(model));

//                poseStack.pushPose();
        poseStack.translate(0.0f, 0.375f, 0.0f);
        poseStack.rotate(Vector3f.ZP.rotationDegrees(180.0f - f));
//        float h = (float)boat.getHurtTime() - g;
//        float j = boat.getDamage() - g;
//        if (j < 0.0f) {
//            j = 0.0f;
//        }
//        if (h > 0.0f) {
//            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(h) * h * j / 10.0f * (float)boat.getHurtDir()));
//        }
//        if (!Mth.equal(k = boat.getBubbleAngle(g), 0.0f)) {
//            poseStack.mulPose(new Quaternionf().setAngleAxis(boat.getBubbleAngle(g) * ((float)Math.PI / 180), 1.0f, 0.0f, 1.0f));
//        }
//        Pair<ResourceLocation, ListModel<Boat>> pair = this.boatResources.get(boat.getVariant());
//        ResourceLocation resourceLocation = pair.getFirst();
//        ListModel<Boat> listModel = pair.getSecond();
//        poseStack.scale(-1.0f, -1.0f, 1.0f);
//        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
//        listModel.setupAnim(boat, g, 0.0f, -0.1f, 0.0f, 0.0f);
//        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(listModel.renderType(resourceLocation));
//        listModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
//        if (!boat.isUnderWater()) {
//            VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(RenderType.waterMask());
//            if (listModel instanceof WaterPatchModel) {
//                WaterPatchModel waterPatchModel = (WaterPatchModel)((Object)listModel);
//                waterPatchModel.waterPatch().render(poseStack, vertexConsumer2, i, OverlayTexture.NO_OVERLAY);
//            }
//        }
//        poseStack.popPose();
    }

    @Override
    public IModel apply(IModel model) {
        return placeholderModel;
    }

    @Override
    public EntityRenderer<?> apply(EntityRenderer<?> entityRenderer) {
        modelProvider = ObjectUtils.unsafeCast(entityRenderer);
        return entityRenderer;
    }
}
