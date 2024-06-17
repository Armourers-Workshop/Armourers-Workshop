package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class SkinnableBlockRenderer<T extends SkinnableBlockEntity> extends AbstractBlockEntityRenderer<T> {

    private final BakedArmature armature = new BakedArmature(Armatures.ANY);
    private final Supplier<MannequinEntity> placeholder = PlaceholderManager.MANNEQUIN;

    public SkinnableBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        var descriptor = entity.getDescriptor();
        var skin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (skin == null) {
            return;
        }
        var f = 1 / 16f;

        var blockState = entity.getBlockState();
        var rotations = entity.getRenderRotations(blockState);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.rotate(rotations);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        var context = SkinRenderContext.alloc(null, light, partialTicks, poseStack, bufferSource);
        SkinRenderer.render(placeholder.get(), armature, skin, descriptor.getColorScheme(), context);
        context.release();

        poseStack.popPose();

        if (ModDebugger.skinnable) {
            skin.getBlockBounds().forEach((pos, rect) -> {
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.scale(f, f, f);
                poseStack.rotate(rotations);
                poseStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
                ShapeTesselator.stroke(rect, UIColor.RED, poseStack, bufferSource);
                poseStack.popPose();
            });
            var pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getRenderShape(blockState), UIColor.ORANGE, poseStack, bufferSource);
            poseStack.popPose();
        }
    }
}
