package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class SkinningRecipe {

    protected ISkinType skinType;

    public SkinningRecipe(ISkinType skinType) {
        this.skinType = skinType;
    }

    public void apply(Container inventory) {
        var skinStack = ItemStack.EMPTY;
        var targetStack = ItemStack.EMPTY;

        int size = inventory.getContainerSize();
        for (int i = 1; i < size; ++i) { // 0 is output
            var itemStack = inventory.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            if (isValidSkin(itemStack)) {
                skinStack = itemStack;
                continue;
            }
            if (isValidTarget(itemStack)) {
                targetStack = itemStack;
                continue;
            }
            return;
        }

        if (targetStack.isEmpty() || skinStack.isEmpty()) {
            return;
        }

        shrink(targetStack, skinStack);
    }

    public ItemStack test(Container inventory, SkinOptions options) {
        var skinStack = ItemStack.EMPTY;
        var targetStack = ItemStack.EMPTY;

        int size = inventory.getContainerSize();
        for (int i = 1; i < size; ++i) { // 0 is output
            var itemStack = inventory.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            if (isValidSkin(itemStack)) {
                skinStack = itemStack;
                continue;
            }
            if (isValidTarget(itemStack)) {
                targetStack = itemStack;
                continue;
            }
            return ItemStack.EMPTY;
        }

        if (targetStack.isEmpty() || skinStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return build(targetStack, skinStack, options);
    }

    protected void shrink(ItemStack targetStack, ItemStack skinStack) {
        targetStack.shrink(1);
        skinStack.shrink(1);
    }

    protected ItemStack build(ItemStack targetStack, ItemStack skinStack, SkinOptions options) {
        var skin = skinStack.getOrDefault(ModDataComponents.SKIN.get(), SkinDescriptor.EMPTY);
        var newItemStack = targetStack.copy();
        newItemStack.setCount(1);
        newItemStack.set(ModDataComponents.SKIN.get(), new SkinDescriptor(skin.getIdentifier(), skin.getType(), options, skin.getColorScheme()));
        return newItemStack;
    }

    protected boolean isValidSkin(ItemStack itemStack) {
        return ModItems.SKIN.get() == itemStack.getItem() && SkinDescriptor.of(itemStack).getType() == skinType;
    }

    protected boolean isValidTarget(ItemStack itemStack) {
        return ModItems.SKIN.get() != itemStack.getItem();
    }
}
