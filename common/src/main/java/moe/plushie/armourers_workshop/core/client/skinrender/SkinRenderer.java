package moe.plushie.armourers_workshop.core.client.skinrender;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.*;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.transform.SkinTransform;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.platform.TransformationProvider;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class SkinRenderer<T extends Entity, V extends Model, M extends IModelHolder<V>> {

    protected final EntityProfile profile;
    protected final Transformer<T, M> transformer = new Transformer<>();

    protected final ArrayList<ModelPart> overriders = new ArrayList<>();

    public SkinRenderer(EntityProfile profile) {
        this.profile = profile;
    }

    public void init(EntityRenderer<T> entityRenderer) {
    }

    public void initTransformers() {
    }

    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemStack itemStack, ItemTransforms.TransformType transformType) {
        ISkinPartType partType = bakedPart.getType();
        if (partType == SkinPartTypes.BLOCK || partType == SkinPartTypes.BLOCK_MULTI) {
            return true;
        }
        if (partType instanceof ICanHeld) {
            if (transformer.items.containsKey(transformType)) {
                return true;
            }
        }
        return transformer.armors.containsKey(bakedPart.getType());
    }


    public void apply(T entity, M model, ItemStack itemStack, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        M model1 = getOverrideModel(model);
        ITransform<T, M> op = getPartTransform(entity, model1, itemStack, context.transformType, bakedPart);
        if (op != null && model1 != null) {
            SkinTransform transform = bakedPart.getTransform();
            transform.setup(context.partialTicks, entity);
            transform.pre(context.openPoseStack);
            op.apply(context.poseStack, entity, model1, itemStack, context.transformType, bakedPart);
            transform.post(context.openPoseStack);
        }
    }

    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
    }

    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        overriders.clear();
        apply(entity, model, renderData.getOverriddenManager(), renderData);
    }

    public void willRenderModel(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        overriders.forEach(m -> m.visible = false);
    }

    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, int slotIndex, SkinRenderContext context) {
        if (profile != null) {
            ISkinType type = bakedSkin.getType();
            if (type instanceof ISkinArmorType && !profile.canSupport(type)) {
                return 0;
            }
        }
        int counter = 0;
        PoseStack matrixStack = context.poseStack;
        Skin skin = bakedSkin.getSkin();
        ColorScheme scheme1 = bakedSkin.resolve(entity, scheme);
        SkinRenderObjectBuilder builder = context.getBuffer(skin);
        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
            if (!prepare(entity, model, bakedSkin, bakedPart, itemStack, context.transformType)) {
                continue;
            }
            boolean shouldRenderPart = bakedSkin.shouldRenderPart(bakedPart, entity, itemStack, context.transformType);
            matrixStack.pushPose();
            apply(entity, model, itemStack, bakedPart, bakedSkin, context);
            builder.addPartData(bakedPart, scheme1, context.light, context.partialTicks, slotIndex, matrixStack, shouldRenderPart);
            counter += renderChildPart(bakedPart, scheme1, slotIndex, shouldRenderPart, builder, context);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShapeData(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(bakedPart.getId()), matrixStack);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShapeData(Vector3f.ZERO, matrixStack);
            }
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
//            RenderUtils.drawPoint(matrixStack, null, 32, buffers);
            matrixStack.popPose();
        }

        if (ModDebugger.skinBounds) {
            builder.addShapeData(bakedSkin.getRenderShape(entity, model, itemStack, context.transformType, this).bounds(), UIColor.RED, matrixStack);
        }
        if (ModDebugger.skinBounds) {
            builder.addShapeData(Vector3f.ZERO, matrixStack);
        }

        return counter;
    }

    public int renderChildPart(BakedSkinPart parentPart, ColorScheme scheme, int slotIndex, boolean shouldRenderPart, SkinRenderObjectBuilder builder, SkinRenderContext context) {
        int counter = 0;
        for (BakedSkinPart bakedPart : parentPart.getChildren()) {
            PoseStack matrixStack = context.poseStack;
            SkinTransform transform = bakedPart.getTransform();
            matrixStack.pushPose();
            transform.apply(context.openPoseStack);
            builder.addPartData(bakedPart, scheme, context.light, context.partialTicks, slotIndex, matrixStack, shouldRenderPart);
            counter += renderChildPart(bakedPart, scheme, slotIndex, shouldRenderPart, builder, context);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShapeData(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(bakedPart.getId()), matrixStack);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShapeData(Vector3f.ZERO, matrixStack);
            }
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
            matrixStack.popPose();
        }
        return counter;
    }

    public void didRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        for (ModelPart modelRenderer : overriders) {
            modelRenderer.visible = true;
        }
        overriders.clear();
    }

    protected void addModelOverride(ModelPart modelRenderer) {
        if (ModDebugger.modelOverride) {
            return;
        }
        if (modelRenderer == null) {
            return;
        }
        if (!modelRenderer.visible) {
            return;
        }
        modelRenderer.visible = false;
        overriders.add(modelRenderer);
    }

    protected M getOverrideModel(M model) {
        return model;
    }

    public ITransform<T, M> getPartTransform(T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        ISkinPartType partType = bakedPart.getType();
        ITransform<T, M> transform = null;
        if (partType instanceof ICanHeld) {
            transform = transformer.items.get(transformType);
        }
        if (transform == null) {
            transform = transformer.armors.get(partType);
        }
        return transform;
    }

    public EntityProfile getProfile() {
        return profile;
    }

    @FunctionalInterface
    public interface ITransform<T, M> {
        void apply(PoseStack matrixStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart);
    }

    public static class Transformer<T, M> {

        final HashMap<ISkinPartType, ITransform<T, M>> armors = new HashMap<>();
        final HashMap<ItemTransforms.TransformType, ITransform<T, M>> items = new HashMap<>();

        public static <M> void none(PoseStack matrixStack, M model) {
        }

        public static <T extends Entity, M0 extends Model, M extends IModelHolder<M0>> void withModel(PoseStack matrixStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
            final float f1 = 16f;
            final float f2 = 1 / 16f;
            final boolean flag = (transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
//            ModDebugger.translate(matrixStack);
            matrixStack.scale(f1, f1, f1);
            BakedModel bakedModel = SkinModelManager.getInstance().getModel(bakedPart.getType(), itemStack, entity.level, entity);
            TransformationProvider.handleTransforms(matrixStack, bakedModel, transformType, flag);
            matrixStack.scale(f2, f2, f2);
//            ModDebugger.rotate(matrixStack);
//            ModDebugger.scale(matrixStack);
            if (flag) {
                matrixStack.scale(-1, 1, 1);
            }
        }

        public void registerArmor(ISkinPartType partType, Function<M, ModelPart> transformer) {
            registerArmor(partType, (matrixStack, entity, model, itemStack, transformType, bakedPart) -> apply(matrixStack, transformer.apply(model)));
        }

        public void registerArmor(ISkinPartType partType, BiConsumer<PoseStack, M> transformer) {
            registerArmor(partType, (matrixStack, entity, model, itemStack, transformType, bakedPart) -> transformer.accept(matrixStack, model));
        }

        public void registerArmor(ISkinPartType partType, ITransform<T, M> transformer) {
            armors.put(partType, transformer);
        }

        public void registerItem(ItemTransforms.TransformType transformType, BiConsumer<PoseStack, M> transformer) {
            registerItem(transformType, (matrixStack, entity, model, itemStack, transformType1, bakedPart) -> transformer.accept(matrixStack, model));
        }

        public void registerItem(ItemTransforms.TransformType transformType, ITransform<T, M> transformer) {
            items.put(transformType, transformer);
        }

        public void apply(PoseStack matrixStack, ModelPart modelRenderer) {
            if (modelRenderer == null) {
                return;
            }
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
    }
}

