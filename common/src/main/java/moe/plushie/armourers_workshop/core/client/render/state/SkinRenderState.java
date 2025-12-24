package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.utils.Collections;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkinRenderState {

    protected float partialTick = 1.0f;
    protected double animationTick = 0.0d;

    protected int outlineColor = 0;

    protected SkinItemSource itemSource = SkinItemSource.EMPTY;
    protected AnimationManager animationManager = AnimationManager.NONE;

    protected List<EntitySlot> slots = Collections.emptyList();

    public void prepare(List<EntitySlot> slots) {
        this.slots = slots;
    }

    public int render(@Nullable EntityRenderState renderState, @Nullable BakedArmature armature, int lightmap, int overlay, IGraphicsContext context) {
        // we will use placeholder mannequin when the entity renders state no specified.
        if (renderState == null) {
            renderState = MannequinRenderState.getPlaceholder();
        }
        var renderCount = 0;
        var renderingContext = SkinRenderContext.newInstance(context);
        for (var slot : slots) {
            var skin = slot.skin();

            var resolvedArmature = skin.resolve(renderState, armature);
            var resolvedPaintScheme = skin.resolve(renderState, slot.paintScheme());

            renderingContext.setPartialTick(partialTick);

            renderingContext.setOverlay(slot.resolveOverlay(renderState, overlay));
            renderingContext.setLightmap(slot.resolveLightmap(renderState, lightmap));

            renderingContext.setOutlineColor(outlineColor);

            renderingContext.setAnimationTick(animationTick);
            renderingContext.setAnimationManager(animationManager);

            renderingContext.setItemSource(slot.resolveItemSource(renderState, itemSource));

            renderingContext.saveGraphicsState();

            skin.setupAnim(renderState, resolvedArmature, renderingContext);

            renderCount += SkinRenderer.render(skin, resolvedPaintScheme, resolvedArmature, renderingContext);

            renderingContext.restoreGraphicsState();
        }

        return renderCount;
    }

    public void setPartialTick(float partialTick) {
        this.partialTick = partialTick;
    }

    public float partialTick() {
        return partialTick;
    }

    public void setAnimationTick(double animationTick) {
        this.animationTick = animationTick;
    }

    public double animationTick() {
        return animationTick;
    }

    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    public AnimationManager animationManager() {
        return animationManager;
    }

    public void setItemSource(SkinItemSource itemSource) {
        this.itemSource = itemSource;
    }

    public SkinItemSource itemSource() {
        return itemSource;
    }

    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }

    public int outlineColor() {
        return outlineColor;
    }

    public List<EntitySlot> slots() {
        return slots;
    }

    public boolean isEmpty() {
        return slots.isEmpty();
    }
}
