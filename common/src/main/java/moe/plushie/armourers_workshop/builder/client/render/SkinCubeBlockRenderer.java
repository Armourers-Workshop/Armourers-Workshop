package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.SkinCubeBlockEntity;
import moe.plushie.armourers_workshop.builder.client.render.state.SkinCubeRenderState;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

@OnlyIn(Dist.CLIENT)
public class SkinCubeBlockRenderer<T extends BlockEntity & IBlockPaintable, S extends SkinCubeRenderState> extends AbstractBlockEntityRenderer<T, S> {

    private static float HOLDING_TOOL_ALPHA;
    private static long HOLDING_TOOL_LAST_TIME;

    private final OpenRectangle3f shape = new OpenRectangle3f(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f);

    public SkinCubeBlockRenderer(Context context) {
        super(context);
    }

    @Override
    protected boolean abi$shouldRender(T entity) {
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

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        // update the alpha time.
        updateHoldingAlpha();
        var alpha = HOLDING_TOOL_ALPHA;
        if (alpha <= 0.0f) {
            return;
        }
        context.saveGraphicsState();

        context.translateCTM(0.5f, 0.5f, 0.5f);
        context.scaleCTM(-1, -1, 1);

        context.draw(ShapeElement.marker(shape, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, renderState.colors(), alpha));

        context.restoreGraphicsState();
    }

    private static void updateHoldingAlpha() {
        var level = Minecraft.getInstance().level;
        if (level == null || HOLDING_TOOL_LAST_TIME == level.getGameTime()) {
            return;
        }
        var alpha = HOLDING_TOOL_ALPHA;
        if (isHoldingPaintingTool()) {
            alpha += 0.25f;
        } else {
            alpha -= 0.25f;
        }
        HOLDING_TOOL_ALPHA = OpenMath.clamp(alpha, 0.0f, 1.0f);
        HOLDING_TOOL_LAST_TIME = level.getGameTime();
    }


    private static boolean isHoldingPaintingTool() {
        var player = Minecraft.getInstance().player;
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
}
