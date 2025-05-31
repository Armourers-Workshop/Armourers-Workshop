package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ItemStackStorage {

    public SkinDescriptor skinDescriptor;
    public Optional<SkinPaintColor> paintColor;
    public Optional<BlockPaintColor> blockPaintColor;

    public ItemStackStorage(ItemStack itemStack) {
    }

    public static ItemStackStorage of(@NotNull ItemStack itemStack) {
        return DataContainer.of(itemStack, ItemStackStorage::new);
    }
}
