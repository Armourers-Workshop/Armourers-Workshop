package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.blockentity.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.state.SkinnableRenderState;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;

@OnlyIn(Dist.CLIENT)
public class SkinnableBlockRenderer<T extends SkinnableBlockEntity, S extends SkinnableRenderState> extends AbstractBlockEntityRenderer<T, S> {

    private final BakedArmature armature = new BakedArmature(Armatures.ANY);

    public SkinnableBlockRenderer(Context context) {
        super(context);
    }

    @Override
    protected int abi$getViewDistance() {
        return ModConfig.Client.renderDistanceBlockSkin;
    }

    @Override
    protected boolean abi$shouldRender(T entity) {
        // only use custom render in the parent block entity.
        return entity.isParent();
    }

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        var model = renderState.slots();
        if (model.isEmpty()) {
            return;
        }
        context.saveGraphicsState();

        context.translateCTM(0.5f, 0.5f, 0.5f);
        context.rotateCTM(renderState.renderRotations());
        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        model.setPartialTick(renderState.partialTick());
        model.setAnimationTick(renderState.animationTick());
        model.setAnimationManager(renderState.animationManager());
        model.setOutlineColor(0); // never show outline in the skinnable block.

        model.render(null, armature, lightmap, overlay, context);

        if (ModDebugger.skinnable) {
            model.slots().forEach(it -> it.skin().blockBounds().forEach((pos, rect) -> {
                context.saveGraphicsState();
                context.scaleCTM(-1, -1, 1);
                context.translateCTM(pos.x() * 16f, pos.y() * 16f, pos.z() * 16f);
                context.draw(ShapeElement.stroke(rect, Colors.RED));
                context.restoreGraphicsState();
            }));
        }

        context.restoreGraphicsState();
    }
}
