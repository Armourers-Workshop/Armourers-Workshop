package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.minecraftWrapper.common.item.ItemStackPointer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSoap extends AbstractModItemNew {

    public ItemSoap() {
        super(LibItemNames.SOAP);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
        iconList.add(LibItemResources.SOAP);
    }
    
    @Override
    public int getColorFromItemStack(ItemStackPointer stack, int pass) {
        return 0xFFFF7FD2;
    }
}
