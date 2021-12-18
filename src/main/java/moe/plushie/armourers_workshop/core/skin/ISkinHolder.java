package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinIdentifier;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import net.minecraft.item.ItemStack;

public interface ISkinHolder {
    
    public ItemStack makeSkinStack(Skin skin);
    
    public ItemStack makeSkinStack(ISkinIdentifier identifier);
    
    public ItemStack makeSkinStack(ISkinDescriptor descriptor);
}
