package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public class VillagerModelArmaturePlugin extends ArmaturePlugin {

    private final IModelPart sourcePart;
    private final IModelPart destinationPart;
    private final TransformModel<LivingEntity, EntityRenderState> transformModel = new TransformModel<>();

    public VillagerModelArmaturePlugin(ArmatureTransformerContext context) {
        var entityModel = context.entityModel();
        transformModel.link(entityModel);
        sourcePart = entityModel.abi$getPartByName("head");
        destinationPart = transformModel.abi$getPartByName("head");
        context.setEntityModel0(transformModel);
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (!(renderState instanceof LivingEntityRenderState)) {
            return;
        }
        transformModel.setup(renderState);
        if (sourcePart != null && destinationPart != null) {
            var src = sourcePart.pose();
            var dest = destinationPart.pose();
            dest.setRotation(src.xRot(), src.yRot(), src.zRot());
        }
    }
}
