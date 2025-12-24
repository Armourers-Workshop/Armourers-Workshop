package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractTransformModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;

@OnlyIn(Dist.CLIENT)
public class TransformModel<T extends LivingEntity, S extends EntityRenderState> extends PlaceholderModel<S, IModelPart> {

    private final AbstractTransformModel<T, S> impl = new AbstractTransformModel<>();

    public void setup(S renderState) {
        impl.setup(renderState);
    }

    public void link(IEntityModel<?> entityModel) {
        var babyPart = entityModel.abi$getPartByName("baby");
        if (babyPart != null) {
            namedParts.put("baby", babyPart);
            allParts.add(babyPart);
        }
    }

    @Override
    protected IModelPart createPart(String name) {
        return impl.getPartByName(name);
    }
}
