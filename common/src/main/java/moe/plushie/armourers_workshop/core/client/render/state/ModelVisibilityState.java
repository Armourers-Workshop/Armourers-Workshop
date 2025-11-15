package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

import java.util.ArrayList;
import java.util.Collection;

public class ModelVisibilityState {

    private Collection<SkinType> usingTypes;
    private Collection<SkinPartType> usingPartTypes;

    private SkinOverriddenManager overriddenManager;

    private final ArrayList<IModelPart> applying = new ArrayList<>();

    public void prepare(EntityRenderData renderData) {
        usingTypes = renderData.usingTypes();
        usingPartTypes = renderData.usingPartTypes();
        overriddenManager = renderData.overriddenManager();
        applying.clear();
    }

    public void link(Collection<? extends IModelPart> parts) {
        for (var part : parts) {
            if (part.isVisible()) {
                applying.add(part);
            }
        }
    }

    public void setVisible(boolean visible) {
        for (var part : applying) {
            part.setVisible(visible);
        }
    }

    public SkinOverriddenManager overriddenManager() {
        return overriddenManager;
    }

    public Collection<SkinType> usingTypes() {
        return usingTypes;
    }

    public Collection<SkinPartType> usingPartTypes() {
        return usingPartTypes;
    }
}

