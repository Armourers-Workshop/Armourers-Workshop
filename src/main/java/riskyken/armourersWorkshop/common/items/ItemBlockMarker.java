package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.cubes.CubeFactory;
import riskyken.minecraftWrapper.common.entity.EntityPlayerPointer;
import riskyken.minecraftWrapper.common.item.ItemStackPointer;
import riskyken.minecraftWrapper.common.world.BlockLocation;
import riskyken.minecraftWrapper.common.world.WorldPointer;

public class ItemBlockMarker extends AbstractModItemNew {

    public ItemBlockMarker() {
        super(LibItemNames.BLOCK_MARKER);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
        iconList.add(LibItemResources.BLOCK_MARKER);
    }
    
    @Override
    public boolean onItemUse(ItemStackPointer stack, EntityPlayerPointer player, WorldPointer world,
            BlockLocation blockLocation, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(blockLocation);
        if (CubeFactory.INSTANCE.isBuildingBlock(block)) {
            if (!world.isRemote()) {
                int meta = world.getBlockMetadata(blockLocation);
                int newMeta = side + 1;
                if (newMeta == meta) {
                    //This side is already marked.
                    world.setBlockMetaData(blockLocation, 0, 2);
                } else {
                    world.setBlockMetaData(blockLocation, newMeta, 2);
                }
            }
            return true;
        }
        return false;
    }
}
