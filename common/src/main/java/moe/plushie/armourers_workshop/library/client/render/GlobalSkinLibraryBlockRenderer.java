package moe.plushie.armourers_workshop.library.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.utils.ModelPartBuilder;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class GlobalSkinLibraryBlockRenderer<T extends BlockEntity> extends AbstractBlockEntityRenderer<T> {

    private final ModelPart model = ModelPartBuilder.of(64, 32).cube(-8, -8, -8, 16, 16, 16).build();

    public GlobalSkinLibraryBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.scale(-1, -1, 1);

        float f = 0.0625f;
        float xPos = 2.5f;
        float zPos = 4.6f;
        float yPos = 4.0f;
        BlockState state = entity.getBlockState();
        Direction direction = state.getValue(GlobalSkinLibraryBlock.FACING).getOpposite();
        poseStack.translate(
                (xPos * -direction.getStepZ() + zPos * direction.getStepX()) * f,
                yPos * f,
                (xPos * -direction.getStepX() + zPos * -direction.getStepZ()) * f
        );

        poseStack.scale(0.2f, 0.2f, 0.2f);

        if (entity.getLevel() != null) {
            float angle = (entity.getLevel().getGameTime()) % 360 + partialTicks;
            poseStack.mulPose(new OpenQuaternionf(angle * 4, angle, angle * 2, true));
        }

        auto builder = buffers.getBuffer(SkinRenderType.BLOCK_EARTH);
        model.render(poseStack, builder, 0xf000f0, overlay, 1.0f, 1.0f, 1.0f, 0.5f);

        poseStack.popPose();
    }
}
