package moe.plushie.armourers_workshop.library.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.item.SkinItemStackRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TrigUtils;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.library.tileentity.GlobalSkinLibraryTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.awt.*;

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
        Direction direction = state.getValue(GlobalSkinLibraryBlock.FACING);
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
