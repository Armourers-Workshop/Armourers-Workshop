package moe.plushie.armourers_workshop.compat.fabric;

import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricClientPlayerEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricItemTooltipEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRegisterScreensEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRegisterTextureEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRenderFrameEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRenderHighlightEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRenderLivingEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRenderScreenEvent;
import moe.plushie.armourers_workshop.compat.fabric.event.client.AbstractFabricRenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderScreenEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;

public class AbstractFabricClientEvents {

    public static void init() {
        EventManager.post(ClientPlayerEvent.LoggingIn.class, AbstractFabricClientPlayerEvent.loggingInFactory());
        EventManager.post(ClientPlayerEvent.LoggingOut.class, AbstractFabricClientPlayerEvent.loggingOutFactory());

        EventManager.post(ClientPlayerEvent.Clone.class, AbstractFabricClientPlayerEvent.cloneFactory());

        EventManager.post(RenderFrameEvent.Pre.class, AbstractFabricRenderFrameEvent.preFactory());
        EventManager.post(RenderFrameEvent.Post.class, AbstractFabricRenderFrameEvent.postFactory());

        EventManager.post(RenderScreenEvent.Pre.class, AbstractFabricRenderScreenEvent.preFactory());
        EventManager.post(RenderScreenEvent.Post.class, AbstractFabricRenderScreenEvent.postFactory());

        EventManager.post(ItemTooltipEvent.Gather.class, AbstractFabricItemTooltipEvent.gatherFactory());
        EventManager.post(ItemTooltipEvent.Render.class, AbstractFabricItemTooltipEvent.renderFactory());

        EventManager.post(RenderHighlightEvent.Block.class, AbstractFabricRenderHighlightEvent.blockFactory());

        EventManager.post(RenderLivingEntityEvent.Pre.class, AbstractFabricRenderLivingEvent.preFactory());
        EventManager.post(RenderLivingEntityEvent.Setup.class, AbstractFabricRenderLivingEvent.setupFactory());
        EventManager.post(RenderLivingEntityEvent.Post.class, AbstractFabricRenderLivingEvent.postFactory());

        EventManager.post(RenderSpecificHandEvent.class, AbstractFabricRenderSpecificHandEvent.armFactory());

        EventManager.post(RegisterTextureEvent.class, AbstractFabricRegisterTextureEvent.registryFactory());
        EventManager.post(RegisterItemPropertyEvent.class, AbstractFabricRegisterItemPropertyEvent.propertyFactory());

        EventManager.post(RegisterScreensEvent.class, AbstractFabricRegisterScreensEvent.registryFactory());
        EventManager.post(RegisterKeyMappingsEvent.class, AbstractFabricRegisterKeyMappingsEvent.registryFactory());

        EventManager.post(RegisterClientDataPackEvent.class, AbstractFabricRegisterClientDataPackEvent.registryFactory());
    }
}
