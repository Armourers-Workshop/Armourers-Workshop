package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinCubeBlockRenderer<T extends BlockEntity & IPaintable> extends AbstractBlockEntityRenderer<T> {

    private static float markerAlpha = 0F;
    private static long lastWorldTimeUpdate;

    public SkinCubeBlockRenderer(Context context) {
        super(context);
    }

    public static void updateAlpha(BlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
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
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() instanceof IBlockPaintViewer) {
            return true;
        }
        if (itemStack.is(ModItems.COLOR_PICKER.get())) {
            return true;
        }
        return itemStack.is(ModItems.SOAP.get());
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        updateAlpha(entity);
        if (!(markerAlpha > 0)) {
            return;
        }
        int alpha = (int) (markerAlpha * 255);
        auto builder = bufferSource.getBuffer(SkinRenderType.IMAGE_MARKER);
        for (auto direction : Direction.values()) {
            if (!entity.shouldChangeColor(direction) || !entity.hasColor(direction)) {
                continue;
            }
            IPaintColor paintColor = entity.getColor(direction);
            ExtendedFaceRenderer.renderMarker(0, 0, 0, direction, paintColor, alpha, light, overlay, poseStack, builder);
        }
    }
}
