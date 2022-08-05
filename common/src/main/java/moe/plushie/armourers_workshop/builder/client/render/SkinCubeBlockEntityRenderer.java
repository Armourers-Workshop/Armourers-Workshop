package moe.plushie.armourers_workshop.builder.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.IExtendedBlockEntityRenderer;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(value = EnvType.CLIENT)
public class SkinCubeBlockEntityRenderer<T extends BlockEntity & IPaintable & IExtendedBlockEntityRenderer> extends BlockEntityRenderer<T> {

    private static float markerAlpha = 0F;
    private static long lastWorldTimeUpdate;

    public SkinCubeBlockEntityRenderer(BlockEntityRenderDispatcher rendererManager) {
        super(rendererManager);
    }

    public static void updateAlpha(BlockEntity tileEntity) {
        Level level = tileEntity.getLevel();
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
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (item instanceof IBlockPaintViewer) {
            return true;
        } else if (item == ModItems.COLOR_PICKER.get()) {
            return true;
        } else return item == ModItems.SOAP.get();
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, int overlay) {
        if (!entity.shouldUseExtendedRenderer()) {
            return;
        }
        updateAlpha(entity);
        if (!(markerAlpha > 0)) {
            return;
        }
        int alpha = (int) (markerAlpha * 255);
        VertexConsumer builder = buffers.getBuffer(SkinRenderType.MARKER_FACE);
        for (Direction direction : Direction.values()) {
            if (!entity.shouldChangeColor(direction)) {
                continue;
            }
            IPaintColor paintColor = entity.getColor(direction);
            ExtendedFaceRenderer.renderMarker(0, 0, 0, direction, paintColor, alpha, matrixStack, builder);
        }
    }
}
