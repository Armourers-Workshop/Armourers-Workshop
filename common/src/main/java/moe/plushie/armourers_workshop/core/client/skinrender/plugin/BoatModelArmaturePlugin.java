package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModelProvider;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.model.LinkedModel;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;

public class BoatModelArmaturePlugin extends ArmaturePlugin {

    private IModelProvider<Entity> modelProvider;
    private final LinkedModel placeholderModel = new LinkedModel(null);

    public BoatModelArmaturePlugin(ArmatureTransformerContext context) {
        context.setEntityModel(placeholderModel);
        context.addEntityRendererListener(entityRenderer -> {
            // force cast to model provider.
            if (entityRenderer instanceof IModelProvider<?>) {
                modelProvider = ObjectUtils.unsafeCast(entityRenderer);
            }
        });
    }

    @Override
    public void activate(Entity entity, Context context) {
        // link to placeholder model.
        placeholderModel.linkTo(modelProvider.getModel(entity));

        // fix the direction.
        context.getPoseStack().scale(-1, -1, 1);
    }

    @Override
    public boolean freeze() {
        return modelProvider != null;
    }
}
