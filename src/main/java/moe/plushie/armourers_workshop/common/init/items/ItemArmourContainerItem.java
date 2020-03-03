package moe.plushie.armourers_workshop.common.init.items;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.skin.ISkinHolder;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.item.ItemStack;

public class ItemArmourContainerItem extends AbstractModItem implements ISkinHolder {

    public ItemArmourContainerItem() {
        super(LibItemNames.ARMOUR_CONTAINER);
        setMaxStackSize(64);
    }

    @Override
    public ItemStack makeSkinStack(Skin skin) {
        return makeSkinStack(new SkinIdentifier(skin));
    }

    @Override
    public ItemStack makeSkinStack(ISkinIdentifier identifier) {
        return makeSkinStack(new SkinDescriptor(identifier));
    }
    
    @Override
    public ItemStack makeSkinStack(ISkinDescriptor descriptor) {
        if (descriptor.getIdentifier().getSkinType().getVanillaArmourSlotId() == -1) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(ModItems.ARMOUR_CONTAINER[descriptor.getIdentifier().getSkinType().getVanillaArmourSlotId()], 1);
        SkinNBTHelper.addSkinDataToStack(stack, (SkinDescriptor) descriptor);
        return stack;
    }
}
