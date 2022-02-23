package moe.plushie.armourers_workshop.core.registry;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.command.SkinCommands;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.gui.SkinWardrobeScreen;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.render.entity.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.texture.TextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWKeyBindings;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
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
import net.minecraftforge.registries.DataSerializerEntry;

public class AWRegistry {


    public void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                AWItems.SKIN,
                AWItems.BOTTLE,
                AWItems.MANNEQUIN
        );
    }

    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                AWEntities.MANNEQUIN
        );
    }

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(AWEntities.MANNEQUIN, MannequinEntity.createLivingAttributes().build());
    }

    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {

    }

    public void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                SkinWardrobeContainer.TYPE
        );
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

        ScreenManager.register(SkinWardrobeContainer.TYPE, SkinWardrobeScreen::new);

        ClientRegistry.registerKeyBinding(AWKeyBindings.UNDO_KEY);
        ClientRegistry.registerKeyBinding(AWKeyBindings.OPEN_WARDROBE_KEY);

        RenderingRegistry.registerEntityRenderingHandler(AWEntities.MANNEQUIN, MannequinEntityRenderer::new);
    }
}
