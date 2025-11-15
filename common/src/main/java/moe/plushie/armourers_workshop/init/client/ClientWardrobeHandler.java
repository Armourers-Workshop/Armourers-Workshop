package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.bake.BakedFirstPersonArmature;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.render.state.SkinRenderState;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import net.minecraft.world.entity.Entity;

@OnlyIn(Dist.CLIENT)
public class ClientWardrobeHandler {

    public static void init() {
    }

    private static void tick(Entity entity) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            renderData.tick(entity);
        }
        for (var passenger : entity.getPassengers()) {
            tick(passenger);
        }
    }

    public static void willTick(Entity entity) {
        ClientDynamicLightHandler.startTick(entity);
    }

    public static void didTick(Entity entity) {
        tick(entity);
        ClientDynamicLightHandler.endTick(entity);
    }

    public static void renderSpecificHand(RenderSpecificHandEvent event) {
        //
        if (!ModConfig.enableFirstPersonSkinRenderer()) {
            return;
        }
        var context = event.context();
        var renderState = event.renderState();
        if (renderState == null) {
            return;
        }
        var model = renderState.armors();
        if (model.isEmpty()) {
            return;
        }
        var interactionHand = event.hand();
        var equipmentSlot = _equipmentSlot(interactionHand);
        var overrideHand = _shouldOverrideHand(model, interactionHand);

        var armature = BakedFirstPersonArmature.defaultBy(equipmentSlot);

        context.saveGraphicsState();
        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        model.setPartialTicks(renderState.partialTicks());
        model.setAnimationTicks(renderState.animationTicks());
        model.setAnimationManager(renderState.animationManager());
        model.setOutlineColor(0); // never show outline in the hand?

        var count = model.render(renderState, armature, event.lightmap(), event.overlay(), context);
        if (count != 0 && overrideHand && !ModDebugger.handOverride) {
            event.setCancelled(true);
        }

        context.restoreGraphicsState();
    }

    public static void renderFallback(RenderEntityEvent.Pre<?, ?> event) {
        var context = event.context();
        var renderState = event.renderState();
        if (renderState == null) {
            return;
        }
        var model = renderState.hands();
        if (model.isEmpty()) {
            return;
        }
        context.saveGraphicsState();
        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        model.setPartialTicks(renderState.partialTicks());
        model.setAnimationTicks(renderState.animationTicks());
        model.setAnimationManager(renderState.animationManager());
        model.setOutlineColor(renderState.outlineColor());

        model.render(renderState, null, event.lightmap(), event.overlay(), context);

        context.restoreGraphicsState();
    }

    private static OpenEquipmentSlot _equipmentSlot(OpenInteractionHand hand) {
        if (hand == OpenInteractionHand.MAIN_HAND) {
            return OpenEquipmentSlot.MAINHAND;
        }
        return OpenEquipmentSlot.OFFHAND;
    }

    private static boolean _shouldOverrideHand(SkinRenderState skin, OpenInteractionHand hand) {
        for (var slot : skin.slots()) {
            var properties = slot.skin().properties();
            if (hand == OpenInteractionHand.MAIN_HAND) {
                if (properties.get(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM)) {
                    return true;
                }
            }
            if (hand == OpenInteractionHand.OFF_HAND) {
                if (properties.get(SkinProperty.OVERRIDE_MODEL_LEFT_ARM)) {
                    return true;
                }
            }
        }
        return false;
    }
}
