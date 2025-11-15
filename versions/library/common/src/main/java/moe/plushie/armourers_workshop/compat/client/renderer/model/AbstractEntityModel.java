package moe.plushie.armourers_workshop.compat.client.renderer.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.client.model.Model;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractEntityModel {

    public static <S extends EntityRenderState> IEntityModel<S> wrap(Model model) {
        // noinspection unchecked
        return (IEntityModel<S>) model;
    }
}
