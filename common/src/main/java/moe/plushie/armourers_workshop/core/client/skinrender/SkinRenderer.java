package moe.plushie.armourers_workshop.core.client.skinrender;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinModelTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.advanced.AdvancedPartType;
import moe.plushie.armourers_workshop.core.skin.part.block.BlockPartType;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinRenderer<T extends Entity, M extends IModel> {

    protected final EntityProfile profile;
    protected final SkinModelTransformer<T, M> transformer = new SkinModelTransformer<>();
    protected final SkinVisibilityTransformer<M> visibilityTransformer = new SkinVisibilityTransformer<>();

    public SkinRenderer(EntityProfile profile) {
        this.profile = profile;
    }

    protected void init(EntityRenderer<T> entityRenderer) {
    }

    protected void init(SkinModelTransformer<T, M> transformer) {
    }

    protected void init(SkinVisibilityTransformer<M> watcher) {
    }

    public void initWithRenderer(EntityRenderer<?> entityRenderer) {
        EntityRenderer<T> renderer = ObjectUtils.unsafeCast(entityRenderer);
        init(transformer);
        init(visibilityTransformer);
        init(renderer);
    }

    public boolean prepare(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        auto partType = bakedPart.getType();
        if (!context.shouldRenderPart(partType)) {
            return false;
        }
        if (partType instanceof BlockPartType || partType instanceof AdvancedPartType) {
            return true;
        }
        if (partType instanceof ICanHeld) {
            if (transformer.getItem(context.getTransformType()) != null) {
                return true;
            }
        }
        return transformer.getArmour(bakedPart.getType()) != null;
    }


    public void apply(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        // apply the part self transform(pre).
        auto transform = bakedPart.getTransform();
        transform.setup(context.getPartialTicks(), entity);
        transform.pre(context.pose());

        // apply the part model transform.
        auto model1 = getOverrideModel(model);
        auto partTransform = getPartTransform(entity, model1, bakedPart, bakedSkin, context);
        if (partTransform != null && model1 != null) {
            partTransform.apply(context.pose(), entity, model1, bakedPart, bakedSkin, context);
            //SkinPartTransform transform = bakedPart.getTransform();
            //transform.setup(context.partialTicks, entity);
            //transform.pre(context.poseStack1);
            //partTransform.apply(context.poseStack, entity, model1, bakedPart, bakedSkin, context);
            //transform.post(context.poseStack1);
        }

        // apply the part self transform(post).
        transform.post(context.pose());
    }


    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
    }

    public void willRenderModel(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        visibilityTransformer.willRender(model, renderData.getOverriddenManager());
    }

    public void didRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        visibilityTransformer.didRender(model, renderData.getOverriddenManager());
    }

    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, SkinRenderContext context) {
        if (profile != null) {
            ISkinType type = bakedSkin.getType();
            if (type instanceof ISkinArmorType && !profile.canSupport(type)) {
                return 0;
            }
        }
        int counter = 0;
        auto scheme1 = bakedSkin.resolve(entity, scheme);
        auto builder = context.getBuffer(bakedSkin);
        for (auto bakedPart : bakedSkin.getSkinParts()) {
            if (!prepare(entity, model, bakedPart, bakedSkin, context)) {
                continue;
            }
            boolean shouldRenderPart = bakedSkin.shouldRenderPart(entity, model, bakedPart, context);
            context.pushPose();
            apply(entity, model, bakedPart, bakedSkin, context);
            builder.addPart(bakedPart, bakedSkin, scheme1, shouldRenderPart, context);
            counter += renderChildPart(bakedPart, bakedSkin, scheme1, shouldRenderPart, builder, context);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShape(bakedPart.getRenderShape(), ColorUtils.getPaletteColor(bakedPart.getId()), context);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShape(Vector3f.ZERO, context);
            }
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
            context.popPose();
        }

        if (ModDebugger.skinBounds) {
            builder.addShape(bakedSkin.getRenderShape(entity, model, context.getReferenced(), this), UIColor.RED, context);
        }
        if (ModDebugger.skinOrigin) {
            builder.addShape(Vector3f.ZERO, context);
        }
        if (ModDebugger.armature && bakedSkin.getType() instanceof ISkinArmorType) {
            builder.addShape(context.getTransforms(), context);
        }

        return counter;
    }

    public int renderChildPart(BakedSkinPart parentPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRenderPart, SkinRenderBufferSource.ObjectBuilder builder, SkinRenderContext context) {
        int counter = 0;
        for (auto bakedPart : parentPart.getChildren()) {
            auto transform = bakedPart.getTransform();
            context.pushPose();
            transform.apply(context.pose());
            builder.addPart(bakedPart, bakedSkin, scheme, shouldRenderPart, context);
            counter += renderChildPart(bakedPart, bakedSkin, scheme, shouldRenderPart, builder, context);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShape(bakedPart.getRenderShape(), ColorUtils.getPaletteColor(bakedPart.getId()), context);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShape(Vector3f.ZERO, context);
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

    public SkinModelTransformer.Entry<T, M> getPartTransform(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        ISkinPartType partType = bakedPart.getType();
        if (partType instanceof ICanHeld) {
            auto transform = transformer.getItem(context.getTransformType());
            if (transform != null) {
                return transform;
            }
        }
        return transformer.getArmour(partType);
    }

    public M getOverrideModel(M model) {
        return model;
    }

    public EntityProfile getProfile() {
        return profile;
    }

    public interface Factory<T> {

        T create(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel, EntityProfile entityProfile);
    }
}


