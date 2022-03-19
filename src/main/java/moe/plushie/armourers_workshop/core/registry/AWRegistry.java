package moe.plushie.armourers_workshop.core.registry;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.base.AWBlocks;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.base.AWTileEntities;
import moe.plushie.armourers_workshop.core.container.HologramProjectorContainer;
import moe.plushie.armourers_workshop.core.container.SkinnableContainer;
import moe.plushie.armourers_workshop.core.container.SkinningTableContainer;
import moe.plushie.armourers_workshop.core.gui.misc.SkinnableScreen;
import moe.plushie.armourers_workshop.core.gui.misc.SkinningTableScreen;
import moe.plushie.armourers_workshop.core.render.entity.SeatEntityRenderer;
import moe.plushie.armourers_workshop.core.utils.AWContainerOpener;
import moe.plushie.armourers_workshop.core.utils.command.SkinCommands;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.gui.hologramprojector.HologramProjectorScreen;
import moe.plushie.armourers_workshop.core.gui.wardrobe.WardrobeScreen;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.render.tileentities.HologramProjectorTileEntityRenderer;
import moe.plushie.armourers_workshop.core.render.entity.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.render.tileentities.SkinnableTileEntityRenderer;
import moe.plushie.armourers_workshop.core.utils.AWKeyBindings;
import moe.plushie.armourers_workshop.core.container.WardrobeContainer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AWRegistry {


    public void registerBlocks(RegistryEvent.Register<Block> event) {
        AWBlocks.forEach(event.getRegistry()::register);
    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        AWItems.forEach(event.getRegistry()::register);
    }

    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        AWEntities.forEach(event.getRegistry()::register);
    }

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(AWEntities.MANNEQUIN, MannequinEntity.createLivingAttributes().build());
        event.put(AWEntities.SEAT, MannequinEntity.createLivingAttributes().build());
    }

    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        AWTileEntities.forEach(event.getRegistry()::register);
    }

    public void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        AWContainerOpener.forEach(event.getRegistry()::register);
    }


    public void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(SkinCommands.commands());
    }

    @OnlyIn(Dist.CLIENT)
    public void registerItemModels(ModelRegistryEvent event) {
        ItemModelsProperties.register(AWItems.BOTTLE, AWCore.resource("empty"), BottleItem::isEmpty);
        ItemModelsProperties.register(AWItems.SKIN, AWCore.resource("loading"), SkinItem::getIconIndex);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register(ColoredItem.getColorProvider(0), AWItems.BOTTLE);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerClientEvents() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//
//        modEventBus.addListener(this::registerParticleFactories);
//        modEventBus.addListener(this::registerTextures);
        modEventBus.addListener(this::registerItemColors);
        modEventBus.addListener(this::registerItemModels);
//        modEventBus.addListener(this::handleModelBake);

        ScreenManager.register(WardrobeContainer.TYPE, WardrobeScreen::new);
        ScreenManager.register(SkinnableContainer.TYPE, SkinnableScreen::new);
        ScreenManager.register(SkinningTableContainer.TYPE, SkinningTableScreen::new);
        ScreenManager.register(HologramProjectorContainer.TYPE, HologramProjectorScreen::new);

        ClientRegistry.registerKeyBinding(AWKeyBindings.UNDO_KEY);
        ClientRegistry.registerKeyBinding(AWKeyBindings.OPEN_WARDROBE_KEY);

        ClientRegistry.bindTileEntityRenderer(AWTileEntities.HOLOGRAM_PROJECTOR, HologramProjectorTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(AWTileEntities.SKINNABLE, SkinnableTileEntityRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(AWEntities.MANNEQUIN, MannequinEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AWEntities.SEAT, SeatEntityRenderer::new);
    }
}
