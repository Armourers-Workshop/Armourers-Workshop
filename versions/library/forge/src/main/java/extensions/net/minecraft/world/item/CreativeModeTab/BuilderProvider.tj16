package extensions.net.minecraft.world.item.CreativeModeTab;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.20)")
@Extension
public class BuilderProvider {

    public static Supplier<CreativeModeTab> createCreativeModeTabFO(@ThisClass Class<?> clazz, String name, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        ResourceLocation registryName = ModConstants.key(name);
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
