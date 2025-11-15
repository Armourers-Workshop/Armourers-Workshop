package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.render.model.LinkedModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;

public class EpicFlightModel extends LinkedModel<EntityRenderState> {

    private static final DataContainer.Key<EpicFlightModel> KEY = DataContainer.key("EpicFlightModel", EpicFlightModel::new);

    private Object childRef;

    private boolean isValid = false;
    private BakedArmatureTransformer transformer;

    public EpicFlightModel(IEntityModel<?> parent) {
        super(Objects.unsafeCast(parent));
    }

    public static EpicFlightModel of(IEntityModel<?> entityModel) {
        return DataContainer.of(entityModel, KEY);
    }

    public void linkTo(Object mesh) {
        if (childRef != mesh) {
            childRef = mesh;
            linkTo(EpicFlightModelHolder.create(mesh));
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
