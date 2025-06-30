package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractModelViewStack;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ExtendedBlockRenderer {

    private static final BakedArmature armature = new BakedArmature(Armatures.ANY);
    private static final SkinRenderContext context = new SkinRenderContext();

    public static void render(Entity entity, BakedSkin skin, int overlay, int lightmap, float partialTicks, AnimationManager animationManager, IPoseStack poseStack, IBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.scale(0.0625f, 0.0625f, 0.0625f);
        poseStack.scale(-1, -1, 1);

        context.setOverlay(overlay);
        context.setLightmap(lightmap);
        context.setPartialTicks(partialTicks);
        context.setAnimationTicks(TickUtils.animationTicks());

        context.setAnimationManager(animationManager);

        context.setPoseStack(poseStack);
        context.setBufferSource(bufferSource);
        context.setModelViewStack(AbstractModelViewStack.getInstance());

        skin.setupAnim(entity, armature, context);
        SkinRenderer.render(entity, armature, skin, SkinPaintScheme.EMPTY, context);

        poseStack.popPose();
    }
}
