package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class EmbeddedSkinStack {

    private final int mode;
    private final ItemStack itemStack;

    private final SkinDescriptor descriptor;
    private final SkinRenderData.Entry entry;

    public EmbeddedSkinStack(int mode, SkinRenderData.Entry entry) {
        this.mode = mode;
        this.entry = entry;
        this.descriptor = entry.getDescriptor();
        this.itemStack = entry.getItemStack();
    }

    public EmbeddedSkinStack(int mode, SkinDescriptor descriptor, ItemStack itemStack) {
        this.mode = mode;
        this.entry = null;
        this.descriptor = descriptor;
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }

    public SkinRenderData.Entry getEntry() {
        return entry;
    }

    public int getMode() {
        return mode;
    }
}
