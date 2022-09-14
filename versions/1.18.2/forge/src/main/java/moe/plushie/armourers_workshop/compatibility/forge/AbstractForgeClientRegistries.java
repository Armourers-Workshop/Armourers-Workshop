package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;

import java.util.function.Consumer;

public interface AbstractForgeClientRegistries {

    static void registerSpecialModel(ResourceLocation rl) {
        ForgeModelBakery.addSpecialModel(rl);
    }

    static void registerItemProperty(Item item, ResourceLocation key, IItemModelProperty property) {
        ItemProperties.register(item, key, property::getValue);
    }

    static void registerStitchTexture(TextureStitchEvent.Pre event, Consumer<Consumer<ResourceLocation>> consumer) {
        if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            consumer.accept(event::addSprite);
        }
    }

    static void registerKeyMapping(KeyMapping mapping) {
        ClientRegistry.registerKeyBinding(mapping);
    }
}
