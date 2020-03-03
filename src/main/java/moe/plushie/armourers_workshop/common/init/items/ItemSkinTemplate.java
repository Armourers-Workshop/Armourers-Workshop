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

public class ItemSkinTemplate extends AbstractModItem implements ISkinHolder {
    
    private static final String TAG_OWNER = "owner";
    
    public ItemSkinTemplate() {
        super(LibItemNames.SKIN_TEMPLATE);
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
        ItemStack stack = new ItemStack(ModItems.SKIN);
        SkinNBTHelper.addSkinDataToStack(stack, (SkinDescriptor) descriptor);
        return stack;
    }
}
