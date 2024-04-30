package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeClientPlayerEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeItemTooltipEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRegisterColorHandlersEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRegisterModelEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRegisterScreensEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRegisterTextureEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRenderFrameEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRenderHighlightEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRenderLivingEvent;
import moe.plushie.armourers_workshop.compatibility.forge.event.client.AbstractForgeRenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterColorHandlersEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterModelEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderSpecificHandEvent;

public class AbstractForgeClientEvents {

    public static void init() {
        EventManager.post(RegisterColorHandlersEvent.Item.class, AbstractForgeRegisterColorHandlersEvent.itemFactory());
        EventManager.post(RegisterColorHandlersEvent.Block.class, AbstractForgeRegisterColorHandlersEvent.blockFactory());

        EventManager.post(ClientPlayerEvent.LoggingIn.class, AbstractForgeClientPlayerEvent.loggingInFactory());
        EventManager.post(ClientPlayerEvent.LoggingOut.class, AbstractForgeClientPlayerEvent.loggingOutFactory());

        EventManager.post(RenderFrameEvent.Pre.class, AbstractForgeRenderFrameEvent.preFactory());
        EventManager.post(RenderFrameEvent.Post.class, AbstractForgeRenderFrameEvent.postFactory());

        EventManager.post(ItemTooltipEvent.Gather.class, AbstractForgeItemTooltipEvent.gatherFactory());
        EventManager.post(ItemTooltipEvent.Render.class, AbstractForgeItemTooltipEvent.renderFactory());

        EventManager.post(RenderHighlightEvent.Block.class, AbstractForgeRenderHighlightEvent.blockFactory());

        EventManager.post(RenderLivingEntityEvent.Pre.class, AbstractForgeRenderLivingEvent.preFactory());
        EventManager.post(RenderLivingEntityEvent.Post.class, AbstractForgeRenderLivingEvent.postFactory());

        EventManager.post(RenderSpecificHandEvent.class, AbstractForgeRenderSpecificHandEvent.armFactory());

        EventManager.post(RegisterModelEvent.class, AbstractForgeRegisterModelEvent.registryFactory());
        EventManager.post(RegisterTextureEvent.class, AbstractForgeRegisterTextureEvent.registryFactory());

        EventManager.post(RegisterItemPropertyEvent.class, AbstractForgeRegisterItemPropertyEvent.propertyFactory());

        EventManager.post(RegisterScreensEvent.class, AbstractForgeRegisterScreensEvent.registryFactory());
        EventManager.post(RegisterKeyMappingsEvent.class, AbstractForgeRegisterKeyMappingsEvent.registryFactory());
    }
}
