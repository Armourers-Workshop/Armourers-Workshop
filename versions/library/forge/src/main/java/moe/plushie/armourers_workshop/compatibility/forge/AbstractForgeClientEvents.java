package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Available("[1.21, )")
public class AbstractForgeClientEvents {

    public static final Class<TickEvent.RenderTickEvent> TICK = TickEvent.RenderTickEvent.class;

    public static final TickEvent.Phase TICK_PHASE_START = TickEvent.Phase.START;
    public static final TickEvent.Phase TICK_PHASE_END = TickEvent.Phase.END;


    public static final Class<ClientPlayerNetworkEvent.LoggingIn> PLAYER_LOGIN = ClientPlayerNetworkEvent.LoggingIn.class;
    public static final Class<ClientPlayerNetworkEvent.LoggingOut> PLAYER_LOGOUT = ClientPlayerNetworkEvent.LoggingOut.class;

    public static final Class<RegisterColorHandlersEvent.Item> ITEM_COLOR_REGISTRY = RegisterColorHandlersEvent.Item.class;
    public static final Class<RegisterColorHandlersEvent.Block> BLOCK_COLOR_REGISTRY = RegisterColorHandlersEvent.Block.class;
    public static final Class<ModelEvent.RegisterAdditional> MODEL_REGISTRY = ModelEvent.RegisterAdditional.class;
    public static final Class<RegisterKeyMappingsEvent> KEY_REGISTRY = RegisterKeyMappingsEvent.class;
    public static final Class<ItemTooltipEvent> ITEM_TOOLTIP_REGISTRY = ItemTooltipEvent.class;
    public static final Class<EntityRenderersEvent.RegisterRenderers> ENTITY_RENDERER_REGISTRY = EntityRenderersEvent.RegisterRenderers.class;

    public static final Class<RenderHighlightEvent.Block> RENDER_HIGHLIGHT = RenderHighlightEvent.Block.class;
    public static final Class<RenderTooltipEvent.Pre> RENDER_TOOLTIP_PRE = RenderTooltipEvent.Pre.class;

    public static final Class<RenderArmEvent> RENDER_SPECIFIC_HAND = RenderArmEvent.class;

    public static final Class<RenderLivingEvent.Pre> RENDER_LIVING_ENTITY_PRE = RenderLivingEvent.Pre.class;
    public static final Class<RenderLivingEvent.Post> RENDER_LIVING_ENTITY_POST = RenderLivingEvent.Post.class;
}
