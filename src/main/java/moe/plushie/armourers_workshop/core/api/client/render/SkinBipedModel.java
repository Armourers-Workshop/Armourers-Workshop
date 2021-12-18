package moe.plushie.armourers_workshop.core.api.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.render.other.SkinPartRenderData;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.UtilColour;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class SkinBipedModel<T extends LivingEntity> {

    public final ModelRenderer head;
    public final ModelRenderer hat;
    public final ModelRenderer body;
    public final ModelRenderer skirt;
    public final ModelRenderer rightArm;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightFoot;
    public final ModelRenderer leftFoot;
    public final ModelRenderer rightLeg;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightWing;
    public final ModelRenderer leftWing;
    //    private final BipedModel<DumpLivingEntity> innerBipedModel = new BipedModel<>(0);
//    private final DumpLivingEntity innerBipedEntity = new DumpLivingEntity();
//
//    private static class DumpLivingEntity extends LivingEntity {
//        DumpLivingEntity() {
//            super(EntityType.ARMOR_STAND, null);
//        }
//
//        @Override
//        public Iterable<ItemStack> getArmorSlots() {
//            return null;
//        }
//
//        @Override
//        public ItemStack getItemBySlot(EquipmentSlotType slotType) {
//            return null;
//        }
//
//        @Override
//        public void setItemSlot(EquipmentSlotType slotType, ItemStack itemStack) {
//
//        }
//
//        @Override
//        public HandSide getMainArm() {
//            return null;
//        }
//    }
    private final HashMap<ISkinPartType, ModelRenderer> registeredParts = new HashMap<>();

    public boolean young = false;
    public boolean crouching = false;
    public boolean riding = false;
    public boolean flying = false;

    public SkinBipedModel() {
        super();
        this.head = register(SkinPartTypes.BIPED_HEAD, 0, 0, renderer -> {
            renderer.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F);
            renderer.setPos(0.0F, 0.0F, 0.0F);
        });
        this.hat = register(SkinPartTypes.BIPED_HAT, 32, 0, renderer -> {
            renderer.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F);
            renderer.setPos(0.0F, 0.0F, 0.0F);
        });
        this.body = register(SkinPartTypes.BIPED_CHEST, 16, 16, renderer -> {
            renderer.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F);
            renderer.setPos(0.0F, 0.0F, 0.0F);
        });
        this.leftArm = register(SkinPartTypes.BIPED_LEFT_ARM, 40, 16, renderer -> {
            renderer.mirror = true;
            renderer.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F);
            renderer.setPos(5.0F, 2.0F, 0.0F);
        });
        this.rightArm = register(SkinPartTypes.BIPED_RIGHT_ARM, 40, 16, renderer -> {
            renderer.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F);
            renderer.setPos(-5.0F, 2.0F, 0.0F);
        });
        this.leftLeg = register(SkinPartTypes.BIPED_LEFT_LEG, 0, 16, renderer -> {
            renderer.mirror = true;
            renderer.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, 0.0F);
            renderer.setPos(1.9F, 12.0F, 0.0F);
        });
        this.rightLeg = register(SkinPartTypes.BIPED_RIGHT_LEG, 0, 16, renderer -> {
            renderer.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, 0.0F);
            renderer.setPos(-1.9F, 12.0F, 0.0F);
        });
        // custom part
        this.skirt = register(SkinPartTypes.BIPED_SKIRT, 0, 0, renderer -> {
            renderer.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F);
            renderer.setPos(0.0F, 12.0F, 0.0F);
        });
        this.leftFoot = register(SkinPartTypes.BIPED_LEFT_FOOT, 0, 16, renderer -> {
            renderer.mirror = true;
            renderer.addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F);
            renderer.setPos(1.9F, 12.0F, 0.0F);
        });
        this.rightFoot = register(SkinPartTypes.BIPED_RIGHT_FOOT, 0, 16, renderer -> {
            renderer.addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F);
            renderer.setPos(-1.9F, 12.0F, 0.0F);
        });
        this.rightWing = register(SkinPartTypes.BIPED_LEFT_WING, 0, 0, renderer -> {
            renderer.addBox(-4.0F, 0.0F, -4.0F, 4.0F, 12.0F, 4.0F, 0.0F);
            renderer.setPos(0.0F, 0.0F, 0.0F);
        });
        this.leftWing = register(SkinPartTypes.BIPED_RIGHT_WING, 0, 0, renderer -> {
            renderer.mirror = true;
            renderer.addBox(-4.0F, 0.0F, -4.0F, 4.0F, 12.0F, 4.0F, 0.0F);
            renderer.setPos(0.0F, 0.0F, 0.0F);
        });
    }


//    public void prepareModel(BakedSkin bakedSkin) {
//        innerBipedModel.setupAnim(innerBipedEntity, 0, 0, 0, 0, 0);
//        copyFromBipedModel(innerBipedModel);
//    }

    public void prepareModel(BakedSkin bakedSkin, Model model) {
        applyModelTransform(model);
    }

    public void renderToBuffer(BakedSkin bakedSkin, SkinRenderData renderData, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        matrixStack.pushPose();

        if (young) {
            float bodyScale = 1.5f / 2.0f; // from BipedModel
            matrixStack.scale(bodyScale, bodyScale, bodyScale);
        }

        int i = 0;
        Skin skin = bakedSkin.getSkin();

        for (SkinPart skinPart : skin.getParts()) {
            //
            ModelRenderer modelRenderer = getModelRenderer(skinPart.getType());
            if (modelRenderer == null || !modelRenderer.visible) {
                continue;
            }
            matrixStack.pushPose();

            // apply custom/vanilla offset and rotation.
            applySkinTransform(matrixStack, skinPart, modelRenderer);

            // render the contents.
            SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skinPart, renderData), matrixStack, renderer);

            // render the debug skin part bounding box.
            if (SkinConfig.showDebugPartBounds) {
                RenderUtils.drawBoundingBox(matrixStack, skinPart.getRenderShape(), UtilColour.getPaletteColor(i++));
            }

            matrixStack.popPose();
        }

        // render the debug skin bounding box.
        if (SkinConfig.showDebugFullBounds) {
            RenderUtils.drawBoundingBox(matrixStack, skin.getRenderShape(this), Color.YELLOW);
        }

        matrixStack.popPose();
    }

    public void setAllVisible(boolean visible) {
        registeredParts.values().forEach(modelRenderer -> {
            modelRenderer.visible = visible;
        });
    }

    public ModelRenderer getModelRenderer(ISkinPartType partType) {
        return registeredParts.get(partType);
    }

    @SuppressWarnings("unchecked")
    public void applyModelTransform(Model model) {
        if (model instanceof BipedModel) {
            applyModelTransform((BipedModel<T>) model);
            return;
        }
        if (model instanceof VillagerModel) {
            applyModelTransform((VillagerModel<T>) model);
            return;
        }
    }

    public void translateToHand(HandSide handSide, MatrixStack matrixStack) {
//        this.getArm(p_225599_1_).translateAndRotate(p_225599_2_);
    }


    protected void applyModelTransform(VillagerModel<T> model) {
//        this.hat.copyFrom(model.hat);
//        this.head.copyFrom(model.head);
//        this.body.copyFrom(model.body);
    }

    protected void applyModelTransform(BipedModel<?> model) {
        this.young = model.young;
        this.crouching = model.crouching;
        this.riding = model.riding;
        // copy model properties.
        this.hat.copyFrom(model.hat);
        this.head.copyFrom(model.head);
        this.body.copyFrom(model.body);
        this.leftArm.copyFrom(model.leftArm);
        this.rightArm.copyFrom(model.rightArm);
        this.leftFoot.copyFrom(model.leftLeg);
        this.rightFoot.copyFrom(model.rightLeg);
        this.leftLeg.copyFrom(model.leftLeg);
        this.rightLeg.copyFrom(model.rightLeg);
        this.skirt.copyFrom(model.rightLeg);
        this.skirt.x = model.body.x;
        this.skirt.yRot = model.body.yRot;
        // skirt does not wobble during normal walking.
        if (!this.riding) {
            this.skirt.xRot = 0;
        }
        this.leftWing.copyFrom(model.body);
        this.leftWing.z += MathHelper.cos(model.body.xRot) * 2;
        this.leftWing.y -= MathHelper.sin(model.body.xRot) * 2;
        this.rightWing.copyFrom(this.leftWing);
    }

    protected void applySkinTransform(MatrixStack matrixStack, SkinPart skinPart, ModelRenderer modelRenderer) {
        applySkinTransform(matrixStack, modelRenderer);
        // some parts can rotate automatically.
        if (!(skinPart.getType() instanceof IHasRotation)) {
            return;
        }
        List<SkinMarker> markers = skinPart.getMarkers();
        if (markers == null || markers.size() == 0) {
            return;
        }
        SkinMarker marker = markers.get(0);
        Point3D point = marker.getPosition();

        float angle = (float) getRotationDegrees(skinPart);
        Vector3f offset = new Vector3f(point.getX() + 0.5f, point.getY() + 0.5f, point.getZ() + 0.5f);
        if (modelRenderer.mirror) {
            angle = -angle;
        }

        matrixStack.translate(offset.x(), offset.y(), offset.z());
        matrixStack.mulPose(getRotationMatrix(marker).rotationDegrees(angle));
        matrixStack.translate(-offset.x(), -offset.y(), -offset.z());
    }

    protected void applySkinTransform(MatrixStack matrixStack, ModelRenderer modelRenderer) {
        matrixStack.translate(modelRenderer.x, modelRenderer.y, modelRenderer.z);
        if (modelRenderer.zRot != 0) {
            matrixStack.mulPose(Vector3f.ZP.rotation(modelRenderer.zRot));
        }
        if (modelRenderer.yRot != 0) {
            matrixStack.mulPose(Vector3f.YP.rotation(modelRenderer.yRot));
        }
        if (modelRenderer.xRot != 0) {
            matrixStack.mulPose(Vector3f.XP.rotation(modelRenderer.xRot));
        }
    }


    protected double getRotationDegrees(SkinPart skinPart) {
        SkinProperties properties = skinPart.getProperties();

        double maxAngle = properties.get(SkinProperty.WINGS_MAX_ANGLE);
        double minAngle = properties.get(SkinProperty.WINGS_MIN_ANGLE);
        double idleSpeed = properties.get(SkinProperty.WINGS_IDLE_SPEED);
        double flyingSpeed = properties.get(SkinProperty.WINGS_FLYING_SPEED);
        String movmentTypeName = properties.get(SkinProperty.WINGS_MOVMENT_TYPE);
        SkinProperty.MovementType movmentType = SkinProperty.MovementType.valueOf(movmentTypeName);

        double angle = 0;
        double flapTime = flying ? flyingSpeed : idleSpeed;

        angle = (((double) System.currentTimeMillis()) % flapTime);
        if (movmentType == SkinProperty.MovementType.EASE) {
            angle = Math.sin(angle / flapTime * Math.PI * 2);
        }
        if (movmentType == SkinProperty.MovementType.LINEAR) {
            angle = angle / flapTime;
        }

        double fullAngle = maxAngle - minAngle;
        if (movmentType == SkinProperty.MovementType.LINEAR) {
            return fullAngle * angle;
        }

        return -minAngle - fullAngle * ((angle + 1D) / 2);
    }

    protected Vector3f getRotationMatrix(SkinMarker marker) {
        switch (marker.getDirection()) {
            case UP:
                return Vector3f.YP;
            case DOWN:
                return Vector3f.YN;
            case SOUTH:
                return Vector3f.ZN;
            case NORTH:
                return Vector3f.ZP;
            case EAST:
                return Vector3f.XP;
            case WEST:
                return Vector3f.XN;
        }
        return Vector3f.YP;
    }

    protected ModelRenderer register(ISkinPartType skinPartType, int texOffsetX, int texOffsetY, IModelPartBuilder builder) {
        ModelRenderer model = new ModelRenderer(64, 32, texOffsetX, texOffsetY);
        builder.build(model);
        registeredParts.put(skinPartType, model);
        return model;
    }

    private interface IModelPartBuilder {
        void build(ModelRenderer model);
    }
}
