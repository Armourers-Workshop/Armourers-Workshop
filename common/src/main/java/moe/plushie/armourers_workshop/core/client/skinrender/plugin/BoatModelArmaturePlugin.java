package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IEntityModelProvider;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.model.LinkedModel;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

import manifold.ext.rt.api.auto;

public class BoatModelArmaturePlugin extends ArmaturePlugin {

    private IEntityModelProvider<Entity, EntityModel<Entity>> modelProvider;
    private final LinkedModel placeholderModel = new LinkedModel(null);

    public BoatModelArmaturePlugin(ArmatureTransformerContext context) {
        context.setEntityModel(placeholderModel);
        context.addEntityRendererListener(entityRenderer -> {
            // force cast to model provider.
            modelProvider = ObjectUtils.unsafeCast(entityRenderer);
        });
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        auto boat = (Boat) entity;

        float h = context.getPartialTicks();
        float g = MathUtils.lerp(h, boat.yRotO, boat.getYRot());

        auto boatModel = modelProvider.getModel(boat);
        boatModel.setupAnim(boat, h, 0.0F, -0.1F, 0.0F, 0.0F);
        placeholderModel.linkTo(ModelHolder.of(boatModel));

        SkinRenderData renderData = context.getRenderData();
        SkinOverriddenManager overriddenManager = renderData.getOverriddenManager();
        overriddenManager.addProperty(SkinProperty.OVERRIDE_MODEL_CHEST);

        apply(boat, g, h, context.pose());
    }

    private void apply(Boat boat, float g, float h, IPoseStack poseStack) {
        // ref: net.minecraft.client.renderer.entity.BoatRenderer
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.rotate(Vector3f.YP.rotationDegrees(180.0F - g));
        float f = boat.getHurtTime() - h;
        float f1 = boat.getDamage() - h;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.rotate(Vector3f.XP.rotationDegrees(MathUtils.sin(f) * f * f1 / 10.0F * (float) boat.getHurtDir()));
        }

        float f2 = boat.getBubbleAngle(h);
        if (!MathUtils.equal(f2, 0.0F)) {
            Vector3f axis = new Vector3f(1.0f, 0.0f, 1.0f);
            poseStack.rotate((new OpenQuaternionf(axis, f2 * 0.017453292F, false)));
        }

        poseStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
    }
}
