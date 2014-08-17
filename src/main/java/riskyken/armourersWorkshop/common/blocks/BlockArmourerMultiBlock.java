package riskyken.armourersWorkshop.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.block.ModItemBlockWithMetadata;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockArmourerMultiBlock extends AbstractModBlock {

    public BlockArmourerMultiBlock() {
        super(LibBlockNames.ARMOURER_MULTI_BLOCK);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlockWithMetadata.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public int damageDropped(int meta) {
        return meta;
    }
}
