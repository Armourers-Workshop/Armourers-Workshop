package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultOverriddenArmaturePlugin extends ArmaturePlugin {

    private final ArrayList<IModelPart> applying = new ArrayList<>();
    private final HashMap<ISkinProperty<Boolean>, Collection<? extends IModelPart>> overrides = new HashMap<>();
    private final HashMap<ISkinType, Collection<? extends IModelPart>> skinTypeToOverrides = new HashMap<>();
    private final HashMap<ISkinPartType, Collection<? extends IModelPart>> skinPartTypeToOverrides = new HashMap<>();

    public DefaultOverriddenArmaturePlugin(Map<String, Collection<String>> keys, ArmatureTransformerContext context) {
        // when entity model change.
        context.addEntityModelListener(model -> buildOverrides(model, keys));
    }

    @Override
    public void prepare(Entity entity, SkinRenderContext context) {
        SkinRenderData renderData = context.getRenderData();

        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (entity instanceof LivingEntity && renderData.isLimitLimbs()) {
            ((LivingEntity) entity).applyLimitLimbs();
        }
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        SkinRenderData renderData = context.getRenderData();
        SkinOverriddenManager overriddenManager = renderData.getOverriddenManager();

        // apply all other part by the entity.
        overriddenManager.willRender(entity);

        // apply all visible part to hidden.
        overrides.forEach((key, value) -> {
            if (overriddenManager.contains(key)) {
                hidden(value);
            }
        });

        // apply all visible part to hidden if the specified skin type exists.
        skinTypeToOverrides.forEach((key, value) -> {
            if (has(key, SkinTypes.UNKNOWN, renderData.getUsingTypes())) {
                hidden(value);
            }
        });
        // apply all visible part to hidden if the specified skin part type exists.
        skinPartTypeToOverrides.forEach((key, value) -> {
            if (has(key, SkinPartTypes.UNKNOWN, renderData.getUsingPartTypes())) {
                hidden(value);
            }
        });
    }

    @Override
    public void deactivate(Entity entity, SkinRenderContext context) {
        SkinRenderData renderData = context.getRenderData();
        SkinOverriddenManager overriddenManager = renderData.getOverriddenManager();

        overriddenManager.didRender(entity);

        applying.forEach(it -> it.setVisible(true));
        applying.clear();
    }

    public boolean isEmpty() {
        return overrides.isEmpty();
    }

    private <T> boolean has(T value, T anyValue, Collection<T> list) {
        if (value != anyValue) {
            return list.contains(value);
        }
        return !list.isEmpty();
    }

    private void hidden(Collection<? extends IModelPart> parts) {
        // ..
        if (ModDebugger.modelOverride) {
            return;
        }
        for (IModelPart part : parts) {
            if (part.isVisible()) {
                part.setVisible(false);
                applying.add(part);
            }
        }
    }

    private void buildOverrides(IModel model, Map<String, Collection<String>> keys) {
        overrides.clear();
        keys.forEach((key, names) -> {
            if (key.startsWith("hasType.")) {
                ISkinType skinType = SkinTypes.byName(key.replace("hasType.", "armourers:"));
                skinTypeToOverrides.put(skinType, buildParts(names, model));
                return;
            }
            if (key.startsWith("hasPart.")) {
                ISkinPartType skinPartType = SkinPartTypes.byName(key.replace("hasPart.", "armourers:"));
                skinPartTypeToOverrides.put(skinPartType, buildParts(names, model));
                return;
            }
            // NOTE: we assume that all default values is false.
            ISkinProperty<Boolean> property = SkinProperty.normal(key, false);
            overrides.put(property, buildParts(names, model));
        });
    }

    private Collection<? extends IModelPart> buildParts(Collection<String> names, IModel model) {
        // '*' will wildcard all parts.
        if (names.contains("*")) {
            return model.getAllParts();
        }
        // find all parts and remove duplicates.
        LinkedHashMap<String, IModelPart> parts = new LinkedHashMap<>();
        for (String name : names) {
            IModelPart part = model.getPart(name);
            if (part != null) {
                parts.put(name, part);
            }
        }
        return parts.values();
    }
}
