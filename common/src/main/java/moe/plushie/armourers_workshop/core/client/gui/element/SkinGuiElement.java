package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public abstract class SkinGuiElement implements CGGraphicsElement, IGraphicsElement {

    protected final SkinRenderState slots = new SkinRenderState();

    public static SkinGuiElement blit(BakedSkin skin, SkinPaintScheme scheme, SkinItemSource itemSource) {
        return new Normal(skin, scheme, itemSource);
    }

    public static SkinGuiElement blit(BakedSkin skin, float x, float y, float z, float width, float height, float rx, float ry, float rz) {
        return blit(skin, SkinPaintScheme.EMPTY, ItemStack.EMPTY, x, y, z, width, height, rx, ry, rz);
    }

    public static SkinGuiElement blit(BakedSkin skin, SkinPaintScheme scheme, float x, float y, float z, float width, float height, float rx, float ry, float rz) {
        return blit(skin, scheme, ItemStack.EMPTY, x, y, z, width, height, rx, ry, rz);
    }

    public static SkinGuiElement blit(BakedSkin skin, SkinPaintScheme scheme, ItemStack itemStack, float x, float y, float z, float width, float height, float rx, float ry, float rz) {
        return new BlitInBox(skin, scheme, itemStack, x, y, z, width, height, rx, ry, rz);
    }

    @Override
    public void prepare(IGraphicsContext context) {
        // submit skin render elements immediately.
        submit(MannequinRenderState.getPlaceholder(), LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, context);
    }

    @Override
    public void render(IPoseStack poseStack, IBufferSource bufferSource) {
        // nop
    }

    protected void prepare(BakedSkin skin, SkinPaintScheme scheme, ItemStack itemStack) {
        if (skin == null) {
            return;
        }
        var slot = new EntitySlot(skin, scheme, itemStack, SkinDescriptor.EMPTY);
        slots.prepare(Collections.newList(slot));
    }

    protected void submit(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        // ignore when the skin is empty.
        var model = slots;
        if (model.isEmpty()) {
            return;
        }
        context.saveGraphicsState();

        model.setPartialTicks(1.0f);
        model.setAnimationTicks(0.0d); // never show animations in the gui.
        model.setAnimationManager(AnimationManager.NONE);
        model.setItemSource(resolveItemSource(SkinItemSource.EMPTY));
        model.setOutlineColor(0); // never show outlines in the gui.

        apply(renderState, model, context);

        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        model.render(renderState, null, lightmap, overlay, context);

        context.restoreGraphicsState();
    }

    protected abstract void apply(EntityRenderState renderState, SkinRenderState model, IGraphicsContext context);

    protected abstract SkinItemSource resolveItemSource(SkinItemSource itemSource);

    protected static class BlitInBox extends SkinGuiElement {

        private final float x;
        private final float y;
        private final float z;
        private final float width;
        private final float height;
        private final float rx;
        private final float ry;
        private final float rz;

        private final SkinItemSource itemSource;

        protected BlitInBox(BakedSkin skin, SkinPaintScheme scheme, ItemStack itemStack, float x, float y, float z, float width, float height, float rx, float ry, float rz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.itemSource = SkinItemSource.create(itemStack);
            this.prepare(skin, scheme, itemStack);
        }

        @Override
        protected void apply(EntityRenderState renderState, SkinRenderState model, IGraphicsContext context) {
            var t = TickUtils.animationTicks();
            var si = Math.min(width, height);
            context.translateCTM(x + width / 2f, y + height / 2f, z);
            context.scaleCTM(1, -1, 1);
            context.rotateCTM(OpenVector3f.XP.rotationDegrees(rx));
            context.rotateCTM(OpenVector3f.YP.rotationDegrees(ry + (float) ((t * 100) % 360)));
            context.scaleCTM(0.625f, 0.625f, 0.625f);
            context.scaleCTM(si, si, si);
        }

        @Override
        protected SkinItemSource resolveItemSource(SkinItemSource oldItemSource) {
            var newItemSource = itemSource.copy();
            newItemSource.setDisplayBox(OpenRectangle3f.ONE);
            newItemSource.setItemModelResolver(null);
            return newItemSource;
        }
    }

    protected static class Normal extends SkinGuiElement {

        private final SkinItemSource itemSource;

        protected Normal(BakedSkin skin, SkinPaintScheme scheme, SkinItemSource itemSource) {
            this.itemSource = itemSource;
            this.prepare(skin, scheme, ItemStack.EMPTY);
        }

        @Override
        protected void apply(EntityRenderState renderState, SkinRenderState model, IGraphicsContext context) {
            // nop
        }

        @Override
        protected SkinItemSource resolveItemSource(SkinItemSource oldItemSource) {
            var newItemSource = itemSource.copy();
            newItemSource.setDisplayBox(OpenRectangle3f.ONE);
            newItemSource.setItemModelResolver(null);
            return newItemSource;
        }
    }
}
