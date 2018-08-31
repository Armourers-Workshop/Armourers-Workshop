package riskyken.armourers_workshop.common.skin;

import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.skin.data.Skin;

public interface ISkinHolder {
    
    public ItemStack makeStackForEquipment(Skin skin);
}
