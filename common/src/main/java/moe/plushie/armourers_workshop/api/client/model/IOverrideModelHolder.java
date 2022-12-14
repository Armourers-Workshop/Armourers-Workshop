package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.math.IPoseStack;

import java.util.HashMap;

public interface IOverrideModelHolder {

    void setOverrides(HashMap<String, IPoseStack> overrides);

    HashMap<String, IPoseStack> getOverrides();
}
