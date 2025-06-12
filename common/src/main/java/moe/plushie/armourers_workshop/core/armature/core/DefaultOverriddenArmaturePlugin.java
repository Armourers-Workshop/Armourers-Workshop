package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
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
    private final HashMap<SkinProperty<Boolean>, Collection<? extends IModelPart>> overrides = new HashMap<>();
    private final HashMap<SkinType, Collection<? extends IModelPart>> skinTypeToOverrides = new HashMap<>();
    private final HashMap<SkinPartType, Collection<? extends IModelPart>> skinPartTypeToOverrides = new HashMap<>();

    public DefaultOverriddenArmaturePlugin(Map<String, Collection<String>> keys, ArmatureTransformerContext context) {
        // when entity model change.
        context.addEntityModelListener(model -> buildOverrides(model, keys));
    }

    @Override
    public void prepare(Entity entity, Context context) {
        var renderData = context.renderData();

        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (entity instanceof LivingEntity livingEntity && renderData.isLimitLimbs()) {
            livingEntity.applyLimitLimbs();
        }
    }

    @Override
    public void activate(Entity entity, Context context) {
        var renderData = context.renderData();
        var overriddenManager = renderData.overriddenManager();

        // apply all other part by the entity.
        overriddenManager.willRender(entity);

        // apply all visible part to hidden.
        for (var entry : overrides.entrySet()) {
            if (overriddenManager.contains(entry.getKey())) {
                hidden(entry.getValue());
            }
        }

        // apply all visible part to hidden if the specified skin type exists.
        for (var entry : skinTypeToOverrides.entrySet()) {
            if (has(entry.getKey(), SkinTypes.UNKNOWN, renderData.usingTypes())) {
                hidden(entry.getValue());
            }
        }
        // apply all visible part to hidden if the specified skin part type exists.
        for (var entry : skinPartTypeToOverrides.entrySet()) {
            if (has(entry.getKey(), SkinPartTypes.UNKNOWN, renderData.usingPartTypes())) {
                hidden(entry.getValue());
            }
        }
    }

    @Override
    public void deactivate(Entity entity, Context context) {
        var renderData = context.renderData();
        var overriddenManager = renderData.overriddenManager();

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
        for (var part : parts) {
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
                var skinType = SkinTypes.byName(key.replace("hasType.", "armourers:"));
                skinTypeToOverrides.put(skinType, buildParts(names, model));
                return;
            }
            if (key.startsWith("hasAnyType")) {
                skinTypeToOverrides.put(SkinTypes.UNKNOWN, buildParts(names, model));
                return;
            }
            if (key.startsWith("hasPart.")) {
                var skinPartType = SkinPartTypes.byName(key.replace("hasPart.", "armourers:"));
                skinPartTypeToOverrides.put(skinPartType, buildParts(names, model));
                return;
            }
            if (key.startsWith("hasAnyPart")) {
                skinPartTypeToOverrides.put(SkinPartTypes.UNKNOWN, buildParts(names, model));
                return;
            }
            // NOTE: we assume that all default values is false.
            var property = SkinProperty.normal(key, false);
            overrides.put(property, buildParts(names, model));
        });
    }

    private Collection<? extends IModelPart> buildParts(Collection<String> names, IModel model) {
        // '*' will wildcard all parts.
        if (names.contains("*")) {
            return model.allParts();
        }
        // find all parts and remove duplicates.
        var parts = new LinkedHashMap<String, IModelPart>();
        for (var name : names) {
            var part = model.partByName(name);
            if (part != null) {
                parts.put(name, part);
            }
        }
        return parts.values();
    }
}
