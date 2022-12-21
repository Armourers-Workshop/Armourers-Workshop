package moe.plushie.armourers_workshop.compatibility.forge.ext;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientNativeProvider;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

@Available("[1.18, )")
public interface AbstractClientForgeExt_V18 extends AbstractForgeClientNativeProvider {

    void willRegisterItemRenderer(Consumer<ItemRendererRegistry> consumer);

    interface ItemRendererRegistry {
        void register(Item item, IItemStackRendererProvider provider);
    }
}
