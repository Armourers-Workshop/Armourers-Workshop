package moe.plushie.armourers_workshop.library.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;
import moe.plushie.armourers_workshop.library.blockentity.GlobalSkinLibraryBlockEntity;

@OnlyIn(Dist.CLIENT)
public class GlobalSkinLibraryBlockRenderer<T extends GlobalSkinLibraryBlockEntity, S extends GlobalSkinLibraryRenderState> extends AbstractBlockEntityRenderer<T, S> {

    private final OpenModelPart model = OpenModelPartBuilder.of(64, 32).cube(-8, -8, -8, 16, 16, 16).build();

    public GlobalSkinLibraryBlockRenderer(Context context) {
        super(context);
    }

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        context.saveGraphicsState();
        context.translateCTM(0.5f, 1.75f, 0.5f);
        context.scaleCTM(-1, -1, 1);

        var f = 0.0625f;
        var xPos = 2.5f;
        var zPos = 4.6f;
        var yPos = 4.0f;

        var direction = renderState.facing().getOpposite();
        context.translateCTM(
                (xPos * -direction.getStepZ() + zPos * direction.getStepX()) * f,
                yPos * f,
                (xPos * -direction.getStepX() + zPos * -direction.getStepZ()) * f
        );

        context.scaleCTM(0.2f, 0.2f, 0.2f);

        var angle = renderState.gameTime() % 360 + renderState.partialTicks();
        context.rotateCTM(new OpenQuaternionf(angle * 4, angle, angle * 2, true));

        context.draw(ModelPartElement.newInstance(model, lightmap, overlay, 0x7fffffff, SkinRenderType.BLOCK_EARTH));

        context.restoreGraphicsState();
    }
}
