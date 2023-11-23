package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.init.platform.TransformationProvider;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.function.BiConsumer;

import manifold.ext.rt.api.auto;

@SuppressWarnings("unused")
public class SkinModelTransformer<T, M> {

    private final HashMap<ISkinPartType, Entry<T, M>> armors = new HashMap<>();
    private final HashMap<AbstractItemTransformType, Entry<T, M>> items = new HashMap<>();

    public static <M> void none(IPoseStack poseStack, M model) {
    }

    public static <T extends Entity, M extends IModel> void fromModel(IPoseStack poseStack, T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        auto itemSource = context.getReferenced();
        auto transformType = itemSource.getTransformType();
        auto itemModel = SkinModelManager.getInstance().getModel(bakedPart, bakedSkin, itemSource.getItem(), entity);
        float f1 = 16f;
        float f2 = 1 / 16f;
        boolean flag = transformType.isLeftHand();
        poseStack.scale(f1, f1, f1);
        TransformationProvider.handleTransforms(context.pose().pose(), itemModel, transformType, flag);
        poseStack.scale(f2, f2, f2);
        if (flag) {
            // we must reverse x-axis the direction of drawing,
            // but we should not change the normalMatrix,
            // because the normal direction is correct.
            poseStack.multiply(OpenMatrix4f.createScaleMatrix(-1, 1, 1));
        }
    }

    public void registerArmor(ISkinPartType partType, IJoint joint) {
        registerArmor(partType, (poseStack, entity, model, bakedPart, bakedSkin, context) -> apply(poseStack, joint, context));
    }

    public void registerArmor(ISkinPartType partType, BiConsumer<IPoseStack, M> transformer) {
        registerArmor(partType, (poseStack, entity, model, bakedPart, bakedSkin, context) -> transformer.accept(poseStack, model));
    }

    public void registerArmor(ISkinPartType partType, Entry<T, M> transformer) {
        armors.put(partType, transformer);
    }

    public void registerItem(AbstractItemTransformType transformType, Entry<T, M> transformer) {
        items.put(transformType, transformer);
    }

    public void apply(IPoseStack poseStack, IJoint joint, SkinRenderContext context) {
        IJointTransform[] transforms = context.getTransforms();
        if (transforms != null) {
            IJointTransform transform = transforms[joint.getId()];
            if (transform != null) {
                transform.apply(poseStack);
            }
        }
    }

    public void apply(IPoseStack poseStack, IModelPart modelPart) {
        if (modelPart != null) {
            modelPart.pose().transform(poseStack);
        }
    }

    public Entry<T, M> getArmour(ISkinPartType partType) {
        return armors.get(partType);
    }

    public Entry<T, M> getItem(AbstractItemTransformType transformType) {
        return items.get(transformType);
    }

    @FunctionalInterface
    public interface Entry<T, M> {
        void apply(IPoseStack poseStack, T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context);
    }
}
