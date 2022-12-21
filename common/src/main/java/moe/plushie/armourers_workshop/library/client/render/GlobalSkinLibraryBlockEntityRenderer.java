package moe.plushie.armourers_workshop.library.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.sagesse.minecraft.client.renderer.BlockEntityRenderer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.utils.ModelPartBuilder;
import moe.plushie.armourers_workshop.utils.math.Quaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Environment(value = EnvType.CLIENT)
public class GlobalSkinLibraryBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {

    private final ModelPart model = ModelPartBuilder.of(64, 32).cube(-8, -8, -8, 16, 16, 16).build();

    public GlobalSkinLibraryBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
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
            poseStack.rotate(new Quaternionf(angle * 4, angle, angle * 2, true));
        }

        VertexConsumer builder = buffers.getBuffer(SkinRenderType.IMAGE_EARTH);
        model.render(poseStack.cast(), builder, light, overlay, 1.0f, 1.0f, 1.0f, 0.5f);

        poseStack.popPose();
    }
}
