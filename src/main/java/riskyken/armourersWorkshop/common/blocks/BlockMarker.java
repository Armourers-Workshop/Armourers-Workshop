package riskyken.armourersWorkshop.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.block.ModItemBlockWithMetadata;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMarker extends AbstractModBlock {

    public BlockMarker() {
        super(LibBlockNames.MARKER);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlockWithMetadata.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public int damageDropped(int meta) {
        return meta;
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon marker2Icon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.MARKER_1);
        marker2Icon = register.registerIcon(LibBlockResources.MARKER_2);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 1) {
            return marker2Icon;
        }
        return blockIcon;
    }
}
