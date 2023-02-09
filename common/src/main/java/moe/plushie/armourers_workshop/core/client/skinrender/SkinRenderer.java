package moe.plushie.armourers_workshop.core.client.skinrender;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
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
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

    public boolean prepare(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        ISkinPartType partType = bakedPart.getType();
        if (!context.shouldRenderPart(partType)) {
            return false;
        }
        if (partType == SkinPartTypes.BLOCK || partType == SkinPartTypes.BLOCK_MULTI || partType == SkinPartTypes.ADVANCED) {
            return true;
        }
        if (partType instanceof ICanHeld) {
            if (transformer.items.containsKey(context.transformType)) {
                return true;
            }
        }
        return transformer.armors.containsKey(bakedPart.getType());
    }


    public void apply(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        M model1 = getOverrideModel(model);
        PartTransform<T, M> partTransform = getPartTransform(entity, model1, bakedPart, bakedSkin, context);
        if (partTransform != null && model1 != null) {
            SkinTransform transform = bakedPart.getTransform();
            transform.setup(context.partialTicks, entity);
            transform.pre(context.poseStack);
            partTransform.apply(context.poseStack, entity, model1, bakedPart, bakedSkin, context);
            transform.post(context.poseStack);
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

    public void didRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        for (ModelPart modelRenderer : overriders) {
            modelRenderer.visible = true;
        }
        overriders.clear();
    }

    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, SkinRenderContext context) {
        if (profile != null) {
            ISkinType type = bakedSkin.getType();
            if (type instanceof ISkinArmorType && !profile.canSupport(type)) {
                return 0;
            }
        }
        int counter = 0;
        Skin skin = bakedSkin.getSkin();
        ColorScheme scheme1 = bakedSkin.resolve(entity, scheme);
        SkinRenderObjectBuilder builder = context.getBuffer(skin);
        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
            if (!prepare(entity, model, bakedPart, bakedSkin, context)) {
                continue;
            }
            boolean shouldRenderPart = bakedSkin.shouldRenderPart(entity, model, bakedPart, context);
            context.pushPose();
            apply(entity, model, bakedPart, bakedSkin, context);
            builder.addPartData(bakedPart, bakedSkin, scheme1, shouldRenderPart, context);
            counter += renderChildPart(bakedPart, bakedSkin, scheme1, shouldRenderPart, builder, context);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShapeBox(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(bakedPart.getId()), context);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShapePoint(Vector3f.ZERO, context);
            }
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
//            RenderUtils.drawPoint(poseStack, null, 32, buffers);
            context.popPose();
        }

        if (ModDebugger.skinBounds) {
            builder.addShapeBox(bakedSkin.getRenderShape(entity, model, context.itemStack, context.transformType, this).bounds(), UIColor.RED, context);
        }
        if (ModDebugger.skinBounds) {
            builder.addShapePoint(Vector3f.ZERO, context);
        }
        if (ModDebugger.armature && skin.getType() instanceof ISkinArmorType) {
            builder.addArmatureBox(context.getTransforms(), context);
        }

        return counter;
    }

    public int renderChildPart(BakedSkinPart parentPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRenderPart, SkinRenderObjectBuilder builder, SkinRenderContext context) {
        int counter = 0;
        for (BakedSkinPart bakedPart : parentPart.getChildren()) {
            context.pushPose();
            SkinTransform transform = bakedPart.getTransform();
            transform.apply(context.poseStack);
            builder.addPartData(bakedPart, bakedSkin, scheme, shouldRenderPart, context);
            counter += renderChildPart(bakedPart, bakedSkin, scheme, shouldRenderPart, builder, context);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShapeBox(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(bakedPart.getId()), context);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShapePoint(Vector3f.ZERO, context);
            }
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
            context.popPose();
        }
        return counter;
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

    public M getOverrideModel(M model) {
        return model;
    }

    public PartTransform<T, M> getPartTransform(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        ISkinPartType partType = bakedPart.getType();
        PartTransform<T, M> transform = null;
        if (partType instanceof ICanHeld) {
            transform = transformer.items.get(context.transformType);
        }
        if (transform == null) {
            transform = transformer.armors.get(partType);
        }
        return transform;
    }

    public EntityProfile getProfile() {
        return profile;
    }

    public interface Plugin<T extends LivingEntity, V extends EntityModel<T>, M extends IModelHolder<V>> {

        RenderLayer<T, V> getOverrideLayer(SkinRenderer<T, V, M> skinRenderer, LivingEntityRenderer<T, V> entityRenderer, RenderLayer<T, V> renderLayer);
    }

    public interface Factory<T> {

        T create(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel, EntityProfile entityProfile);
    }

    @FunctionalInterface
    public interface PartTransform<T, M> {
        void apply(IPoseStack poseStack, T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context);
    }

    public static class Transformer<T, M> {

        final HashMap<ISkinPartType, PartTransform<T, M>> armors = new HashMap<>();
        final HashMap<ItemTransforms.TransformType, PartTransform<T, M>> items = new HashMap<>();

        public static <M> void none(IPoseStack poseStack, M model) {
        }

        public static <T extends Entity, M0 extends Model, M extends IModelHolder<M0>> void withModel(IPoseStack poseStack, T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
            ItemStack itemStack = context.itemStack;
            ItemTransforms.TransformType transformType = context.transformType;
            final float f1 = 16f;
            final float f2 = 1 / 16f;
            final boolean flag = (transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
            poseStack.scale(f1, f1, f1);
            BakedModel bakedModel = SkinModelManager.getInstance().getModel(bakedPart.getType(), itemStack, entity.level, entity);
            TransformationProvider.handleTransforms(poseStack, bakedModel, transformType, flag);
            poseStack.scale(f2, f2, f2);
            if (flag) {
                // we must reverse x-axis the direction of drawing,
                // but we should not change the normalMatrix,
                // because the normal direction is correct.
                poseStack.lastPose().scale(-1, 1, 1);
            }
        }

        public void registerArmor(ISkinPartType partType, Function<M, ModelPart> transformer) {
            registerArmor(partType, (poseStack, entity, model, bakedPart, bakedSkin, context) -> apply(poseStack, transformer.apply(model)));
        }

        public void registerArmor(ISkinPartType partType, IJoint joint) {
            registerArmor(partType, (poseStack, entity, model, bakedPart, bakedSkin, context) -> apply(poseStack, joint, context));
        }

        public void registerArmor(ISkinPartType partType, BiConsumer<IPoseStack, M> transformer) {
            registerArmor(partType, (poseStack, entity, model, bakedPart, bakedSkin, context) -> transformer.accept(poseStack, model));
        }

        public void registerArmor(ISkinPartType partType, PartTransform<T, M> transformer) {
            armors.put(partType, transformer);
        }

        public void registerItem(ItemTransforms.TransformType transformType, PartTransform<T, M> transformer) {
            items.put(transformType, transformer);
        }

        public void apply(IPoseStack poseStack, IJoint joint, SkinRenderContext context) {
            ITransformf[] transforms = context.getTransforms();
            if (transforms != null) {
                ITransformf transform = transforms[joint.getId()];
                if (transform != null) {
                    transform.apply(poseStack);
                }
            }
        }

        public void apply(IPoseStack poseStack, ModelPart modelRenderer) {
            if (modelRenderer == null) {
                return;
            }
            poseStack.translate(modelRenderer.x, modelRenderer.y, modelRenderer.z);
            if (modelRenderer.zRot != 0) {
                poseStack.rotate(Vector3f.ZP.rotation(modelRenderer.zRot));
            }
            if (modelRenderer.yRot != 0) {
                poseStack.rotate(Vector3f.YP.rotation(modelRenderer.yRot));
            }
            if (modelRenderer.xRot != 0) {
                poseStack.rotate(Vector3f.XP.rotation(modelRenderer.xRot));
            }
        }
    }
}

