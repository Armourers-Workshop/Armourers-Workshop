package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

import manifold.ext.rt.api.auto;

public class FallbackEntityRenderPatch<T extends Entity> extends EntityRenderPatch<T> {

    private static final float SCALE = 1 / 16f;

    private final BakedArmature armature;

    public FallbackEntityRenderPatch(BakedArmatureTransformer transformer, SkinRenderData renderData) {
        super(renderData);
        this.transformer = transformer;
        this.armature = BakedArmature.mutableBy(transformer.getArmature());
    }

    public static <T extends Entity> void activate(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, Consumer<FallbackEntityRenderPatch<T>> handler) {
        _activate(FallbackEntityRenderPatch.class, entity, partialTicks, packedLight, poseStackIn, buffersIn, null, handler, renderData -> {
            auto transformer = SkinRendererManager.getFallbackTransformer(entity.getType());
            if (transformer != null) {
                return new FallbackEntityRenderPatch<>(transformer, renderData);
            }
            return null;
        });
    }

    public static <T extends Entity> void apply(T entity, Consumer<FallbackEntityRenderPatch<T>> handler) {
        _apply(FallbackEntityRenderPatch.class, entity, handler);
    }

    public static <T extends Entity> void deactivate(T entity, Consumer<FallbackEntityRenderPatch<T>> handler) {
        _deactivate(FallbackEntityRenderPatch.class, entity, handler);
    }

    @Override
    protected void onApply(Entity entity) {
        poseStack.pushPose();

        transformer.activate(entity, this);
        transformer.applyTo(armature);

        poseStack.scale(-SCALE, -SCALE, SCALE);

        ClientWardrobeHandler.render(entity, armature, this, renderData::getItemSkins);

        poseStack.popPose();
    }
}
