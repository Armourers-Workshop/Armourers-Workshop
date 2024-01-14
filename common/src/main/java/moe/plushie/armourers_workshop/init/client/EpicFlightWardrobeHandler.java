package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.thirdparty.EpicFlightRenderContext;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.DataStorageKey;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

import java.nio.FloatBuffer;

@Environment(EnvType.CLIENT)
public class EpicFlightWardrobeHandler {

//    private static final EpicFlightContext CONTEXT = new EpicFlightContext();

    public static void setup() {
    }

    public static void onRenderLivingPre(LivingEntity entity, EpicFlightRenderContext context) {
//        EpicFlightContext context = EpicFlightContext.of(entity);
//        if (context == null) {
//            return;
//        }
//        context.setLightmap(packedLight);
//        context.setPartialTicks(partialTicks);
//        context.setPose(poseStack);
//        context.setBuffers(buffers);

//        LivingEntityRenderer<?, ?> entityRenderer

//        if (model.transformerProvider == null) {
//            ArmatureTransformer transformer1 = SkinRendererManager2.EPICFIGHT.getTransformer(entity.getType(), model);
//            BakedArmatureTransformer transformer = BakedArmatureTransformer.create(transformer1, entityRenderer);
//            model.transformerProvider = () -> transformer;
//        }
//        BakedArmatureTransformer transformer = model.transformerProvider.get();
//        if (transformer == null) {
//            return;
//        }
//        EpicFlightContext context = CONTEXT;
//        Collection<ISkinPartType> overrideParts = null;
//        if (isFirstPersonRenderer) {
//            overrideParts = Collections.singleton(SkinPartTypes.BIPPED_HEAD);
//        }
////        model.setAssociatedObject(transformProvider, EpicFlightTransformProvider.KEY);
//
//        context.overrideParts = overrideParts;
//        context.overridePostStack = poseStack.copy();
//        context.overrideTransformer = transformer;
//        context.isLimitLimbs = false;
//        context.overrideModel = model;
//        context.renderContext = SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers);
//
//        renderData.epicFlightContext = context;
    }

    public static void onRenderLivingEntity(LivingEntity entity, EpicFlightRenderContext context) {
        context.activate(entity);
    }

    public static void onRenderLivingPost(LivingEntity entity, EpicFlightRenderContext context) {
        context.deactivate(entity);

//        SkinRenderData renderData = SkinRenderData.of(entity);
//        if (renderData == null || renderData.epicFlightContext == null) {
//            return;
//        }
//        EpicFlightContext context = renderData.epicFlightContext;
//        IModel model = context.overrideModel;
//
//        context.deactivate(entity);
//        context.renderContext.release();
//        context.reset();
//
//        renderData.epicFlightContext = null;
    }

//    public static void onPrepareModel(LivingEntity entity, Object animatedModel, int packedLightIn, float partialTicks, PoseStack poseStack, MultiBufferSource buffers) {
//        SkinRenderData renderData = SkinRenderData.of(entity);
//        if (renderData == null || renderData.epicFlightContext == null) {
//            return;
//        }
//        EpicFlightContext context = renderData.epicFlightContext;
//        IModel model = context.overrideModel;
//        if (model instanceof CustomModel) {
//            ((CustomModel) model).linkTo(animatedModel);
//        }
//        context.activate(entity);
//    }
}
