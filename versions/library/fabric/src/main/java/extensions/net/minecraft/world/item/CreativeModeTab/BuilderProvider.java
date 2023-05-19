package extensions.net.minecraft.world.item.CreativeModeTab;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.16, 1.19.3)")
@Extension
public class BuilderProvider {

    public static CreativeModeTab createCreativeModeTabFA(@ThisClass Class<?> clazz, ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        return FabricItemGroupBuilder.create(registryName)
                .icon(() -> icon.get().get())
                .appendItems(itemProvider)
                .build();
    }
}
