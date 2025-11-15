package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.builder.AbstractCreativeModeTabBuilder;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Available("[1.20, )")
public class AbstractFabricCreativeModeTabBuilder<T extends CreativeModeTab> extends AbstractCreativeModeTabBuilder<T> {

    @Override
    protected CreativeModeTab create(OpenResourceLocation registryName, Consumer<List<ItemStack>> provider) {
        return FabricItemGroup.builder()
                .title(Component.translatable(registryName.toLanguageKey("itemGroup")))
                .icon(() -> icon.get().get())
                .displayItems((set, out) -> {
                    var results = new ArrayList<ItemStack>();
                    provider.accept(results);
                    out.acceptAll(results);
                })
                .build();
    }
}
