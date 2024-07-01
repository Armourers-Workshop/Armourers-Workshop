package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;

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
        var skins = renderData.getAllSkins();
        if (skins.isEmpty()) {
            return;
        }
        var f = 1 / 16f;

        var blockState = entity.getBlockState();
        var rotations = entity.getRenderRotations(blockState);

        var renderPatch = renderData.getRenderPatch();
        var mannequinEntity = PlaceholderManager.MANNEQUIN.get();

        renderPatch.activate(entity, partialTicks, light, overlay, poseStack, bufferSource);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.rotate(rotations);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        for (var entry : skins) {
            var skin = entry.getBakedSkin();
            skin.setupAnim(mannequinEntity, renderPatch);
            var colorScheme = skin.resolve(mannequinEntity, entry.getBakedScheme());
            SkinRenderer.render(mannequinEntity, armature, skin, colorScheme, renderPatch);
            if (ModDebugger.skinnable) {
                skin.getBlockBounds().forEach((pos, rect) -> {
                    poseStack.pushPose();
                    poseStack.scale(-1, -1, 1);
                    poseStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
                    ShapeTesselator.stroke(rect, UIColor.RED, poseStack, bufferSource);
                    poseStack.popPose();
                });
            }
        }

        poseStack.popPose();

        if (ModDebugger.skinnable) {
            var pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getRenderShape(blockState), UIColor.ORANGE, poseStack, bufferSource);
            poseStack.popPose();
        }

        renderPatch.deactivate(entity, partialTicks, light, overlay, poseStack, bufferSource);
    }
}
