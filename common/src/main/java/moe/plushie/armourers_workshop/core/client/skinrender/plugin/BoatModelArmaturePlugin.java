package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.model.LinkedModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;

public class BoatModelArmaturePlugin extends ArmaturePlugin {

    private IEntityModel.Provider<EntityRenderState> modelProvider;
    private final LinkedModel<EntityRenderState> placeholderModel = new LinkedModel<>(null);

    public BoatModelArmaturePlugin(ArmatureTransformerContext context) {
        context.setEntityModel(placeholderModel);
        context.addEntityRendererListener(entityRenderer -> {
            // force cast to model provider.
            if (entityRenderer instanceof IEntityModel.Provider<?> provider) {
                // noinspection unchecked
                modelProvider = (IEntityModel.Provider<EntityRenderState>) provider;
            }
        });
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        // link to placeholder model.
        placeholderModel.linkTo(modelProvider.abi$getEntityModel(renderState));

        // fix the direction.
        context.scaleCTM(-1, -1, 1);
    }

    @Override
    public boolean freeze() {
        return modelProvider != null;
    }
}
