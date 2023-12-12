package moe.plushie.armourers_workshop.builder.client.gui.widget;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.world.item.ItemStack;

public class PartItem {

    private final SkinDescriptor descriptor;
    private final ItemStack itemStack;

    public PartItem(SkinDescriptor descriptor) {
        this(descriptor, ItemStack.EMPTY);
    }

    public PartItem(SkinDescriptor descriptor, ItemStack itemStack) {
        this.descriptor = descriptor;
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }

    public boolean hasSkin() {
        return !descriptor.isEmpty();
    }

    public boolean hasItem() {
        return !itemStack.isEmpty();
    }
}
