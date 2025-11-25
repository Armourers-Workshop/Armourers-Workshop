package moe.plushie.armourers_workshop.init.proxy;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.builder.client.render.PaintingHighlightPlacementRenderer;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemHandler;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.bake.SkinPreloadManager;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.core.client.render.model.EmbeddedItemModelDiscovery;
import moe.plushie.armourers_workshop.core.client.render.plugin.FallbackEntityRenderPlugin;
import moe.plushie.armourers_workshop.core.client.render.plugin.LivingEntityRenderPlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.client.sound.SmartSoundManager;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinBakery;
import moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.AutoreleasePool;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Scheduler;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModBlockTintSources;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModItemRenderers;
import moe.plushie.armourers_workshop.init.ModItemTintSources;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterClientDataPackEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.init.platform.ClientResourceManager;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.init.registry.Registries;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.data.impl.MinecraftAuth;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;

import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {

    public static void init() {
        ModDebugger.init();
        ModKeyBindings.init();
        ModItemTintSources.init();
        ModBlockTintSources.init();
        ModItemRenderers.init();
        ClientWardrobeHandler.init();
        SkinRendererManager.init();
        ClientResourceManager.init();

        MinecraftAuth.init(new MinecraftAuth.UserProvider() {

            @Override
            public String id() {
                return Minecraft.getInstance().getUser().getUuid();
            }

            @Override
            public String name() {
                return Minecraft.getInstance().getUser().getName();
            }

            @Override
            public String accessToken() {
                return Minecraft.getInstance().getUser().getAccessToken();
            }
        });

        register();
    }

    private static void register() {
        EventBus.register(RegisterClientDataPackEvent.class, event -> {
            event.register(DataPackManager.byType(DataPackType.CLIENT_RESOURCES));
        });

        // register custom item property.
        EventBus.register(RegisterItemPropertyEvent.class, event -> Registries.ITEMS.forEach(it -> {
            var item = it.get();
            if (item instanceof AbstractItemHandler handler) {
                handler.appendModelProperties((key, property) -> event.register(key, item, property));
            }
            event.register(ModConstants.key("is_crossbow"), Items.CROSSBOW, (itemStack, level, entity, id) -> 1);
            event.register(ModConstants.key("is_skin"), ModItems.SKIN.get(), (itemStack, level, entity, id) -> {
                // the mannequin entity mainhand/offhand is special, it will rendering skin stack as a normal item.
                if (entity instanceof MannequinEntity mannequin) {
                    if (itemStack == mannequin.getMainHandItem() || itemStack == mannequin.getOffhandItem()) {
                        return 0;
                    }
                }
                return 1;
            });
        }));

        // register custom sprite
        EventBus.register(RegisterTextureEvent.class, event -> Stream.of(SkinSlotType.values()).forEach(slotType -> {
            event.register(slotType.icon());
        }));

        EventBus.register(ClientPlayerEvent.LoggingIn.class, event -> {
            var player = event.getPlayer();
            if (player == null || !player.equals(Minecraft.getInstance().player)) {
                return; // other players join
            }
            SkinBakery.start();
            SkinPreloadManager.start();
            SmartSoundManager.getInstance().start();
            SmartTextureManager.getInstance().start();
            PlayerSkinBakery.start();
        });
        EventBus.register(ClientPlayerEvent.LoggingOut.class, event -> {
            var player = event.getPlayer();
            if (player == null || !player.equals(Minecraft.getInstance().player)) {
                return; // other players leave
            }
            PlayerSkinBakery.stop();
            SkinPreloadManager.stop();
            SkinBakery.stop();
            TicketManager.invalidateAll();
            SmartSoundManager.getInstance().stop();
            SmartTextureManager.getInstance().stop();
            SkinLoader.getInstance().stop();
            GlobalSkinLibrary.getInstance().disconnect();
            SkinLibraryManager.getClient().publicLibrary().reset();
            SkinLibraryManager.getClient().privateLibrary().reset();
            ModContext.reset();
            ModEntityProfiles.setCustomProfiles(Collections.emptyMap());
            ModConfigSpec.COMMON.apply(null);
        });

        EventBus.register(ClientPlayerEvent.Clone.class, event -> {
            // we can use the old wardrobe data until the next wardrobe sync packet.
            SkinUtils.copySkinWardrobe(event.getOldPlayer(), event.getNewPlayer());
        });

        EventBus.register(RenderFrameEvent.Pre.class, event -> {
            Scheduler.CLIENT.begin();
            AutoreleasePool.begin();
            TickUtils.tick(event.deltaTracker().isPaused() || event.deltaTracker().isFrozen()); // respect the /tick frozen command.
            SkinPreloadManager.tick(event.deltaTracker().isPaused());
        });

        EventBus.register(RenderFrameEvent.Post.class, event -> {
            AutoreleasePool.end();
            Scheduler.CLIENT.end();
        });

        // listen the block highlight events.
        EventBus.register(RenderHighlightEvent.Block.class, event -> {
            var hitResult = event.target();
            var player = Minecraft.getInstance().player;
            if (player == null || hitResult == null) {
                return;
            }
            // hidden hit box at inside
            // if (event.getTarget().isInside()) {
            //     BlockState state = player.level.getBlockState(event.getTarget().getBlockPos());
            //     if (state.is(ModBlocks.BOUNDING_BOX)) {
            //         event.setCanceled(true);
            //         return;
            //     }
            // }
            var itemStack = player.getMainHandItem();
            if (ModConfig.Client.enableEntityPlacementHighlight && itemStack.is(ModItems.MANNEQUIN.get())) {
                HighlightPlacementRenderer.renderEntity(player, hitResult, event.camera(), event.context());
            }
            if (ModConfig.Client.enableBlockPlacementHighlight && itemStack.is(ModItems.SKIN.get())) {
                HighlightPlacementRenderer.renderBlock(itemStack, player, hitResult, event.camera(), event.context());
            }
            if (ModConfig.Client.enablePaintToolPlacementHighlight && itemStack.is(ModItems.BLENDING_TOOL.get())) {
                PaintingHighlightPlacementRenderer.renderPaintTool(itemStack, player, hitResult, event.camera(), event.context());
            }
        });

        EventBus.register(RenderEntityEvent.Setup.class, FallbackEntityRenderPlugin::prepare);
        EventBus.register(RenderEntityEvent.Pre.class, FallbackEntityRenderPlugin::activate);
        EventBus.register(RenderEntityEvent.Post.class, FallbackEntityRenderPlugin::deactivate);

        EventBus.register(RenderLivingEntityEvent.Setup.class, LivingEntityRenderPlugin::prepare);
        EventBus.register(RenderLivingEntityEvent.Pre.class, LivingEntityRenderPlugin::activate);
        EventBus.register(RenderLivingEntityEvent.Post.class, LivingEntityRenderPlugin::deactivate);

        EventBus.register(RenderEntityEvent.Pre.class, ClientWardrobeHandler::renderFallback);
        EventBus.register(RenderSpecificHandEvent.class, ClientWardrobeHandler::renderSpecificHand);

        EventBus.register(ItemTooltipEvent.Gather.class, ItemTooltipManager::gatherSkinTooltip);
        EventBus.register(ItemTooltipEvent.Render.class, ItemTooltipManager::renderSkinTooltip);

        EventBus.register(DataPackEvent.Reloading.class, EmbeddedItemModelDiscovery::reload);
    }
}
