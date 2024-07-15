package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class FallbackEntityRenderPatch<T extends Entity> extends EntityRenderPatch<T> {

    private static final float SCALE = 1 / 16f;

    private final BakedArmature armature;

    public FallbackEntityRenderPatch(BakedArmatureTransformer transformer, EntityRenderData renderData) {
        super(renderData);
        this.transformer = transformer;
        this.armature = BakedArmature.mutableBy(transformer.getArmature());
    }

    public static <T extends Entity> void activate(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, Consumer<FallbackEntityRenderPatch<T>> handler) {
        _activate(FallbackEntityRenderPatch.class, entity, partialTicks, packedLight, poseStackIn, null, handler, renderData -> {
            var transformer = SkinRendererManager.getFallbackTransformer(entity.getType());
            if (transformer != null) {
                return new FallbackEntityRenderPatch<>(transformer, renderData);
            }
            return null;
        });
    }

    public static <T extends Entity> void apply(T entity, PoseStack poseStackIn, MultiBufferSource buffersIn, Consumer<FallbackEntityRenderPatch<T>> handler) {
        _apply(FallbackEntityRenderPatch.class, entity, poseStackIn, buffersIn, handler);
    }

    public static <T extends Entity> void deactivate(T entity, Consumer<FallbackEntityRenderPatch<T>> handler) {
        _deactivate(FallbackEntityRenderPatch.class, entity, handler);
    }

    @Override
    protected void onApply(Entity entity, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var poseStack = pluginContext.getPoseStack();
        var renderData = pluginContext.getRenderData();

        poseStack.pushPose();

        transformer.activate(entity, pluginContext);
        transformer.applyTo(armature);

        poseStack.scale(-SCALE, -SCALE, SCALE);

        renderingContext.setOverlay(pluginContext.getOverlay());
        renderingContext.setLightmap(pluginContext.getLightmap());
        renderingContext.setPartialTicks(pluginContext.getPartialTicks());
        renderingContext.setAnimationTicks(pluginContext.getAnimationTicks());

        renderingContext.setPoseStack(pluginContext.getPoseStack());
        renderingContext.setBufferSource(AbstractBufferSource.wrap(bufferSourceIn));
        renderingContext.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

        ClientWardrobeHandler.render(entity, armature, renderingContext, renderData.getItemSkins());

        poseStack.popPose();
    }
}
