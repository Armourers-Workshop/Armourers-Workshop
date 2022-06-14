package moe.plushie.armourers_workshop.builder.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.tileentity.BoundingBoxTileEntity;
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
public class SkinCubeTileEntityRenderer<T extends TileEntity & IPaintable> extends TileEntityRenderer<T> {

    private static float markerAlpha = 0F;
    private static long lastWorldTimeUpdate;

    public SkinCubeTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {
        updateAlpha(entity);
        if (!(markerAlpha > 0)) {
            return;
        }
        int alpha = (int)(markerAlpha * 255);
        IVertexBuilder builder = buffers.getBuffer(SkinRenderType.MARKER_FACE);
        for (Direction direction : Direction.values()) {
            if (!entity.shouldChangeColor(direction)) {
                continue;
            }
            IPaintColor paintColor = entity.getColor(direction);
            SkinCubeFaceRenderer.renderMarker(0, 0, 0, direction, paintColor, alpha, matrixStack, builder);
        }
    }

    public static void updateAlpha(TileEntity tileEntity) {
        World level = tileEntity.getLevel();
        if (level == null || lastWorldTimeUpdate == level.getGameTime()) {
            return;
        }
        lastWorldTimeUpdate = level.getGameTime();
        if (isPlayerHoldingPaintingTool()) {
            markerAlpha += 0.25F;
            if (markerAlpha > 1F) {
                markerAlpha = 1F;
            }
        } else {
            markerAlpha -= 0.25F;
            if (markerAlpha < 0F) {
                markerAlpha = 0F;
            }
        }
    }

    private static boolean isPlayerHoldingPaintingTool() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (item instanceof IBlockPaintViewer) {
            return true;
        } else if (item == ModItems.COLOR_PICKER) {
            return true;
        } else if (item == ModItems.SOAP) {
            return true;
        }
        return false;
    }
}
