package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFightModelHolder;
import moe.plushie.armourers_workshop.core.client.model.CachedModel;
import moe.plushie.armourers_workshop.init.client.EpicFlightWardrobeHandler;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.FirstPersonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.nio.FloatBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Available("[1.18, )")
@Pseudo
@Mixin(PatchedLivingEntityRenderer.class)
public abstract class ForgeEpicFightRendererMixin {

    private static final FloatBuffer AW_MAT_BUFFER3 = ObjectUtils.createFloatBuffer(16);
    private static final FloatBuffer AW_MAT_BUFFER4 = ObjectUtils.createFloatBuffer(16);

    private static final Function<ModelPart<?>, IModelPart> PART_TRANSFORMER = modelPart -> new IModelPart() {
        @Override
        public boolean isVisible() {
            return !modelPart.hidden;
        }

        @Override
        public void setVisible(boolean visible) {
            modelPart.hidden = !visible;
        }

        @Override
        public IModelPartPose pose() {
            return null;
        }
    };

    private static final BiConsumer<OpenMatrix4f, FloatBuffer> MATRIX_STORE_FUNC = (mat, buf) -> {
        buf.put(mat.m00);
        buf.put(mat.m01);
        buf.put(mat.m02);
        buf.put(mat.m03);
        buf.put(mat.m10);
        buf.put(mat.m11);
        buf.put(mat.m12);
        buf.put(mat.m13);
        buf.put(mat.m20);
        buf.put(mat.m21);
        buf.put(mat.m22);
        buf.put(mat.m23);
        buf.put(mat.m30);
        buf.put(mat.m31);
        buf.put(mat.m32);
        buf.put(mat.m33);
    };

    public void aw$prepareModel(PrepareModelEvent event) {
        EpicFlightWardrobeHandler.onPrepareModel(event.getEntityPatch().getOriginal(), event.getMesh());
    }

    @Inject(method = "renderLayer", at = @At("HEAD"), remap = false)
    public void aw$renderLayerPre(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStack, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        Armature armature = entityPatch.getArmature();
        boolean isFirstPersonRenderer = ObjectUtils.safeCast(this, FirstPersonRenderer.class) != null;
        EpicFlightWardrobeHandler.onRenderLivingPre(entityIn, partialTicks, packedLightIn, poseStack, buffers, renderer, isFirstPersonRenderer, name -> {
            Joint joint = armature.searchJointByName(name);
            if (joint == null) {
                return IJointTransform.NONE;
            }
            // Referenced: yesman.epicfight.api.client.model.AnimatedMesh.drawModelWithPose
            OpenMatrix4f jointPose = OpenMatrix4f.mul(poses[joint.getId()], joint.getToOrigin(), null);
            OpenMatrix4f jointNormal = jointPose.removeTranslation();
            return poseStack1 -> {
                MATRIX_STORE_FUNC.accept(jointPose, AW_MAT_BUFFER4);
                MATRIX_STORE_FUNC.accept(jointNormal, AW_MAT_BUFFER3);
                AW_MAT_BUFFER3.rewind();
                AW_MAT_BUFFER4.rewind();
                poseStack1.multiply(EpicFlightWardrobeHandler.convertPoseMatrix(AW_MAT_BUFFER4));
                poseStack1.multiply(EpicFlightWardrobeHandler.convertNormalMatrix(AW_MAT_BUFFER3));
            };
        });
    }

    @Inject(method = "renderLayer", at = @At("RETURN"), remap = false)
    public void aw$renderLayerPost(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStack, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        EpicFlightWardrobeHandler.onRenderLivingPost(entityIn, partialTicks, packedLightIn, poseStack, buffers, renderer);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void aw$init(CallbackInfo callbackInfo) {
        EpicFlightWardrobeHandler.onSetup();
        // register prepare model event and the forward it.
        NotificationCenterImpl.observer(PrepareModelEvent.class, event -> {
            ((ForgeEpicFightRendererMixin) (Object) event.getRenderer()).aw$prepareModel(event);
        });
        // register epic fight model accessor.
        EpicFightModelHolder.register(AnimatedMesh.class, CachedModel::new, PART_TRANSFORMER, (model, it) -> {
            // nothing
        });
        EpicFightModelHolder.register(HumanoidMesh.class, CachedModel.Player::new, PART_TRANSFORMER, (model, it) -> {
            it.put("hat", model.hat);
            it.put("head", model.head);
            it.put("body", model.torso);
            it.put("left_arm", model.lefrArm);
            it.put("right_arm", model.rightArm);
            it.put("left_leg", model.leftLeg);
            it.put("right_leg", model.rightLeg);
            it.put("left_sleeve", model.leftSleeve);
            it.put("right_sleeve", model.rightSleeve);
            it.put("left_pants", model.leftPants);
            it.put("right_pants", model.rightPants);
            it.put("jacket", model.jacket);
        });
    }
}
