package riskyken.armourersWorkshop.common.skin;

import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.skin.data.Skin;

public interface ISkinHolder {
    
    public ItemStack makeStackForEquipment(Skin skin);
}
