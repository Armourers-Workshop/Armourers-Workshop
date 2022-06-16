package moe.plushie.armourers_workshop;


import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.handler.BlockHandler;
import moe.plushie.armourers_workshop.core.handler.PlayerNetworkHandler;
import moe.plushie.armourers_workshop.core.handler.WorldHandler;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.common.*;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("armourers_workshop")
public class ArmourersWorkshop {

    private final ModRegistry registry = new ModRegistry();
    private final SkinLibraryManager libraryManager = DistExecutor.safeRunForDist(() -> SkinLibraryManager::getClient, () -> SkinLibraryManager::getServer);

    public ArmourersWorkshop() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addGenericListener(Block.class, registry::registerBlocks);
        eventBus.addGenericListener(Item.class, registry::registerItems);
        eventBus.addGenericListener(EntityType.class, registry::registerEntities);
        eventBus.addGenericListener(TileEntityType.class, registry::registerTileEntities);
        eventBus.addGenericListener(ContainerType.class, registry::registerContainerTypes);
        eventBus.addGenericListener(ParticleType.class, registry::registerParticleTypes);
        eventBus.addGenericListener(SoundEvent.class, registry::registerSoundEvents);

        eventBus.addListener(registry::registerEntityAttributes);
        eventBus.addListener(this::onConfigReloaded);
        eventBus.addListener(this::onCommonSetup);

        // Register client-only events
        DistExecutor.runWhenOn(Dist.CLIENT, () -> registry::registerClientEvents);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(registry::onTextureStitch));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(this::onClientSetup));

        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerDidStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerWillStop);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStop);
        MinecraftForge.EVENT_BUS.addListener(registry::registerCommands);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerNetworkHandler());
        MinecraftForge.EVENT_BUS.register(new WorldHandler());
        MinecraftForge.EVENT_BUS.register(new BlockHandler());

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModConfigSpec.CLIENT.getRight());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigSpec.COMMON.getRight());
    }

//    @SubscribeEvent
//    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getPlayer() instanceof ClientPlayerEntity) {
//            ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
//            PlayerTextureReader reader = new PlayerTextureReader(player);
//        }
//    }

    private void onConfigReloaded(ModConfig.ModConfigEvent event) {
        ModConfigSpec.reload(event.getConfig().getSpec());
    }

    private void onCommonSetup(FMLLoadCompleteEvent event) {
        libraryManager.start();
        event.enqueueWork(registry::onCommonSetup);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(FMLClientSetupEvent event) {
        AWCore.init();
        registry.onClientSetup(event);
    }

    private void onServerStart(FMLServerAboutToStartEvent event) {
        ModLog.debug("hello");
        LocalDataService.start(event.getServer());
        SkinLoader.getInstance().setup(event.getServer());
    }

    private void onServerDidStart(FMLServerStartedEvent event) {
        ModContext.init(event.getServer());
    }

    private void onServerWillStop(FMLServerStoppingEvent event) {
        LocalDataService.stop();
        SkinLoader.getInstance().clear();
    }

    private void onServerStop(FMLServerStoppedEvent event) {
        ModLog.debug("bye");
        ModContext.reset();
    }
}