package extensions.net.minecraft.world.item.CreativeModeTab;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.20, )")
@Extension
public class BuilderProvider {

    private static final DeferredRegister<CreativeModeTab> ITEM_GROUPS = createDeferredRegister(Registries.CREATIVE_MODE_TAB);

    public static Supplier<CreativeModeTab> createCreativeModeTabFO(@ThisClass Class<?> clazz, ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        ModLog.debug("Registering Creative Mode Tab '{}'", registryName);
        return ITEM_GROUPS.register(registryName.getPath(), () -> {
            return CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + registryName.getNamespace() + "." + registryName.getPath()))
                    .icon(() -> icon.get().get())
//                    .withTabsBefore(CreativeModeTabs.SEARCH)
                    .displayItems((features, output) -> {
                        ArrayList<ItemStack> list = new ArrayList<>();
                        itemProvider.accept(list);
                        output.acceptAll(list);
                    })
                    .build();
        });
    }

    public static <B> DeferredRegister<B> createDeferredRegister(ResourceKey<? extends Registry<B>> key) {
        DeferredRegister<B> registry = DeferredRegister.create(key, ModConstants.MOD_ID);
        registry.register(FMLJavaModLoadingContext.get().getModEventBus());
        return registry;
    }
}
