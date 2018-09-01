package moe.plushie.armourers_workshop.common.skin;

import moe.plushie.armourers_workshop.common.skin.data.Skin;
import net.minecraft.item.ItemStack;

public interface ISkinHolder {
    
    public ItemStack makeStackForEquipment(Skin skin);
}
