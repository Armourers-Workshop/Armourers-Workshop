package moe.plushie.armourers_workshop.init.proxy;

import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.builder.client.render.PaintingHighlightPlacementRenderer;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.bake.SkinPreloadManager;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.client.sound.SmartSoundManager;
import moe.plushie.armourers_workshop.core.client.texture.EntityTextureLoader;
import moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.data.cache.AutoreleasePool;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.Scheduler;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterColorHandlersEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.event.client.RegisterTextureEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.data.impl.MinecraftAuth;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ClientProxy {

    public static void init() {
        ClientWardrobeHandler.init();
        SkinRendererManager.init();
        ModKeyBindings.init();
        ModDebugger.init();

        MinecraftAuth.init(new MinecraftAuth.UserProvider() {
            @Override
            public String getId() {
                return Minecraft.getInstance().getUser().getUuid();
            }

            @Override
            public String getName() {
                return Minecraft.getInstance().getUser().getName();
            }

            @Override
            public String getAccessToken() {
                return Minecraft.getInstance().getUser().getAccessToken();
            }
        });

        EnvironmentExecutor.willSetup(EnvironmentType.CLIENT, () -> () -> {
            var resourceManager = Minecraft.getInstance().getResourceManager();
            var packLoader = DataPackManager.byType(DataPackType.CLIENT_RESOURCES);
            ((ReloadableResourceManager) resourceManager).registerReloadListener(packLoader);
        });

        register();
    }

    private static void register() {
        // register custom item property.
        EventBus.register(RegisterItemPropertyEvent.class, event -> TypedRegistry.findEntries(Item.class).forEach(it -> {
            var item = it.get();
            if (item instanceof IItemPropertiesProvider provider) {
                provider.createModelProperties((key, property) -> event.register(key, item, property));
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

        // register item/block color handler.
        EventBus.register(RegisterColorHandlersEvent.Item.class, event -> TypedRegistry.findEntries(Item.class).forEach(it -> {
            var item = it.get();
            if (item instanceof IItemTintColorProvider provider) {
                event.register(provider, item);
            }
        }));
        EventBus.register(RegisterColorHandlersEvent.Block.class, event -> TypedRegistry.findEntries(Block.class).forEach(it -> {
            var block = it.get();
            if (block instanceof IBlockTintColorProvider provider) {
                event.register(provider, block);
            }
        }));

        // register custom sprite
        EventBus.register(RegisterTextureEvent.class, event -> Stream.of(SkinSlotType.values()).forEach(slotType -> {
            event.register(slotType.getIconSprite());
        }));

        EventBus.register(ClientPlayerEvent.LoggingIn.class, event -> {
            var player = event.getPlayer();
            if (player == null || !player.equals(EnvironmentManager.getPlayer())) {
                return; // other players join
            }
            SkinBakery.start();
            SkinPreloadManager.start();
            SmartSoundManager.getInstance().start();
            SmartTextureManager.getInstance().start();
            EntityTextureLoader.getInstance().start();
        });
        EventBus.register(ClientPlayerEvent.LoggingOut.class, event -> {
            var player = event.getPlayer();
            if (player == null || !player.equals(EnvironmentManager.getPlayer())) {
                return; // other players leave
            }
            SkinPreloadManager.stop();
            SkinBakery.stop();
            Tickets.invalidateAll();
            EntityTextureLoader.getInstance().stop();
            SmartSoundManager.getInstance().stop();
            SmartTextureManager.getInstance().stop();
            SkinLoader.getInstance().stop();
            GlobalSkinLibrary.getInstance().disconnect();
            SkinLibraryManager.getClient().getPublicSkinLibrary().reset();
            SkinLibraryManager.getClient().getPrivateSkinLibrary().reset();
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
            TickUtils.tick(event.getDeltaTracker().isPaused() || event.getDeltaTracker().isFrozen()); // respect the /tick frozen command.
            SkinPreloadManager.tick(event.getDeltaTracker().isPaused());
        });

        EventBus.register(RenderFrameEvent.Post.class, event -> {
            AutoreleasePool.end();
            Scheduler.CLIENT.end();
        });

        // listen the block highlight events.
        EventBus.register(RenderHighlightEvent.Block.class, event -> {
            var player = EnvironmentManager.getPlayer();
            if (player == null) {
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
            var poseStack = AbstractPoseStack.wrap(event.getPoseStack());
            var buffers = AbstractBufferSource.wrap(event.getMultiBufferSource());
            var itemStack = player.getMainHandItem();
            if (ModConfig.Client.enableEntityPlacementHighlight && itemStack.is(ModItems.MANNEQUIN.get())) {
                HighlightPlacementRenderer.renderEntity(player, event.getTarget(), event.getCamera(), poseStack, buffers);
            }
            if (ModConfig.Client.enableBlockPlacementHighlight && itemStack.is(ModItems.SKIN.get())) {
                HighlightPlacementRenderer.renderBlock(itemStack, player, event.getTarget(), event.getCamera(), poseStack, buffers);
            }
            if (ModConfig.Client.enablePaintToolPlacementHighlight && itemStack.is(ModItems.BLENDING_TOOL.get())) {
                PaintingHighlightPlacementRenderer.renderPaintTool(itemStack, player, event.getTarget(), event.getCamera(), poseStack, buffers);
            }
        });

        EventBus.register(RenderLivingEntityEvent.Pre.class, event -> {
            ClientWardrobeHandler.onRenderLivingEntityPre(event.getEntity(), event.getPartialTicks(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });
        EventBus.register(RenderLivingEntityEvent.Setup.class, event -> {
            ClientWardrobeHandler.onRenderLivingEntity(event.getEntity(), event.getPartialTicks(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });
        EventBus.register(RenderLivingEntityEvent.Post.class, event -> {
            ClientWardrobeHandler.onRenderLivingEntityPost(event.getEntity(), event.getPartialTicks(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });

        EventBus.register(RenderSpecificHandEvent.class, event -> {
            if (!ModConfig.enableFirstPersonSkinRenderer()) {
                return;
            }
            var itemDisplayContext = OpenItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            if (event.getHand() == InteractionHand.MAIN_HAND) {
                itemDisplayContext = OpenItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            }
            ClientWardrobeHandler.onRenderSpecificHand(event.getPlayer(), 0, event.getPackedLight(), itemDisplayContext, event.getPoseStack(), event.getMultiBufferSource(), () -> {
                event.setCancelled(true);
            });
        });

        EventBus.register(ItemTooltipEvent.Gather.class, ItemTooltipManager::gatherSkinTooltip);
        EventBus.register(ItemTooltipEvent.Render.class, ItemTooltipManager::renderSkinTooltip);
    }
}
