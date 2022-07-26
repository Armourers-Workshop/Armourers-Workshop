package moe.plushie.armourers_workshop.library.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Environment(value = EnvType.CLIENT)
public class GlobalSkinLibraryTitleEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {

    private final ModelPart model = new ModelPart(64, 32, 0, 0);

    public GlobalSkinLibraryTitleEntityRenderer(BlockEntityRenderDispatcher rendererManager) {
        super(rendererManager);
        this.model.addBox(-8, -8, -8, 16, 16, 16);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, int overlay) {
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.scale(-1, -1, 1);

        float f = 0.0625f;
        float xPos = 2.5f;
        float zPos = 4.6f;
        float yPos = 4.0f;
        BlockState state = entity.getBlockState();
        Direction direction = state.getValue(GlobalSkinLibraryBlock.FACING).getOpposite();
        matrixStack.translate(
                (xPos * -direction.getStepZ() + zPos * direction.getStepX()) * f,
                yPos * f,
                (xPos * -direction.getStepX() + zPos * -direction.getStepZ()) * f
        );

        matrixStack.scale(0.2f, 0.2f, 0.2f);

        if (entity.getLevel() != null) {
            float angle = (entity.getLevel().getGameTime()) % 360 + partialTicks;
            matrixStack.mulPose(TrigUtils.rotate(angle * 4, angle, angle * 2, true));
        }

        VertexConsumer builder = buffers.getBuffer(SkinRenderType.EARTH);
        model.render(matrixStack, builder, 0, 0, 1.0f, 1.0f, 1.0f, 0.5f);

        matrixStack.popPose();
    }
}
