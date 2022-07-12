package moe.plushie.armourers_workshop.library.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.library.tileentity.GlobalSkinLibraryTileEntity;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class GlobalSkinLibraryTileEntityRenderer<T extends GlobalSkinLibraryTileEntity> extends TileEntityRenderer<T> {

    private final ModelRenderer model = new ModelRenderer(64, 32, 0, 0);

    public GlobalSkinLibraryTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
        this.model.addBox(-8, -8, -8, 16, 16, 16);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {
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

        IVertexBuilder builder = buffers.getBuffer(SkinRenderType.EARTH);
        model.render(matrixStack, builder, 0, 0, 1.0f, 1.0f, 1.0f, 0.5f);

        matrixStack.popPose();
    }
}
