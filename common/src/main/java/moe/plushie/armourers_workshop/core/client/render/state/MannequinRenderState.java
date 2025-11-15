package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.compat.client.renderer.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.LazyValue;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Rotations;
import net.minecraft.world.phys.AABB;

public class MannequinRenderState extends LivingEntityRenderState {

    protected boolean isSlimModel = false;
    protected boolean isModelVisible = false;

    protected AABB boundingBoxForCulling;

    protected Rotations headPose;
    protected Rotations bodyPose;
    protected Rotations leftArmPose;
    protected Rotations rightArmPose;
    protected Rotations leftLegPose;
    protected Rotations rightLegPose;

    public static MannequinRenderState getPlaceholder() {
        return Placeholder.PLACEHOLDER.get();
    }

    public boolean isSlimModel() {
        return isSlimModel;
    }

    public boolean isModelVisible() {
        return isModelVisible;
    }

    public AABB boundingBoxForCulling() {
        return boundingBoxForCulling;
    }

    public Rotations headPose() {
        return headPose;
    }

    public Rotations bodyPose() {
        return bodyPose;
    }

    public Rotations leftArmPose() {
        return leftArmPose;
    }

    public Rotations rightArmPose() {
        return rightArmPose;
    }

    public Rotations leftLegPose() {
        return leftLegPose;
    }

    public Rotations rightLegPose() {
        return rightLegPose;
    }


    public static void extract(MannequinEntity entity, MannequinRenderState renderState) {
        // copy base info
        renderState.isSlimModel = entity.getTextureModel() == EntityTextureDescriptor.Model.SLIM;
        renderState.isModelVisible = entity.isModelVisible();
        renderState.boundingBoxForCulling = entity.getBoundingBoxForCulling();
        // copy the mannequin pose.
        renderState.headPose = entity.getHeadPose();
        renderState.bodyPose = entity.getBodyPose();
        renderState.leftArmPose = entity.getLeftArmPose();
        renderState.rightArmPose = entity.getRightArmPose();
        renderState.leftLegPose = entity.getLeftLegPose();
        renderState.rightLegPose = entity.getRightLegPose();
    }

    public static class Placeholder {

        private static final int ID = -1021;

        private static final LazyValue<MannequinEntity> ENTITY = LazyValue.of(() -> {
            var level = Minecraft.getInstance().level;
            var entity = new MannequinEntity(ModEntityTypes.MANNEQUIN.get().get(), level);
            entity.setId(ID);
            entity.setExtraRenderer(false); // never magic cir
            return entity;
        });

        private static final LazyValue<MannequinRenderState> PLACEHOLDER = LazyValue.of(() -> {
            var entity = ENTITY.get();
            return AbstractRenderState.create(entity, 0, 0);
        });

        public static int getId() {
            return ID;
        }

        public static MannequinEntity getEntity() {
            return ENTITY.get();
        }
    }
}
