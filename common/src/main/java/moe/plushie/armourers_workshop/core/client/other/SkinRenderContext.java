package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.Iterators;
import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.armature.JointTransformBuilder;
import moe.plushie.armourers_workshop.core.armature.Joints;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SkinRenderContext {

    private static final Iterator<SkinRenderContext> POOL = Iterators.cycle(make(32));

    public int light;
    public float partialTicks;
    public IPoseStack poseStack;
    public SkinRenderData renderData;
    public MultiBufferSource buffers;
    public ItemTransforms.TransformType transformType;

    private ITransformf[] transforms;

    public int slotIndex;
    public ItemStack itemStack;

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, ItemTransforms.TransformType transformType, IPoseStack poseStack, MultiBufferSource buffers) {
        SkinRenderContext context = POOL.next();
        context.init(renderData, light, partialTick, transformType, poseStack, buffers);
        return context;
    }

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, IPoseStack poseStack, MultiBufferSource buffers) {
        return alloc(renderData, light, partialTick, ItemTransforms.TransformType.NONE, poseStack, buffers);
    }

    private static Collection<SkinRenderContext> make(int size) {
        ArrayList<SkinRenderContext> contexts = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            contexts.add(new SkinRenderContext());
        }
        return contexts;
    }

    public void init(SkinRenderData renderData, int light, float partialTick, ItemTransforms.TransformType transformType, IPoseStack poseStack, MultiBufferSource buffers) {
        this.renderData = renderData;
        this.light = light;
        this.partialTicks = partialTick;
        this.transformType = transformType;
        this.poseStack = poseStack;
        this.buffers = buffers;
        // ig
        this.itemStack = ItemStack.EMPTY;
        this.slotIndex = 0;
        this.transforms = null;
    }

    public void release() {
    }

    public void pushPose() {
        poseStack.pushPose();
    }

    public void popPose() {
        poseStack.popPose();
    }

    public boolean shouldRenderPart(ISkinPartType partType) {
        if (renderData != null && renderData.overrideParts != null) {
            return !renderData.overrideParts.contains(partType);
        }
        return true;
    }

    public void setItem(ItemStack itemStack, int slotIndex) {
        this.itemStack = itemStack;
        this.slotIndex = slotIndex;
    }

    public void setTransforms(ITransformf[] transforms) {
        this.transforms = transforms;
    }

    public void setTransforms(Entity entity, IModelHolder<?> model) {

        IHumanoidModelHolder<?> mm = ObjectUtils.safeCast(model, IHumanoidModelHolder.class);
        if (mm == null) {
            return;
        }
        JointTransformBuilder builder = JointTransformBuilder.of(Armatures.BIPPED);

        //builder.put(Joints.BIPPED_HEAD, get(mm::getHeadPart));
        builder.put(Joints.BIPPED_HEAD, getHead(mm, mm::getHeadPart));

        builder.put(Joints.BIPPED_CHEST, get(mm::getBodyPart));
        builder.put(Joints.BIPPED_TORSO, get(mm::getBodyPart, new Vector3f(0, 6, 0)));

        builder.put(Joints.BIPPED_SKIRT, getMp(mm::getBodyPart, mm::getLeftLegPart, mm::getRightLegPart));

        builder.put(Joints.BIPPED_LEFT_ARM, get(mm::getLeftArmPart));
        builder.put(Joints.BIPPED_RIGHT_ARM, get(mm::getRightArmPart));

        builder.put(Joints.BIPPED_LEFT_HAND, get(mm::getLeftArmPart, new Vector3f(0, 4, 0)));
        builder.put(Joints.BIPPED_RIGHT_HAND, get(mm::getRightArmPart, new Vector3f(0, 4, 0)));

        builder.put(Joints.BIPPED_LEFT_THIGH, get(mm::getLeftLegPart));
        builder.put(Joints.BIPPED_RIGHT_THIGH, get(mm::getRightLegPart));

        builder.put(Joints.BIPPED_LEFT_LEG, get(mm::getLeftLegPart, new Vector3f(0, 6, 0)));
        builder.put(Joints.BIPPED_RIGHT_LEG, get(mm::getRightLegPart, new Vector3f(0, 6, 0)));

        builder.put(Joints.BIPPED_LEFT_FOOT, get(mm::getLeftLegPart));
        builder.put(Joints.BIPPED_RIGHT_FOOT, get(mm::getRightLegPart));

        builder.put(Joints.BIPPED_LEFT_WING, get(mm::getBodyPart));
        builder.put(Joints.BIPPED_RIGHT_WING, get(mm::getBodyPart));

        builder.put(Joints.BIPPED_LEFT_PHALANX, get(mm::getBodyPart, new Vector3f(0, 0, 2)));
        builder.put(Joints.BIPPED_RIGHT_PHALANX, get(mm::getBodyPart, new Vector3f(0, 0, 2)));

        transforms = builder.build();
    }

    public ITransformf[] getTransforms() {
        return transforms;
    }

    public SkinRenderObjectBuilder getBuffer(@NotNull Skin skin) {
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        return bufferBuilder.getBuffer(skin);
    }

    private void apply(IPoseStack poseStack, ModelPart modelPart) {
        if (modelPart == null) {
            return;
        }
        poseStack.translate(modelPart.x, modelPart.y, modelPart.z);
        if (modelPart.zRot != 0) {
            poseStack.rotate(Vector3f.ZP.rotation(modelPart.zRot));
        }
        if (modelPart.yRot != 0) {
            poseStack.rotate(Vector3f.YP.rotation(modelPart.yRot));
        }
        if (modelPart.xRot != 0) {
            poseStack.rotate(Vector3f.XP.rotation(modelPart.xRot));
        }
    }

    private ITransformf get(Supplier<ModelPart> supplier) {
        return get(supplier, Vector3f.ZERO);
    }

    private ITransformf get(Supplier<ModelPart> supplier, Vector3f offset) {
        return get(supplier, poseStack -> poseStack.translate(offset.getX(), offset.getY(), offset.getZ()));
    }

    private ITransformf get(Supplier<ModelPart> supplier, Consumer<IPoseStack> applier) {
        IPoseStack poseStack1 = AbstractPoseStack.empty();
        apply(poseStack1, supplier.get());
        applier.accept(poseStack1);
        return poseStack -> poseStack.multiply(poseStack1);
    }

    private ITransformf getMp(Supplier<ModelPart> supplier1, Supplier<ModelPart> supplier2, Supplier<ModelPart> supplier3) {
        IPoseStack poseStack1 = AbstractPoseStack.empty();

        ModelPart body = supplier1.get();
        ModelPart leg1 = supplier2.get();
        ModelPart leg2 = supplier3.get();

        float z = (leg1.z + leg2.z) / 2;
        poseStack1.translate(body.x, leg1.y, z);
        if (body.yRot != 0) {
            poseStack1.rotate(Vector3f.YP.rotation(body.yRot));
        }
        float xRot = (ort(leg1.xRot) + ort(leg2.xRot)) / 2;
        if (Float.compare(xRot, 0) != 0) {
            poseStack1.rotate(Vector3f.XP.rotation(xRot));
        }
        return poseStack -> poseStack.multiply(poseStack1);
    }

    private ITransformf getHead(IHumanoidModelHolder<?> model, Supplier<ModelPart> supplier) {
        return get(supplier, poseStack -> {
            if (model.isBaby()) {
                float scale = model.getBabyScale();
                IVector3f offset = model.getBabyOffset();
                if (offset == null) {
                    return;
                }
                poseStack.scale(scale, scale, scale);
                poseStack.translate(offset.getX() / 16f, offset.getY() / 16f, offset.getZ() / 16f);
            }
        });
    }

    private float ort(float q) {
        float pi = (float) Math.PI;
        if (q > pi) {
            return q - pi * 2;
        }
        if (q < -pi) {
            return q + pi * 2;
        }
        return q;
    }

}
