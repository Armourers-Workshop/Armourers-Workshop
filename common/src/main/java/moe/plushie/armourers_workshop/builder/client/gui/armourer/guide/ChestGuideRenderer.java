package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTypes;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;

public class ChestGuideRenderer extends AbstractGuideRenderer {

    protected final OpenModelPart body;
    protected final OpenModelPart jacket;
    protected final OpenModelPart leftArm;
    protected final OpenModelPart rightArm;
    protected final OpenModelPart leftSleeve;
    protected final OpenModelPart rightSleeve;
    protected final OpenModelPart leftArmSlim;
    protected final OpenModelPart rightArmSlim;
    protected final OpenModelPart leftSleeveSlim;
    protected final OpenModelPart rightSleeveSlim;

    public ChestGuideRenderer() {
        body = OpenModelPartBuilder.player().uv(16, 16).cube(-4, -12, -2, 8, 12, 4).build();
        jacket = OpenModelPartBuilder.player().uv(16, 32).cube(-4, -12, -2, 8, 12, 4, 0.25f).build();
        leftArm = OpenModelPartBuilder.player().uv(32, 48).cube(-1, -12, -2, 4, 12, 4).build();
        rightArm = OpenModelPartBuilder.player().uv(40, 16).cube(-3, -12, -2, 4, 12, 4).build();
        leftSleeve = OpenModelPartBuilder.player().uv(48, 48).cube(-1, -12, -2, 4, 12, 4, 0.25f).build();
        rightSleeve = OpenModelPartBuilder.player().uv(40, 32).cube(-3, -12, -2, 4, 12, 4, 0.25f).build();
        // config the slim model part.
        leftArmSlim = OpenModelPartBuilder.player().uv(32, 48).cube(-1, -12, -2, 3, 12, 4).build();
        rightArmSlim = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -12, -2, 3, 12, 4).build();
        leftSleeveSlim = OpenModelPartBuilder.player().uv(48, 48).cube(-1, -12, -2, 3, 12, 4, 0.25f).build();
        rightSleeveSlim = OpenModelPartBuilder.player().uv(40, 32).cube(-2, -12, -2, 3, 12, 4, 0.25f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_CHEST, this::render);
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_ARM, this::renderLeftArm);
        rendererManager.register(SkinPartTypes.BIPPED_RIGHT_ARM, this::renderRightArm);
        rendererManager.register(PlayerSkinModel.SLIM, SkinPartTypes.BIPPED_LEFT_ARM, this::renderLeftArmSlim);
        rendererManager.register(PlayerSkinModel.SLIM, SkinPartTypes.BIPPED_RIGHT_ARM, this::renderRightArmSlim);
    }

    public void render(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(body, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_JACKET)) {
            context.draw(ModelPartElement.newInstance(jacket, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT_NO_CULL));
        }
    }

    public void renderLeftArm(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(leftArm, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE)) {
            context.draw(ModelPartElement.newInstance(leftSleeve, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT_NO_CULL));
        }
    }

    public void renderRightArm(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(rightArm, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE)) {
            context.draw(ModelPartElement.newInstance(rightSleeve, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT_NO_CULL));
        }
    }

    public void renderLeftArmSlim(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(leftArmSlim, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE)) {
            context.draw(ModelPartElement.newInstance(leftSleeveSlim, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT_NO_CULL));
        }
    }

    public void renderRightArmSlim(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(rightArmSlim, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE)) {
            context.draw(ModelPartElement.newInstance(rightSleeveSlim, lightmap, overlay, SkinRenderTypes.PLAYER_CUTOUT_NO_CULL));
        }
    }
}
