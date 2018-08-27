package riskyken.armourersWorkshop.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;

public class ItemBlockMarker extends AbstractModItem {

    public ItemBlockMarker() {
        super(LibItemNames.BLOCK_MARKER);
        setSortPriority(12);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(LibItemResources.BLOCK_MARKER);
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
            if (!world.isRemote) {
                int meta = world.getBlockMetadata(x, y, z);
                int newMeta = side + 1;
                if (newMeta == meta) {
                    //This side is already marked.
                    world.setBlockMetadataWithNotify(x, y, z, 0, 2);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
                }
            }
            return true;
        }
        return false;
    }
}
