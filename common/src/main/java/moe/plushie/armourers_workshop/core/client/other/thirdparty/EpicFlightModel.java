package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.render.model.LinkedModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;

public class EpicFlightModel<S extends EntityRenderState> extends LinkedModel<S> {

    private static final DataContainer.Key<EpicFlightModel<?>> KEY = DataContainer.key("EpicFlightModel");

    private Object childRef;

    private boolean isValid = false;
    private BakedArmatureTransformer transformer;

    protected EpicFlightModel(IEntityModel<S> parent) {
        super(parent);
    }

    public static <S extends EntityRenderState> EpicFlightModel<S> of(IEntityModel<S> entityModel) {
        var model = DataContainer.get(entityModel, KEY);
        if (model == null) {
            model = new EpicFlightModel<>(entityModel);
            DataContainer.set(entityModel, KEY, model);
        }
        return Objects.unsafeCast(model);
    }

    public void linkTo(Object mesh) {
        if (childRef != mesh) {
            childRef = mesh;
            super.linkTo(EpicFlightModelHolder.create(mesh));
        }
    }

    public void setTransformer(BakedArmatureTransformer transformer) {
        this.transformer = transformer;
    }

    public BakedArmatureTransformer transformer() {
        return transformer;
    }

    public void setInvalid(boolean valid) {
        isValid = valid;
    }

    public boolean isInvalid() {
        return isValid;
    }
}
