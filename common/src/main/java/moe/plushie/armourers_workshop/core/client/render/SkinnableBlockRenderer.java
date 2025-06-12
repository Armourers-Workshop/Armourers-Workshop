package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinnableBlockRenderer<T extends SkinnableBlockEntity> extends AbstractBlockEntityRenderer<T> {

    private final BakedArmature armature = new BakedArmature(Armatures.ANY);

    public SkinnableBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        var renderData = BlockEntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        renderData.tick(entity);
        var renderingTasks = renderData.allSkins();
        if (renderingTasks.isEmpty()) {
            return;
        }
        var f = 1 / 16f;

        var blockState = entity.getBlockState();
        var rotations = entity.getRenderRotations(blockState);

        var renderPatch = renderData.renderPatch();
        var mannequinEntity = PlaceholderManager.MANNEQUIN.get();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.rotate(rotations);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        renderPatch.activate(entity, partialTicks, light, overlay, poseStack);

        var pluginContext = renderPatch.pluginContext();
        var renderingContext = renderPatch.renderingContext();

        renderingContext.setOverlay(pluginContext.overlay());
        renderingContext.setLightmap(pluginContext.lightmap());
        renderingContext.setPartialTicks(pluginContext.partialTicks());
        renderingContext.setAnimationTicks(pluginContext.animationTicks());

        renderingContext.setPoseStack(poseStack);
        renderingContext.setBufferSource(bufferSource);
        renderingContext.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

        for (var entry : renderingTasks) {
            var skin = entry.skin();
            skin.setupAnim(mannequinEntity, armature, renderingContext);
            var colorScheme = skin.resolve(mannequinEntity, entry.paintScheme());
            SkinRenderer.render(mannequinEntity, armature, skin, colorScheme, renderingContext);
            if (ModDebugger.skinnable) {
                skin.blockBounds().forEach((pos, rect) -> {
                    poseStack.pushPose();
                    poseStack.scale(-1, -1, 1);
                    poseStack.translate(pos.x() * 16f, pos.y() * 16f, pos.z() * 16f);
                    ShapeTesselator.stroke(rect, UIColor.RED, poseStack, bufferSource);
                    poseStack.popPose();
                });
            }
        }

        renderPatch.deactivate(entity);

        poseStack.popPose();

        if (ModDebugger.skinnable) {
            var pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getRenderShape(blockState), UIColor.ORANGE, poseStack, bufferSource);
            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return ModConfig.Client.renderDistanceBlockSkin;
    }

    @Override
    public boolean shouldRender(T entity) {
        // only use custom render in the parent block entity.
        return entity.isParent();
    }
}
