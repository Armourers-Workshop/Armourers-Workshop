package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IPlayerModel;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModDebugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SkinVisibilityTransformer<M extends IModel> {

    private final ArrayList<IModelPart> applying = new ArrayList<>();

    private final HashMap<ISkinProperty<Boolean>, Entry<M>> propertyActions = new HashMap<>();

    public static <M extends IHumanoidModel> void setupHumanoidModel(SkinVisibilityTransformer<M> transformer) {

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_HEAD, M::getHeadPart);

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_CHEST, M::getBodyPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, M::getLeftArmPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, M::getRightArmPart);

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, M::getLeftLegPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, M::getRightLegPart);

        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_HAT, M::getHatPart);
    }

    public static <M extends IPlayerModel> void setupPlayerModel(SkinVisibilityTransformer<M> transformer) {

        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_HAT, M::getHatPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_HAT, M::getEarPart);

        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_CLOAK, M::getCloakPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_JACKET, M::getJacketPart);

        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE, M::getLeftSleevePart);
        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE, M::getRightSleevePart);

        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS, M::getLeftPantsPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS, M::getRightPantsPart);
    }

    public void linkToPart(ISkinProperty<Boolean> property, String name) {
        linkToPart(property, m -> m.getPart(name));
    }

    public void linkToPart(ISkinProperty<Boolean> property, Function<M, IModelPart> applier) {
        propertyActions.computeIfAbsent(property, Entry::new).singles.add(applier);
    }

    public void linkToParts(ISkinProperty<Boolean> property, Function<M, Collection<IModelPart>> applier) {
        propertyActions.computeIfAbsent(property, Entry::new).multiples.add(applier);
    }

    public void willRender(M model, SkinOverriddenManager overriddenManager) {
        if (ModDebugger.modelOverride) {
            return;
        }
        // collect all parts.
        applying.clear();
        propertyActions.forEach(((property, entry) -> {
            if (overriddenManager.contains(property)) {
                entry.singles.forEach(it -> appendIfNeeded(it.apply(model)));
                entry.multiples.forEach(it -> it.apply(model).forEach(this::appendIfNeeded));
            }
        }));
    }

    public void didRender(M model, SkinOverriddenManager overriddenManager) {
        // restore part
        applying.forEach(it -> it.setVisible(true));
        applying.clear();
    }

    private void appendIfNeeded(IModelPart modelPart) {
        if (modelPart != null && modelPart.isVisible()) {
            applying.add(modelPart);
            modelPart.setVisible(false);
        }
    }

    public static class Entry<M extends IModel> {

        private final ArrayList<Function<M, IModelPart>> singles = new ArrayList<>();
        private final ArrayList<Function<M, Collection<IModelPart>>> multiples = new ArrayList<>();

        public Entry(ISkinProperty<Boolean> property) {
        }
    }
}
