package moe.plushie.armourers_workshop.builder.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.builder.tileentity.SkinCubeTileEntity;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.other.SkinCubeFaceRenderer;
import moe.plushie.armourers_workshop.init.common.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class ArmourerTileEntityRenderer<T extends ArmourerTileEntity> extends TileEntityRenderer<T> {

    public ArmourerTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {

    }
}
