package moe.plushie.armourers_workshop.common.skin;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import net.minecraft.item.ItemStack;

public interface ISkinHolder {
    
    public ItemStack makeSkinStack(Skin skin);
    
    public ItemStack makeSkinStack(ISkinIdentifier identifier);
    
    public ItemStack makeSkinStack(ISkinDescriptor descriptor);
}
