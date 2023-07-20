package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ItemStackStorage {

    public SkinDescriptor skinDescriptor;
    public Optional<IPaintColor> paintColor;
    public Optional<BlockPaintColor> blockPaintColor;

    public static ItemStackStorage of(@NotNull ItemStack itemStack) {
        IAssociatedObjectProvider provider = ObjectUtils.unsafeCast(itemStack);
        ItemStackStorage storage = provider.getAssociatedObject();
        if (storage == null) {
            storage = new ItemStackStorage();
            provider.setAssociatedObject(storage);
        }
        return storage;
    }

}
