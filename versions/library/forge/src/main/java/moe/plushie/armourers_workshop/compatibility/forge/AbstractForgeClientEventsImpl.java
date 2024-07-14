package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import manifold.ext.rt.api.auto;

@Available("[1.21, )")
public class AbstractForgeClientEventsImpl {

    public static final auto PLAYER_LOGIN = AbstractForgeEventBus.create(ClientPlayerNetworkEvent.LoggingIn.class);
    public static final auto PLAYER_LOGOUT = AbstractForgeEventBus.create(ClientPlayerNetworkEvent.LoggingOut.class);

    public static final auto PLAYER_CLONE = AbstractForgeEventBus.create(ClientPlayerNetworkEvent.Clone.class);

    public static final auto ITEM_COLOR_REGISTRY = AbstractForgeEventBus.create(RegisterColorHandlersEvent.Item.class);
    public static final auto BLOCK_COLOR_REGISTRY = AbstractForgeEventBus.create(RegisterColorHandlersEvent.Block.class);
    public static final auto MODEL_REGISTRY = AbstractForgeEventBus.create(ModelEvent.RegisterAdditional.class);
    public static final auto KEY_REGISTRY = AbstractForgeEventBus.create(RegisterKeyMappingsEvent.class);
    public static final auto ITEM_TOOLTIP_GATHER = AbstractForgeEventBus.create(ItemTooltipEvent.class);
    public static final auto MENU_SCREEN_REGISTRY = AbstractForgeEventBus.create(RegisterMenuScreensEvent.class);
    public static final auto ENTITY_RENDERER_REGISTRY = AbstractForgeEventBus.create(EntityRenderersEvent.RegisterRenderers.class);

    public static final auto RENDER_HIGHLIGHT_BLOCK = AbstractForgeEventBus.create(RenderHighlightEvent.Block.class);

    public static final auto ITEM_TOOLTIP_RENDER = AbstractForgeEventBus.create(RenderTooltipEvent.Pre.class);

    public static final auto RENDER_SPECIFIC_HAND = AbstractForgeEventBus.create(RenderArmEvent.class);

    public static final auto RENDER_LIVING_ENTITY_PRE = AbstractForgeEventBus.create(RenderLivingEvent.Pre.class);
    public static final auto RENDER_LIVING_ENTITY_POST = AbstractForgeEventBus.create(RenderLivingEvent.Post.class);

    public static final auto RENDER_FRAME_PRE = AbstractForgeEventBus.create(RenderFrameEvent.Pre.class);
    public static final auto RENDER_FRAME_POST = AbstractForgeEventBus.create(RenderFrameEvent.Post.class);
}
