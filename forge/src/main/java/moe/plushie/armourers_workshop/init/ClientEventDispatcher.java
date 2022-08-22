package moe.plushie.armourers_workshop.init;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.init.platform.forge.builder.KeyBindingBuilderImpl;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.TickUtils;
import com.apple.library.coregraphics.CGRect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientEventDispatcher {

    public static void init() {
        ClientEventDispatcher dispatcher = new ClientEventDispatcher();
        FMLJavaModLoadingContext.get().getModEventBus().register(dispatcher);
        MinecraftForge.EVENT_BUS.register(dispatcher);
        MinecraftForge.EVENT_BUS.register(new Forge());
    }

    @SubscribeEvent
    public void registerItemColors(ColorHandlerEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        Registry.ITEM.getEntries().forEach(object -> {
            Item item = object.get();
            if (item instanceof IItemTintColorProvider) {
                itemColors.register(((IItemTintColorProvider) item)::getTintColor, item);
            }
        });
    }

    @SubscribeEvent
    public void registerBlockColors(ColorHandlerEvent.Block event) {
        BlockColors blockColors = event.getBlockColors();
        Registry.BLOCK.getEntries().forEach(object -> {
            Block block = object.get();
            if (block instanceof IBlockTintColorProvider) {
                blockColors.register(((IBlockTintColorProvider) block)::getTintColor, block);
            }
        });
    }

    @SubscribeEvent
    public void registerItemModels(ModelRegistryEvent event) {
        Registry.ITEM.getEntries().forEach(object -> {
            Item item = object.get();
            if (item instanceof IItemPropertiesProvider) {
                ((IItemPropertiesProvider) item).createModelProperties((key, property) -> ItemProperties.register(item, key, property::getValue));
            }
        });
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        SkinPartTypes.registeredTypes().forEach(partType -> {
            ResourceLocation rl = ArmourersWorkshop.getCustomModel(partType.getRegistryName());
            if (resourceManager.hasResource(new ResourceLocation(rl.getNamespace(), "models/item/" + rl.getPath() + ".json"))) {
                ModelLoader.addSpecialModel(rl);
            }
        });
    }

//    @SubscribeEvent
//    public void registerParticleFactories(ParticleFactoryRegisterEvent event) {
//        Minecraft.getInstance().particleEngine.register(ModParticleTypes.PAINT_SPLASH, PaintSplashParticle.Factory::new);
//    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            for (SkinSlotType slotType : SkinSlotType.values()) {
                event.addSprite(slotType.getIconSprite());
            }
        }
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        // forwarding to executor
        EnvironmentExecutor.init(EnvironmentType.CLIENT);
    }

    @SubscribeEvent
    public void onClientFinish(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> EnvironmentExecutor.load(EnvironmentType.CLIENT));
    }

    private static class Forge {

        private int mouseX = 0;
        private int mouseY = 0;
        private int screenHeight = 0;
        private int screenWidth = 0;

        private boolean isPaused;

        @SubscribeEvent
        public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
            KeyBindingBuilderImpl.tick();
        }

        @SubscribeEvent
        public void onItemTooltipEvent(ItemTooltipEvent event) {
            ItemTooltipManager.appendHoverText(event.getItemStack(), null, event.getToolTip(), event.getFlags(), null);
        }

        @SubscribeEvent
        public void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public void onRenderTooltip(RenderTooltipEvent.PostText event) {
            ItemStack itemStack = event.getStack();
            PoseStack matrixStack = event.getMatrixStack();
            CGRect frame = new CGRect(event.getX(), event.getY(), event.getWidth(), event.getHeight());
            ItemTooltipManager.renderHoverText(itemStack, frame, mouseX, mouseY, screenWidth, screenHeight, matrixStack);
        }


        @SubscribeEvent
        public void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
            if (event.getPlayer() != null && event.getPlayer().equals(Minecraft.getInstance().player)) {
                SkinBakery.start();
            }
        }

        @SubscribeEvent
        public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            Player player = event.getPlayer();
            if (player != null && player.equals(Minecraft.getInstance().player)) {
                SkinBakery.stop();
                SkinLoader.getInstance().clear();
                SkinLibraryManager.getClient().getPublicSkinLibrary().reset();
                SkinLibraryManager.getClient().getPrivateSkinLibrary().reset();
                ModContext.reset();
                ModConfigSpec.COMMON.apply(null);
            }
        }

        @SubscribeEvent
        public void onRenderTick(TickEvent.RenderTickEvent event) {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            boolean isPaused = Minecraft.getInstance().isPaused();
            if (this.isPaused != isPaused) {
                this.isPaused = isPaused;
                if (isPaused) {
                    TickUtils.pause();
                } else {
                    TickUtils.resume();
                }
            }
        }

        @SubscribeEvent
        public void onDrawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
            Player player = Minecraft.getInstance().player;
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
            ItemStack itemStack = player.getMainHandItem();
            Item item = itemStack.getItem();
            if (ModConfig.Client.enableEntityPlacementHighlight && item == ModItems.MANNEQUIN.get()) {
                HighlightPlacementRenderer.renderEntity(player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
            }
            if (ModConfig.Client.enableBlockPlacementHighlight && item == ModItems.SKIN.get()) {
                HighlightPlacementRenderer.renderBlock(itemStack, player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
            }
            if (ModConfig.Client.enablePaintToolPlacementHighlight && item == ModItems.BLENDING_TOOL.get()) {
                HighlightPlacementRenderer.renderPaintTool(itemStack, player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
            }
        }

        @SubscribeEvent
        public void onRenderLivingPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
            LivingEntity entity = event.getEntity();
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData == null) {
                return;
            }
            EntityModel<?> entityModel = event.getRenderer().getModel();
            SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity, entityModel, event.getRenderer());
            if (renderer != null) {
                SkinRenderContext context = SkinRenderContext.getInstance();
                context.setup(event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
                renderer.willRender(entity, entityModel, renderData, context);
            }
        }

        @SubscribeEvent
        public void onRenderLivingPost(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
            LivingEntity entity = event.getEntity();
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData == null) {
                return;
            }
            EntityModel<?> entityModel = event.getRenderer().getModel();
            SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity, entityModel, event.getRenderer());
            if (renderer != null) {
                SkinRenderContext context = SkinRenderContext.getInstance();
                context.setup(event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
                renderer.didRender(entity, entityModel, renderData, context);
            }
        }

        @SubscribeEvent
        public void onRenderSpecificFirstPersonHand(RenderArmEvent event) {
            if (!ModConfig.enableFirstPersonSkinRenderer()) {
                return;
            }
            int light = event.getPackedLight();
            Player player = Minecraft.getInstance().player;
            PoseStack matrixStack = event.getPoseStack();
            MultiBufferSource buffers = event.getMultiBufferSource();
            ItemTransforms.TransformType transformType = ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            if (event.getArm() == HumanoidArm.RIGHT) {
                transformType = ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
            }
            ClientWardrobeHandler.onRenderSpecificHand(player, 0, light, 0, transformType, matrixStack, buffers, () -> {
                event.setCanceled(true);
            });
        }
    }
}
