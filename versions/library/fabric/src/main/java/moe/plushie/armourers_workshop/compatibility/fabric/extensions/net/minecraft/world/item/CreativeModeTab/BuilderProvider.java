package moe.plushie.armourers_workshop.compatibility.fabric.extensions.net.minecraft.world.item.CreativeModeTab;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.20, )")
@Extension
public class BuilderProvider {

    public static Supplier<CreativeModeTab> createCreativeModeTabFA(@ThisClass Class<?> clazz, String name, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        ResourceLocation registryName = ModConstants.key(name);
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup." + registryName.getNamespace() + "." + registryName.getPath()))
                .icon(() -> icon.get().get())
                .displayItems((set, out) -> {
                    ArrayList<ItemStack> results = new ArrayList<>();
                    itemProvider.accept(results);
                    out.acceptAll(results);
                })
                .build();
        return () -> tab;
    }
}
