package moe.plushie.armourers_workshop.compat.client.renderer.graphics.element;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderable;
import moe.plushie.armourers_workshop.core.client.render.element.SpecialRenderElement;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

@Available("[1.16, 1.26)")
@OnlyIn(Dist.CLIENT)
public class AbstractEntityRendererElement extends SpecialRenderElement implements AbstractGraphicsRenderable {

    private final Entity entity;
    private final float f;
    private final float partialTick;
    private final int lightmap;
    private final int overlay;

    protected AbstractEntityRendererElement(Entity entity, float f, float partialTick, int lightmap, int overlay) {
        this.entity = entity;
        this.f = f;
        this.partialTick = partialTick;
        this.lightmap = lightmap;
        this.overlay = overlay;
    }

    public static AbstractEntityRendererElement newInstance(Entity entity, float f, float partialTick, int lightmap, int overlay) {
        return new AbstractEntityRendererElement(entity, f, partialTick, lightmap, overlay);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        var rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> {
            rendererManager.render(entity, 0.0d, 0.0d, 0.0d, f, partialTick, poseStack, bufferSource, lightmap);
        });
    }
}
