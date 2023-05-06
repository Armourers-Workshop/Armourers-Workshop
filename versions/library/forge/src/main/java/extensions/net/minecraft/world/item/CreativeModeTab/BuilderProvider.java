package extensions.net.minecraft.world.item.CreativeModeTab;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.16, 1.19.4)")
@Extension
public class BuilderProvider {

    public static Supplier<CreativeModeTab> createCreativeModeTab(@ThisClass Class<?> clazz, ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        CreativeModeTab tab = new CreativeModeTab(registryName.getNamespace() + "." + registryName.getPath()) {
            @Override
            public ItemStack makeIcon() {
                return icon.get().get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> arg) {
                itemProvider.accept(arg);
            }
        };
        return () -> tab;
    }
}
