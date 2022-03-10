package moe.plushie.armourers_workshop;


import moe.plushie.armourers_workshop.client.ClientEventHandler;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.common.ArmourersConfig;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.registry.AWRegistry;
import moe.plushie.armourers_workshop.core.render.renderer.*;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeProvider;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeStorage;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
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

    private final AWRegistry registry = new AWRegistry();

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

    private void onCommonSetup(FMLLoadCompleteEvent event) {
        EntityProfiles.init();
        ArmourersConfig.init();
        NetworkHandler.init(AWCore.resource("aw2"));

        DataSerializers.registerSerializer(AWDataSerializers.PLAYER_TEXTURE);
        CapabilityManager.INSTANCE.register(SkinWardrobe.class, new SkinWardrobeStorage(), () -> null);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(FMLClientSetupEvent event) {
        ClientEventHandler.init(MinecraftForge.EVENT_BUS);
        ClientWardrobeHandler.init();
        AWCore.init();
    }

    private void onServerStart(FMLServerAboutToStartEvent event) {
        AWLog.debug("hello");
        LocalDataService.start(event.getServer());
    }

    private void onServerWillStop(FMLServerStoppingEvent event) {
        LocalDataService.stop();
    }

    private void onServerStop(FMLServerStoppedEvent event) {
        AWLog.debug("bye");
    }
}