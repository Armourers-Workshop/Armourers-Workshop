package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.core.gui.misc.DyeTableScreen;
import moe.plushie.armourers_workshop.init.client.ClientEventHandler;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.builder.block.ColourMixerBlock;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.builder.gui.colourmixer.ColourMixerScreen;
import moe.plushie.armourers_workshop.core.gui.hologramprojector.HologramProjectorScreen;
import moe.plushie.armourers_workshop.core.gui.misc.SkinnableScreen;
import moe.plushie.armourers_workshop.core.gui.misc.SkinningTableScreen;
import moe.plushie.armourers_workshop.core.gui.wardrobe.SkinWardrobeScreen;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.item.LinkingToolItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.render.entity.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.render.entity.SeatEntityRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.render.tileentities.HologramProjectorTileEntityRenderer;
import moe.plushie.armourers_workshop.core.render.tileentities.SkinnableTileEntityRenderer;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.KeyBindings;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.command.ModCommands;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.SkinLibraryScreen;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModRegistry {


    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.forEach(event.getRegistry()::register);
    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        ModItems.forEach(event.getRegistry()::register);
    }

    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        ModEntities.forEach(event.getRegistry()::register);
    }

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANNEQUIN, MannequinEntity.createLivingAttributes().build());
        event.put(ModEntities.SEAT, MannequinEntity.createLivingAttributes().build());
    }

    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTileEntities.forEach(event.getRegistry()::register);
    }

    public void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        ModContainerTypes.forEach(event.getRegistry()::register);
    }


    public void registerArgumentTypes() {
        ArgumentTypes.register("armourers_workshop:items", ListArgument.class, new ListArgument.Serializer());
        ArgumentTypes.register("armourers_workshop:files", FileArgument.class, new FileArgument.Serializer());
    }

    public void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(ModCommands.commands());
    }

    @OnlyIn(Dist.CLIENT)
    public void registerItemModels(ModelRegistryEvent event) {
        ItemModelsProperties.register(ModItems.BOTTLE, AWCore.resource("empty"), BottleItem::isEmpty);
        ItemModelsProperties.register(ModItems.SKIN, AWCore.resource("loading"), SkinItem::getIconIndex);
        ItemModelsProperties.register(ModItems.LINKING_TOOL, AWCore.resource("empty"), LinkingToolItem::isEmpty);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register(ColoredItem.getColorProvider(0), ModItems.BOTTLE);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().register(ColourMixerBlock.getColorProvider(1), ModBlocks.COLOUR_MIXER);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerClientEvents() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//
//        modEventBus.addListener(this::registerParticleFactories);
//        modEventBus.addListener(this::registerTextures);
        modEventBus.addListener(this::registerItemColors);
        modEventBus.addListener(this::registerItemModels);
        modEventBus.addListener(this::registerBlockColors);
//        modEventBus.addListener(this::handleModelBake);

        ClientEventHandler.init(MinecraftForge.EVENT_BUS);
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientSetup(FMLClientSetupEvent event) {
        ScreenManager.register(ModContainerTypes.WARDROBE, SkinWardrobeScreen::new);
        ScreenManager.register(ModContainerTypes.SKINNABLE, SkinnableScreen::new);
        ScreenManager.register(ModContainerTypes.DYE_TABLE, DyeTableScreen::new);
        ScreenManager.register(ModContainerTypes.SKINNING_TABLE, SkinningTableScreen::new);
        ScreenManager.register(ModContainerTypes.SKIN_LIBRARY_CREATIVE, SkinLibraryScreen::new);
        ScreenManager.register(ModContainerTypes.SKIN_LIBRARY, SkinLibraryScreen::new);
        ScreenManager.register(ModContainerTypes.SKIN_LIBRARY_GLOBAL, GlobalSkinLibraryScreen::new);
        ScreenManager.register(ModContainerTypes.HOLOGRAM_PROJECTOR, HologramProjectorScreen::new);
        ScreenManager.register(ModContainerTypes.COLOUR_MIXER, ColourMixerScreen::new);

        ClientRegistry.registerKeyBinding(KeyBindings.UNDO_KEY);
        ClientRegistry.registerKeyBinding(KeyBindings.OPEN_WARDROBE_KEY);

        ClientRegistry.bindTileEntityRenderer(ModTileEntities.HOLOGRAM_PROJECTOR, HologramProjectorTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.SKINNABLE, SkinnableTileEntityRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANNEQUIN, MannequinEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SEAT, SeatEntityRenderer::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.COLOUR_MIXER, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SKINNING_TABLE, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SKIN_LIBRARY_CREATIVE, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SKIN_LIBRARY, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SKIN_LIBRARY_GLOBAL, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.DYE_TABLE, RenderType.cutout());

        event.enqueueWork(SkinRendererManager::init);
        event.enqueueWork(ClientWardrobeHandler::init);
    }

    public void onCommonSetup() {
        registerArgumentTypes();

        EntityProfiles.init();
        ArmourersConfig.init();
        SkinningRecipes.init();
        NetworkHandler.init(AWCore.resource("aw2"));

        DataSerializers.registerSerializer(AWDataSerializers.PLAYER_TEXTURE);
        CapabilityManager.INSTANCE.register(SkinWardrobe.class, new SkinWardrobeStorage(), () -> null);
    }
}
