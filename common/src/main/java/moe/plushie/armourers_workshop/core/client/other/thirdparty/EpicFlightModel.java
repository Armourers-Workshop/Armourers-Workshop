package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.utils.DataStorageKey;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.client.model.Model;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class EpicFlightModel implements IModel {

    private static final DataStorageKey<EpicFlightModel> KEY = DataStorageKey.of("EpicFlightModel", EpicFlightModel.class);

    private final IModel parent;
    private final HashMap<String, EpicFlightModelPart> namedParts = new HashMap<>();

    private Object childRef;
    private IModel child;

    private boolean isValid = false;
    private BakedArmatureTransformer transformer;

    public EpicFlightModel(IModel parent) {
        this.parent = parent;
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
        if (childRef == mesh) {
            return;
        }
        childRef = mesh;
        child = EpicFlightModelTransformer.create(mesh);
        namedParts.forEach((key, value) -> value.linkTo(child.getPart(key)));
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

    @Nullable
    @Override
    public IModelBabyPose getBabyPose() {
        return parent.getBabyPose();
    }

    @Override
    public IModelPart getPart(String name) {
        return namedParts.computeIfAbsent(name, it -> {
            IModelPart part = parent.getPart(name);
            return new EpicFlightModelPart(part);
        });
    }

    @Override
    public Collection<IModelPart> getAllParts() {
        return parent.getAllParts();
    }

    @Override
    public Class<?> getType() {
        return parent.getType();
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return parent.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key) {
        parent.setAssociatedObject(value, key);
    }
}
