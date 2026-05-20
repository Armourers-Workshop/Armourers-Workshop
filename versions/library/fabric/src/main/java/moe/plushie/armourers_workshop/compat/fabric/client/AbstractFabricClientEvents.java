package moe.plushie.armourers_workshop.compat.fabric.client;

import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricClientPlayerEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricItemTooltipEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricRegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricRegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricRegisterScreensEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricRegisterTextureEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricRenderHighlightEvent;
import moe.plushie.armourers_workshop.compat.fabric.client.event.AbstractFabricRenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;

public class AbstractFabricClientEvents {

    public static void init() {
        EventManager.post(ClientPlayerEvent.LoggingIn.class, AbstractFabricClientPlayerEvent.loggingInFactory());
        EventManager.post(ClientPlayerEvent.LoggingOut.class, AbstractFabricClientPlayerEvent.loggingOutFactory());

        EventManager.post(ItemTooltipEvent.Gather.class, AbstractFabricItemTooltipEvent.gatherFactory());

        EventManager.post(RenderHighlightEvent.Block.class, AbstractFabricRenderHighlightEvent.blockFactory());

        EventManager.post(RenderSpecificHandEvent.class, AbstractFabricRenderSpecificHandEvent.armFactory());

        EventManager.post(RegisterTextureEvent.class, AbstractFabricRegisterTextureEvent.registryFactory());
        EventManager.post(RegisterItemPropertyEvent.class, AbstractFabricRegisterItemPropertyEvent.propertyFactory());

        EventManager.post(RegisterScreensEvent.class, AbstractFabricRegisterScreensEvent.registryFactory());

        EventManager.post(RegisterClientDataPackEvent.class, AbstractFabricRegisterClientDataPackEvent.registryFactory());
    }
}
