package riskyken.armourersWorkshop.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabArmourersWorkshop extends CreativeTabs {

    public CreativeTabArmourersWorkshop(int id, String label) {
        super(id, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Item.getItemFromBlock(ModBlocks.armourerBrain);
    }
}
