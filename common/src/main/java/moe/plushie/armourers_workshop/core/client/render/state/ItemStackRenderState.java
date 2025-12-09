package moe.plushie.armourers_workshop.core.client.render.state;


import moe.plushie.armourers_workshop.compat.client.item.model.AbstractItemModel;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelResolver;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.world.item.ItemStack;

public class ItemStackRenderState {

    protected final boolean shouldRenderInGUI;

    protected final AbstractItemModel itemModel;
    protected final ItemStack itemStack;
    protected final SkinItemModelResolver itemModelResolver;

    public ItemStackRenderState(ItemStack itemStack, AbstractItemModel originItemModel, SkinItemModelResolver itemModelResolver, boolean shouldRenderInGUI) {
        this.itemStack = itemStack;
        this.itemModel = originItemModel;
        this.itemModelResolver = itemModelResolver;
        this.shouldRenderInGUI = shouldRenderInGUI;
    }

    public AbstractItemModel itemModel() {
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

    public boolean shouldRenderItemRenderer(SkinDescriptor.Options options) {
        // 0: auto, 1: disable, 2: enable, 3: enable without gui
        return switch (options.embeddedItemRenderer()) {
            case 3 -> !shouldRenderInGUI;
            case 2 -> true;
            default -> false;
        };
    }
}
