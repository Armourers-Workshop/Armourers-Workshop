package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.color.BlockPaintColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ItemStackStorage {

    public SkinDescriptor skinDescriptor;
    public Optional<IPaintColor> paintColor;
    public Optional<BlockPaintColor> blockPaintColor;

    public static ItemStackStorage of(@Nonnull ItemStack itemStack) {
        ISkinDataProvider provider = (ISkinDataProvider) (Object) itemStack;
        ItemStackStorage storage = provider.getSkinData();
        if (storage == null) {
            storage = new ItemStackStorage();
            provider.setSkinData(storage);
        }
        return storage;
    }

}
