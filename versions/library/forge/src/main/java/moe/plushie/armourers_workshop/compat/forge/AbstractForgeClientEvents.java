package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeClientPlayerEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeItemTooltipEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRegisterScreensEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRegisterTextureEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRenderFrameEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRenderHighlightEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRenderScreenEvent;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderScreenEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;

public class AbstractForgeClientEvents {

    public static void init() {
        EventManager.post(ClientPlayerEvent.LoggingIn.class, AbstractForgeClientPlayerEvent.loggingInFactory());
        EventManager.post(ClientPlayerEvent.LoggingOut.class, AbstractForgeClientPlayerEvent.loggingOutFactory());

        EventManager.post(ClientPlayerEvent.Clone.class, AbstractForgeClientPlayerEvent.cloneFactory());

        EventManager.post(RenderFrameEvent.Pre.class, AbstractForgeRenderFrameEvent.preFactory());
        EventManager.post(RenderFrameEvent.Post.class, AbstractForgeRenderFrameEvent.postFactory());

        EventManager.post(RenderScreenEvent.Pre.class, AbstractForgeRenderScreenEvent.preFactory());
        EventManager.post(RenderScreenEvent.Post.class, AbstractForgeRenderScreenEvent.postFactory());

        EventManager.post(ItemTooltipEvent.Gather.class, AbstractForgeItemTooltipEvent.gatherFactory());
        EventManager.post(ItemTooltipEvent.Render.class, AbstractForgeItemTooltipEvent.renderFactory());

        EventManager.post(RenderHighlightEvent.Block.class, AbstractForgeRenderHighlightEvent.blockFactory());

        EventManager.post(RenderSpecificHandEvent.class, AbstractForgeRenderSpecificHandEvent.armFactory());

        EventManager.post(RegisterTextureEvent.class, AbstractForgeRegisterTextureEvent.registryFactory());
        EventManager.post(RegisterItemPropertyEvent.class, AbstractForgeRegisterItemPropertyEvent.propertyFactory());

        EventManager.post(RegisterScreensEvent.class, AbstractForgeRegisterScreensEvent.registryFactory());

        EventManager.post(RegisterClientDataPackEvent.class, AbstractForgeRegisterClientDataPackEvent.registryFactory());
    }
}
