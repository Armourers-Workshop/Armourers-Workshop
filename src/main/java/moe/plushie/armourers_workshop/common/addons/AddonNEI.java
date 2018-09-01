package moe.plushie.armourers_workshop.common.addons;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;

public class AddonNEI extends ModAddon {

    public AddonNEI() {
        super("NotEnoughItems", "NotEnoughItems");
    }
    
    public boolean isVisible() {
        if (isModLoaded()) {
            if (!isEnabled()) {
                return false;
            }
            try {
                Class<?> c = Class.forName("codechicken.nei.NEIClientConfig");
                Object object = c.getMethod("isHidden").invoke(null);
                if (object != null && object instanceof Boolean) {
                    return !(Boolean)object;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    private boolean isEnabled() {
        try {
            Class<?> c = Class.forName("codechicken.nei.NEIClientConfig");
            Object object = c.getMethod("isEnabled").invoke(null);
            if (object != null && object instanceof Boolean) {
                return (Boolean)object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void hideItem(ItemStack stack) {
        if (isModLoaded()) {
            try {
                Class ccApi = Class.forName("codechicken.nei.api.API");
                Method ccHideStack = ccApi.getMethod("hideItem", ItemStack.class);
                ccHideStack.invoke(null, stack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
