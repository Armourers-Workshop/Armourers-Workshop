package moe.plushie.armourers_workshop.core.client.render.layer;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.compat.client.renderer.layer.AbstractRenderLayer;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.render.plugin.EntityRenderPlugin;
import moe.plushie.armourers_workshop.core.client.render.plugin.EpicFightEntityRenderPlugin;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.entity.Entity;

@OnlyIn(Dist.CLIENT)
public class SkinWardrobeLayer<T extends Entity, S extends EntityRenderState, M extends IEntityModel<S>> extends AbstractRenderLayer<T, S, M> {

    protected final BakedArmature armature;

    public SkinWardrobeLayer(BakedArmatureTransformer armatureTransformer, IEntityRenderer<T, S> renderer) {
        super(renderer);
        this.armature = new BakedArmature(armatureTransformer.armature());
    }

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, float netHeadYaw, float headPitch, IGraphicsContext context) {
        // respect invisibility potions etc.
        if (renderState.isInvisible()) {
            return;
        }
        var model = renderState.armors();
        if (model.isEmpty()) {
            return; // nothing to rendering!
        }
        var resolvedArmature = renderState.getArmature(armature);
        if (resolvedArmature == null) {
            return; // the armature doesn't ready!!!
        }
        context.saveGraphicsState();

        // apply the baby body scale and offset, note it also scaled the head so we will rescale head in the plugin.
        if (renderState.isBaby()) {
            applyBabyScale(renderState, context);
        }

        // efm apply a special transform for each layer,
        // but we read is the armature based on model pose stack,
        // so we need to reset to the original pose stack.
        var efm = Objects.safeCast(renderState.renderPlugin(), EpicFightEntityRenderPlugin.class);
        if (efm != null) {
            context.ctm().last().set(efm.overridePose());
        }

        context.scaleCTM(0.0625f, 0.0625f, 0.0625f);

        model.setPartialTick(renderState.partialTick());
        model.setAnimationTick(renderState.animationTick());
        model.setAnimationManager(renderState.animationManager());
        model.setOutlineColor(renderState.outlineColor());

        model.render(renderState, resolvedArmature, lightmap, overlay, context);

        context.restoreGraphicsState();
    }

    protected void applyBabyScale(S renderState, IGraphicsContext context) {
        var babyPart = getBabyPart(renderState.renderPlugin());
        if (babyPart != null) {
            var pose = (IModelBabyPose) babyPart.pose();
            var scale = pose.bodyScale();
            var offset = pose.bodyOffset();
            context.scaleCTM(scale, scale, scale);
            context.translateCTM(offset);
        }
    }

    private IModelPart getBabyPart(EntityRenderPlugin<?, ?> renderPlugin) {
        var context = Objects.flatMap(renderPlugin, EntityRenderPlugin::context);
        if (context != null) {
            return context.entityModel().abi$getPartByName("baby");
        }
        return null;
    }
}
