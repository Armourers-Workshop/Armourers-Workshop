package moe.plushie.armourers_workshop.core.client.render.state;


import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelResolver;
import net.minecraft.world.item.ItemStack;

public class ItemStackRenderState {

    protected final boolean shouldRenderInGUI;

    protected final Object itemModel;
    protected final ItemStack itemStack;
    protected final SkinItemModelResolver itemModelResolver;

    public ItemStackRenderState(ItemStack itemStack, Object originItemModel, SkinItemModelResolver itemModelResolver, boolean shouldRenderInGUI) {
        this.itemStack = itemStack;
        this.itemModel = originItemModel;
        this.itemModelResolver = itemModelResolver;
        this.shouldRenderInGUI = shouldRenderInGUI;
    }

    public Object itemModel() {
        return itemModel;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public SkinItemModelResolver itemModelResolver() {
        return itemModelResolver;
    }

    public boolean shouldRenderInGUI() {
        return shouldRenderInGUI;
    }
}
