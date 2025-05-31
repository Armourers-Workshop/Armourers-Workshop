package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.SkinCubeBlockEntity;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public class SkinCubeBlockRenderer<T extends BlockEntity & IBlockPaintable> extends AbstractBlockEntityRenderer<T> {

    private static float markerAlpha = 0F;
    private static long lastWorldTimeUpdate;

    public SkinCubeBlockRenderer(Context context) {
        super(context);
    }

    public static void updateAlpha(BlockEntity blockEntity) {
        var level = blockEntity.getLevel();
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
        var player = EnvironmentManager.getPlayer();
        if (player == null) {
            return false;
        }
        var itemStack = player.getMainHandItem();
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
        var alpha = (int) (markerAlpha * 255);
        var builder = bufferSource.getBuffer(SkinRenderType.IMAGE_MARKER);
        for (var direction : OpenDirection.values()) {
            if (!entity.shouldChangeColor(direction) || !entity.hasColor(direction)) {
                continue;
            }
            var paintColor = entity.getColor(direction);
            ExtendedFaceRenderer.renderMarker(0, 0, 0, direction, paintColor, alpha, light, overlay, poseStack, builder);
        }
    }

    @Override
    public boolean shouldRender(T entity) {
        // only use custom render when paint color is non-normal type.
        if (entity instanceof SkinCubeBlockEntity entity1) {
            return entity1.isCustomRenderer();
        }
        // only use custom render when paint color is non-normal type.
        if (entity instanceof BoundingBoxBlockEntity entity1) {
            return entity1.isCustomRenderer();
        }
        return false;
    }
}
