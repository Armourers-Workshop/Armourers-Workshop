package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.render.plugin.EpicFightEntityRenderPlugin;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultOverriddenArmaturePlugin extends ArmaturePlugin {

    private final HashMap<SkinProperty<Boolean>, Collection<? extends IModelPart>> overrides = new HashMap<>();
    private final HashMap<SkinType, Collection<? extends IModelPart>> skinTypeToOverrides = new HashMap<>();
    private final HashMap<SkinPartType, Collection<? extends IModelPart>> skinPartTypeToOverrides = new HashMap<>();

    public DefaultOverriddenArmaturePlugin(Map<String, Collection<String>> keys, ArmatureTransformerContext context) {
        // when entity model change.
        context.addEntityModelListener(entityModel -> buildOverrides(entityModel, keys));
    }

    @Override
    public void prepare(EntityRenderState renderState, Entity entity, float partialTick) {
        var renderData = EntityRenderData.of(entity);
        var overriddenManager = renderData.overriddenManager();

        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        renderState.setLimitLimbs(isLimitLimbs(renderState, renderData));

        // apply all other part by the entity.
        for (var equipmentSlot : OpenEquipmentSlot.values()) {
            var shouldRender = overriddenManager.shouldOverrideEquipment(equipmentSlot);
            renderState.setRenderEquipmentSlot(equipmentSlot, !shouldRender);
        }
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (ModDebugger.modelOverride) {
            return;
        }
        var visibilityState = renderState.visibilityState();

        // apply all visible part to hidden.
        for (var entry : overrides.entrySet()) {
            if (visibilityState.overriddenManager().contains(entry.getKey())) {
                visibilityState.link(entry.getValue());
            }
        }

        // apply all visible part to hidden if the specified skin type exists.
        for (var entry : skinTypeToOverrides.entrySet()) {
            if (contains(visibilityState.usingTypes(), entry.getKey(), SkinTypes.UNKNOWN)) {
                visibilityState.link(entry.getValue());
            }
        }

        // apply all visible part to hidden if the specified skin part type exists.
        for (var entry : skinPartTypeToOverrides.entrySet()) {
            if (contains(visibilityState.usingPartTypes(), entry.getKey(), SkinPartTypes.UNKNOWN)) {
                visibilityState.link(entry.getValue());
            }
        }

        visibilityState.setVisible(false);
    }

    @Override
    public void deactivate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (ModDebugger.modelOverride) {
            return;
        }
        var visibilityState = renderState.visibilityState();
        visibilityState.setVisible(true);
    }

    public boolean isEmpty() {
        return overrides.isEmpty();
    }

    private <T> boolean contains(Collection<T> list, T value, T anyValue) {
        if (value != anyValue) {
            return list.contains(value);
        }
        return !list.isEmpty();
    }

    private boolean isLimitLimbs(EntityRenderState renderState, EntityRenderData renderData) {
        // in EF doesn't need to limit limbs.
        if (renderState.renderPlugin() instanceof EpicFightEntityRenderPlugin<?, ?>) {
            return false;
        }
        // user disable is the options.
        if (!ModConfig.Client.enableSkinLimitLimbs) {
            return false;
        }
        return renderData.isLimitLimbs();
    }

    private void buildOverrides(IEntityModel<?> entityModel, Map<String, Collection<String>> keys) {
        overrides.clear();
        keys.forEach((key, names) -> {
            if (key.startsWith("hasType.")) {
                var skinType = SkinTypes.byName(key.replace("hasType.", "armourers:"));
                skinTypeToOverrides.put(skinType, buildParts(names, entityModel));
                return;
            }
            if (key.startsWith("hasAnyType")) {
                skinTypeToOverrides.put(SkinTypes.UNKNOWN, buildParts(names, entityModel));
                return;
            }
            if (key.startsWith("hasPart.")) {
                var skinPartType = SkinPartTypes.byName(key.replace("hasPart.", "armourers:"));
                skinPartTypeToOverrides.put(skinPartType, buildParts(names, entityModel));
                return;
            }
            if (key.startsWith("hasAnyPart")) {
                skinPartTypeToOverrides.put(SkinPartTypes.UNKNOWN, buildParts(names, entityModel));
                return;
            }
            // NOTE: we assume that all default values is false.
            var property = SkinProperty.normal(key, false);
            overrides.put(property, buildParts(names, entityModel));
        });
    }

    private Collection<? extends IModelPart> buildParts(Collection<String> names, IEntityModel<?> entityModel) {
        // '*' will wildcard all parts.
        if (names.contains("*")) {
            return entityModel.abi$allParts();
        }
        // find all parts and remove duplicates.
        var parts = new LinkedHashMap<String, IModelPart>();
        for (var name : names) {
            var part = entityModel.abi$getPartByName(name);
            if (part != null) {
                parts.put(name, part);
            }
        }
        return parts.values();
    }
}
