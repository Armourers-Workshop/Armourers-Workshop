package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IPlayerModel;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class SkinVisibilityTransformer<M extends IModel> {

    private final ArrayList<IModelPart> applying = new ArrayList<>();

    private final HashMap<ISkinPartType, Entry<M>> modelActions = new HashMap<>();
    private final HashMap<ISkinPartType, Entry<M>> overlayActions = new HashMap<>();

    public static <M extends IHumanoidModel> void setupHumanoidModel(SkinVisibilityTransformer<M> transformer) {

        transformer.modelToPart(SkinPartTypes.BIPPED_HEAD, M::getHeadPart);

        transformer.modelToPart(SkinPartTypes.BIPPED_CHEST, M::getBodyPart);
        transformer.modelToPart(SkinPartTypes.BIPPED_LEFT_ARM, M::getLeftArmPart);
        transformer.modelToPart(SkinPartTypes.BIPPED_RIGHT_ARM, M::getRightArmPart);

        transformer.modelToPart(SkinPartTypes.BIPPED_LEFT_LEG, M::getLeftLegPart);
        transformer.modelToPart(SkinPartTypes.BIPPED_RIGHT_LEG, M::getRightLegPart);

        transformer.modelToPart(SkinPartTypes.BIPPED_LEFT_FOOT, M::getLeftLegPart);
        transformer.modelToPart(SkinPartTypes.BIPPED_RIGHT_FOOT, M::getRightLegPart);

        transformer.overlayToPart(SkinPartTypes.BIPPED_HEAD, M::getHatPart);
    }

    public static <M extends IPlayerModel> void setupPlayerModel(SkinVisibilityTransformer<M> transformer) {

        transformer.overlayToPart(SkinPartTypes.BIPPED_HEAD, M::getHatPart);

        transformer.overlayToPart(SkinPartTypes.BIPPED_LEFT_ARM, M::getLeftSleevePart);
        transformer.overlayToPart(SkinPartTypes.BIPPED_RIGHT_ARM, M::getRightSleevePart);

        transformer.overlayToPart(SkinPartTypes.BIPPED_CHEST, M::getJacketPart);

        transformer.overlayToPart(SkinPartTypes.BIPPED_LEFT_LEG, M::getLeftPantsPart);
        transformer.overlayToPart(SkinPartTypes.BIPPED_RIGHT_LEG, M::getRightPantsPart);

        transformer.overlayToPart(SkinPartTypes.BIPPED_LEFT_FOOT, M::getLeftPantsPart);
        transformer.overlayToPart(SkinPartTypes.BIPPED_RIGHT_FOOT, M::getRightPantsPart);
    }

    public void modelToPart(ISkinPartType partType, String name) {
        modelToPart(partType, m -> m.getPart(name));
    }

    public void modelToPart(ISkinPartType partType, Function<M, IModelPart> applier) {
        modelActions.computeIfAbsent(partType, Entry::new).singles.add(applier);
    }

    public void modelToParts(ISkinPartType partType, Function<M, Collection<IModelPart>> applier) {
        modelActions.computeIfAbsent(partType, Entry::new).multiples.add(applier);
    }

    public void overlayToPart(ISkinPartType partType, String name) {
        overlayToPart(partType, m -> m.getPart(name));
    }

    public void overlayToPart(ISkinPartType partType, Function<M, IModelPart> applier) {
        overlayActions.computeIfAbsent(partType, Entry::new).singles.add(applier);
    }

    public void overlayToParts(ISkinPartType partType, Function<M, Collection<IModelPart>> applier) {
        overlayActions.computeIfAbsent(partType, Entry::new).multiples.add(applier);
    }

    public void willRender(M model, SkinOverriddenManager overriddenManager) {
        if (ModDebugger.modelOverride) {
            return;
        }
        // collect all parts.
        applying.clear();
        modelActions.forEach((partType, entry) -> entry.apply(model, overriddenManager::overrideModel, this::appendIfNeeded));
        overlayActions.forEach((partType, entry) -> entry.apply(model, overriddenManager::overrideOverlay, this::appendIfNeeded));
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

        private final ISkinPartType partType;
        private final ArrayList<Function<M, IModelPart>> singles = new ArrayList<>();
        private final ArrayList<Function<M, Collection<IModelPart>>> multiples = new ArrayList<>();

        public Entry(ISkinPartType partType) {
            this.partType = partType;
        }

        public void apply(M model, Predicate<ISkinPartType> condition, Consumer<IModelPart> acc) {
            if (condition.test(partType)) {
                singles.forEach(it -> acc.accept(it.apply(model)));
                multiples.forEach(it -> it.apply(model).forEach(acc));
            }
        }
    }
}
