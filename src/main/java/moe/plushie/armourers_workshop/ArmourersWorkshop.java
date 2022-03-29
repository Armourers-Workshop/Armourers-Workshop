package moe.plushie.armourers_workshop;


import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeProvider;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.init.common.ModRegistry;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("armourers_workshop")
public class ArmourersWorkshop {

    private final ModRegistry registry = new ModRegistry();
    private final SkinLibraryManager libraryManager = DistExecutor.safeRunForDist(() -> SkinLibraryManager.Client::new, () -> SkinLibraryManager.Server::new);

    public ArmourersWorkshop() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addGenericListener(Block.class, registry::registerBlocks);
        eventBus.addGenericListener(Item.class, registry::registerItems);
        eventBus.addGenericListener(EntityType.class, registry::registerEntities);
        eventBus.addGenericListener(TileEntityType.class, registry::registerTileEntities);
        eventBus.addGenericListener(ContainerType.class, registry::registerContainerTypes);

        eventBus.addListener(registry::registerEntityAttributes);
        eventBus.addListener(this::onCommonSetup);

        // Register client-only events
        DistExecutor.runWhenOn(Dist.CLIENT, () -> registry::registerClientEvents);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(this::onClientSetup));

        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerWillStop);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStop);
        MinecraftForge.EVENT_BUS.addListener(registry::registerCommands);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

//    @SubscribeEvent
//    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getPlayer() instanceof ClientPlayerEntity) {
//            ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
//            PlayerTextureReader reader = new PlayerTextureReader(player);
//        }
//    }

    @SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        EntityProfile profile = EntityProfiles.getProfile(entity);
        if (profile != null) {
            event.addCapability(SkinWardrobeProvider.WARDROBE_ID, new SkinWardrobeProvider(entity, profile));
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.isCanceled()) {
            return;
        }
        if (EntityProfiles.getProfile(event.getTarget()) == null) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(event.getTarget());
        if (wardrobe != null) {
            wardrobe.broadcast((ServerPlayerEntity) event.getPlayer());
        }
    }


    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof AbstractArrowEntity && !event.getWorld().isClientSide()) {
            Entity owner = ((AbstractArrowEntity) entity).getOwner();
            ItemStack itemStack = AWCore.getSkinFromEquipment(owner, SkinSlotType.BOW, EquipmentSlotType.MAINHAND);
            if (!itemStack.isEmpty()) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
                if (wardrobe != null) {
                    wardrobe.setItem(SkinSlotType.BOW, 0, itemStack.copy());
                }
            }
        }
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            SkinWardrobe wardrobe = SkinWardrobe.of(player);
            if (wardrobe != null) {
                wardrobe.broadcast(player);
            }
        }
    }


    private void onCommonSetup(FMLLoadCompleteEvent event) {
        libraryManager.run();
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
    }

    private void onServerWillStop(FMLServerStoppingEvent event) {
        LocalDataService.stop();
    }

    private void onServerStop(FMLServerStoppedEvent event) {
        ModLog.debug("bye");
    }
}