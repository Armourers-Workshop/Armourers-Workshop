package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.plushieWrapper.common.entity.PlushieEntityPlayer;
import riskyken.plushieWrapper.common.item.PlushieItemStack;
import riskyken.plushieWrapper.common.world.BlockLocation;
import riskyken.plushieWrapper.common.world.WorldPointer;

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
    public boolean onItemUse(PlushieItemStack stack, PlushieEntityPlayer player, WorldPointer world,
            BlockLocation blockLocation, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(blockLocation);
        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
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
