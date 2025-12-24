package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.builder.AbstractCreativeModeTabBuilder;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Available("[1.20, )")
public class AbstractForgeCreativeModeTabBuilder<T extends CreativeModeTab> extends AbstractCreativeModeTabBuilder<T> {

    private static OpenResourceLocation LAST_ITEM_GROUP;

    @Override
    protected CreativeModeTab create(OpenResourceLocation registryName, Consumer<List<ItemStack>> provider) {
        var lastItemGroup = LAST_ITEM_GROUP;
        var builder = CreativeModeTab.builder()
                .title(Component.translatable(registryName.toLanguageKey("itemGroup")))
                .icon(() -> icon.get().get())
                .displayItems((features, output) -> {
                    var list = new ArrayList<ItemStack>();
                    provider.accept(list);
                    output.acceptAll(list);
                });
        if (lastItemGroup != null) {
            builder = builder.withTabsBefore(lastItemGroup.get());
        } else {
            builder = builder.withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
        }
        LAST_ITEM_GROUP = registryName;
        return builder.build();
    }
}
