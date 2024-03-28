package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.model.LinkedModel;
import moe.plushie.armourers_workshop.utils.DataStorageKey;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.client.model.Model;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class EpicFlightModel extends LinkedModel {

    private static final DataStorageKey<EpicFlightModel> KEY = DataStorageKey.of("EpicFlightModel", EpicFlightModel.class);

    private Object childRef;

    private boolean isValid = false;
    private BakedArmatureTransformer transformer;

    public EpicFlightModel(IModel parent) {
        super(parent);
    }

    public static <V extends Model> EpicFlightModel ofNullable(V model) {
        IModel model1 = ModelHolder.ofNullable(model);
        if (model1 == null) {
            return null;
        }
        EpicFlightModel model2 = model1.getAssociatedObject(KEY);
        if (model2 != null) {
            return model2;
        }
        model2 = new EpicFlightModel(model1);
        model1.setAssociatedObject(model2, KEY);
        return model2;
    }

    public void linkTo(Object mesh) {
        if (childRef != mesh) {
            childRef = mesh;
            linkTo(EpicFlightModelTransformer.create(mesh));
        }
    }

    public void setTransformer(BakedArmatureTransformer transformer) {
        this.transformer = transformer;
    }

    public BakedArmatureTransformer getTransformer() {
        return transformer;
    }

    public void setInvalid(boolean valid) {
        isValid = valid;
    }

    public boolean isInvalid() {
        return isValid;
    }
}
