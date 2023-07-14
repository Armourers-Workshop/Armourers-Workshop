package extensions.net.minecraft.world.item.CreativeModeTab;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.ModConstants;
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

    private static ResourceLocation LAST_ITEM_GROUP;
    private static final DeferredRegister<CreativeModeTab> ITEM_GROUPS = createDeferredRegister(Registries.CREATIVE_MODE_TAB);

    public static Supplier<CreativeModeTab> createCreativeModeTabFO(@ThisClass Class<?> clazz, String name, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        ResourceLocation lastItemGroup = LAST_ITEM_GROUP;
        ResourceLocation registryName = ModConstants.key(name);
        LAST_ITEM_GROUP = registryName;
        return ITEM_GROUPS.register(name, () -> {
            CreativeModeTab.Builder builder = CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + registryName.getNamespace() + "." + registryName.getPath()))
                    .icon(() -> icon.get().get())
                    .displayItems((features, output) -> {
                        ArrayList<ItemStack> list = new ArrayList<>();
                        itemProvider.accept(list);
                        output.acceptAll(list);
                    });
            if (lastItemGroup != null) {
                builder = builder.withTabsBefore(lastItemGroup);
            } else {
                builder = builder.withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
            }
            return builder.build();
        });
    }

    public static <B> DeferredRegister<B> createDeferredRegister(ResourceKey<? extends Registry<B>> key) {
        DeferredRegister<B> registry = DeferredRegister.create(key, ModConstants.MOD_ID);
        registry.register(FMLJavaModLoadingContext.get().getModEventBus());
        return registry;
    }
}
