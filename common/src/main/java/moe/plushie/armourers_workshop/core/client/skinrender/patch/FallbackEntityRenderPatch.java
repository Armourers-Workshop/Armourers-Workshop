package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.auto;

public class FallbackEntityRenderPatch extends EntityRenderPatch<Entity> {

    private static final float SCALE = 1 / 16f;

    private final BakedArmature armature;
    private final BakedArmatureTransformer transformer;

    public FallbackEntityRenderPatch(BakedArmatureTransformer transformer, SkinRenderData renderData) {
        super(renderData);
        this.transformer = transformer;
        this.armature = BakedArmature.mutableBy(transformer.getArmature());
    }

    public static void activate(Entity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        auto renderPatch = renderData.getRenderPatch();
        if (renderPatch == null) {
            auto transformer = SkinRendererManager.getFallbackTransformer(entity.getType());
            if (transformer == null) {
                return;
            }
            auto renderPatch1 = new FallbackEntityRenderPatch(transformer, renderData);
            renderPatch = ObjectUtils.unsafeCast(renderPatch1);
            renderData.setRenderPatch(renderPatch);
        }

        renderPatch.onInit(entity, partialTicks, packedLight, poseStackIn, buffersIn);
        renderPatch.onActivate(entity);
    }

    public static void render(Entity entity) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            auto renderPatch = renderData.getRenderPatch();
            if (renderPatch != null) {
                renderPatch.onRender(entity);
            }
        }
    }

    public static void deactivate(Entity entity) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            auto renderPatch = renderData.getRenderPatch();
            if (renderPatch != null) {
                renderPatch.onDeactivate(entity);
            }
        }
    }

    @Override
    protected void onActivate(Entity entity) {
        transformer.prepare(entity, this);
    }

    @Override
    protected void onDeactivate(Entity entity) {
        transformer.deactivate(entity, this);
    }

    @Override
    protected void onRender(Entity entity) {
        poseStack.pushPose();

        transformer.activate(entity, this);
        transformer.applyTo(armature);

        poseStack.scale(-SCALE, -SCALE, SCALE);

        ClientWardrobeHandler.render(entity, armature, this, renderData::getItemSkins);

        poseStack.popPose();
    }
}
