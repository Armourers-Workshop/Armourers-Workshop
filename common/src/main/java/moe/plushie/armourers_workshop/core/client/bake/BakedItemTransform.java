package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModel;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelManager;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelResolver;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemOverride;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransform;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.init.ModDebugger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class BakedItemTransform {

    protected final SkinItemModel itemModel;
    protected final OpenTransform3f offsetTransform;

    protected BakedItemTransform(SkinItemModel itemModel, OpenTransform3f offsetTransform) {
        this.itemModel = itemModel;
        this.offsetTransform = offsetTransform;
    }

    public static BakedItemTransform create(List<BakedSkinPart> skinParts, OpenItemTransforms itemTransforms, SkinType skinType) {
        // ..
        if (itemTransforms == null) {
            var itemModel = SkinItemModelManager.getInstance().getModel(skinType);
            return new Builtin(itemModel, null);
        }
        return create(skinParts, itemTransforms);
    }

    private static BakedItemTransform create(List<BakedSkinPart> skinParts, OpenItemTransforms itemTransforms) {
        var transforms = new EnumMap<OpenItemDisplayContext, OpenItemTransform>(OpenItemDisplayContext.class);
        for (var value : OpenItemDisplayContext.values()) {
            var itemTransform = itemTransforms.get(value);
            if (itemTransform != null) {
                transforms.put(value, OpenItemTransform.create(itemTransform));
            }
        }
        var overrides = new ArrayList<SkinItemOverride>();
        for (var part : skinParts) {
            //overrides.addAll(SkinUtils.getItemOverrides(part.getType()));
            // we need search child part?
            //

        }
        var itemModel = new SkinItemModel(null, overrides, transforms);
        return new Custom(itemModel, itemTransforms.offset());
    }

    public void apply(EntityRenderState renderState, BakedSkin skin, BakedArmature armature, ConcurrentRenderingContext context) {

        if (ModDebugger.targetBounds) {
            context.draw(ShapeElement.arrow(0, 0, 0, 16, 16, 16));
            context.draw(ShapeElement.stroke(-8, -8, -8, 16, 16, 16, 0xff00ffff));
        }

        var itemSource = context.itemSource();
        var itemTransform = resolveItemTransform(itemSource, itemSource.itemModelResolver());
        if (itemTransform != OpenItemTransform.NO_TRANSFORM) {
            applyItemTransform(itemTransform, itemSource.displayContext(), context.ctm());
        }

        if (ModDebugger.targetBounds) {
            context.draw(ShapeElement.arrow(0, 0, 0, 16, 16, 16));
            context.draw(ShapeElement.stroke(-8, -8, -8, 16, 16, 16, 0xffffff00));
        }

        var displayBox = itemSource.displayBox();
        if (displayBox != null) {
            applyScaleInBox(itemTransform, itemSource.displayContext(), skin, displayBox, context.ctm());
        }
    }

    protected void applyItemTransform(OpenItemTransform itemTransform, OpenItemDisplayContext displayContext, IPoseStack poseStack) {
        // apply left item transform.
        itemTransform.apply(displayContext.isLeftHand(), poseStack);
        // apply right item transform.
        if (offsetTransform != null) {
            offsetTransform.apply(poseStack);
        }
    }

    protected void applyScaleInBox(OpenItemTransform itemTransform, OpenItemDisplayContext displayContext, BakedSkin skin, OpenRectangle3f displayBox, IPoseStack poseStack) {
        var renderBounds = skin.getRenderBounds(itemTransform, displayContext);
        // calculate and apply skin scale.
        float dx = displayBox.width() * 16;
        float dy = displayBox.height() * 16;
        float dz = displayBox.depth() * 16;
        float scale = Math.min(Math.min(dx / renderBounds.width(), dy / renderBounds.height()), dz / renderBounds.depth());
        //poseStack.scale(scale / scale.getX(), scale / scale.getY(), scale / scale.getZ());
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-renderBounds.midX(), -renderBounds.midY(), -renderBounds.midZ());
    }

    protected SkinItemModel resolveItemModel(SkinItemSource itemSource, SkinItemModelResolver itemModelResolver) {
        // in some cases we need to disable item overrides, users:
        //  Epic Fight Mod (Shield Render)
        var itemProperties = itemSource.properties();
        if (itemProperties != null && !itemProperties.isAllowOverrides()) {
            return itemModel;
        }
        return itemModelResolver.resolve(itemModel, itemSource.itemStack(), 0, itemSource.displayContext());
    }

    protected OpenItemTransform resolveItemTransform(SkinItemSource itemSource, @Nullable SkinItemModelResolver itemModelResolver) {
        // the user provided a custom item model?
        if (itemModelResolver != null) {
            var itemModel = resolveItemModel(itemSource, itemModelResolver);
            return itemModel.getTransform(itemSource.displayContext());
        }
        return OpenItemTransform.NO_TRANSFORM;
    }

    private static class Builtin extends BakedItemTransform {

        protected Builtin(SkinItemModel itemModel, OpenTransform3f offsetTransform) {
            super(itemModel, offsetTransform);
        }
    }

    private static class Custom extends BakedItemTransform {

        protected Custom(SkinItemModel itemModel, OpenTransform3f afterTransform) {
            super(itemModel, afterTransform);
        }

        @Override
        protected void applyItemTransform(OpenItemTransform itemTransform, OpenItemDisplayContext displayContext, IPoseStack poseStack) {
            // in normal case will be provided by the custom item transforms.
            if (displayContext != OpenItemDisplayContext.NONE) {
                super.applyItemTransform(itemTransform, displayContext, poseStack);
            }
        }

        @Override
        protected void applyScaleInBox(OpenItemTransform itemTransform, OpenItemDisplayContext displayContext, BakedSkin skin, OpenRectangle3f displayBox, IPoseStack poseStack) {
            // in the none case, we need render it in display box inside if specified.
            if (displayContext == OpenItemDisplayContext.NONE) {
                super.applyScaleInBox(itemTransform, displayContext, skin, displayBox, poseStack);
            }
        }
    }
}
