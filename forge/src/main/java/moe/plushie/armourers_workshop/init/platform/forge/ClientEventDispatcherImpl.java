package moe.plushie.armourers_workshop.init.platform.forge;

import com.apple.library.coregraphics.CGRect;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventDispatcher;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientRegistries;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.init.platform.forge.builder.KeyBindingBuilderImpl;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClientEventDispatcherImpl extends AbstractForgeClientEventDispatcher {

    public static void init() {
        ClientEventDispatcherImpl dispatcher = new ClientEventDispatcherImpl();
        FMLJavaModLoadingContext.get().getModEventBus().register(dispatcher);
        MinecraftForge.EVENT_BUS.register(dispatcher);
        MinecraftForge.EVENT_BUS.register(new Forge());
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);
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
            IItemPropertiesProvider provider = ObjectUtils.safeCast(item, IItemPropertiesProvider.class);
            if (provider != null) {
                provider.createModelProperties((key, property) -> AbstractForgeClientRegistries.registerItemProperty(item, key, property));
            }
        });
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        SkinPartTypes.registeredTypes().forEach(partType -> {
            ResourceLocation rl = ArmourersWorkshop.getCustomModel(partType.getRegistryName());
            if (resourceManager.hasResource(new ResourceLocation(rl.getNamespace(), "models/item/" + rl.getPath() + ".json"))) {
                AbstractForgeClientRegistries.registerSpecialModel(rl);
            }
        });
    }

//    @SubscribeEvent
//    public void registerParticleFactories(ParticleFactoryRegisterEvent event) {
//        Minecraft.getInstance().particleEngine.register(ModParticleTypes.PAINT_SPLASH, PaintSplashParticle.Factory::new);
//    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        AbstractForgeClientRegistries.registerStitchTexture(event, register -> {
            for (SkinSlotType slotType : SkinSlotType.values()) {
                register.accept(slotType.getIconSprite());
            }
        });
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        // forwarding to executor
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT);
    }

    @SubscribeEvent
    public void onClientFinish(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.CLIENT));
    }

    private static class Forge extends Handler {

        private boolean isPaused;

        @SubscribeEvent
        public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
            KeyBindingBuilderImpl.tick();
        }

        @SubscribeEvent
        public void onItemTooltipEvent(ItemTooltipEvent event) {
            ItemTooltipManager.appendHoverText(event.getItemStack(), null, event.getToolTip(), event.getFlags());
        }

        @Override
        public void didRenderTooltip(ItemStack itemStack, CGRect frame, int mouseX, int mouseY, int screenWidth, int screenHeight, PoseStack poseStack) {
            ItemTooltipManager.renderHoverText(itemStack, frame, mouseX, mouseY, screenWidth, screenHeight, poseStack);
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

        @Override
        public void drawBlockHighlightEvent(BlockHitResult traceResult, Camera camera, PoseStack poseStack, MultiBufferSource buffers) {
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
                HighlightPlacementRenderer.renderEntity(player, traceResult, camera, poseStack, buffers);
            }
            if (ModConfig.Client.enableBlockPlacementHighlight && item == ModItems.SKIN.get()) {
                HighlightPlacementRenderer.renderBlock(itemStack, player, traceResult, camera, poseStack, buffers);
            }
            if (ModConfig.Client.enablePaintToolPlacementHighlight && item == ModItems.BLENDING_TOOL.get()) {
                HighlightPlacementRenderer.renderPaintTool(itemStack, player, traceResult, camera, poseStack, buffers);
            }
        }

        @Override
        public void willRenderLivingEntity(LivingEntity entity, LivingEntityRenderer<?, ?> entityRenderer, Supplier<SkinRenderContext> contextSupplier) {
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData != null) {
                SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, contextSupplier);
            }
        }

        @Override
        public void didRenderLivingEntity(LivingEntity entity, LivingEntityRenderer<?, ?> entityRenderer, Supplier<SkinRenderContext> contextSupplier) {
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData != null) {
                SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, contextSupplier);
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
