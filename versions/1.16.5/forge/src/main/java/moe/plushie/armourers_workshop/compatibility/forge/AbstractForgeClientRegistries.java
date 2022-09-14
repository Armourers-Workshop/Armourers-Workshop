package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.function.Consumer;

public interface AbstractForgeClientRegistries {

    static void registerSpecialModel(ResourceLocation rl) {
        ModelLoader.addSpecialModel(rl);
    }

    static void registerItemProperty(Item item, ResourceLocation key, IItemModelProperty property) {
        ItemProperties.register(item, key, (itemStack, level, entity) -> property.getValue(itemStack, level, entity, 0));
    }

    static void registerStitchTexture(TextureStitchEvent.Pre event, Consumer<Consumer<ResourceLocation>> consumer) {
        if (event.getMap().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            consumer.accept(event::addSprite);
        }
    }

    static void registerKeyMapping(KeyMapping mapping) {
        ClientRegistry.registerKeyBinding(mapping);
    }
}
