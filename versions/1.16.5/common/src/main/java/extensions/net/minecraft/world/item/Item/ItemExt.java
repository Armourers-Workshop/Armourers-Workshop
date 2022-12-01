package extensions.net.minecraft.world.item.Item;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

@Extension
public class ItemExt {

    public static boolean allowedIn(@This Item item, CreativeModeTab tab) {
        CreativeModeTab creativeModeTab2 = item.getItemCategory();
        return creativeModeTab2 != null && (tab == CreativeModeTab.TAB_SEARCH || tab == creativeModeTab2);
    }
}
