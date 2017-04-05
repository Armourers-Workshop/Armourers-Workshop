package riskyken.armourersWorkshop.common.blocks;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.items.ItemDebugTool.IDebug;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnableChild;

public class BlockSkinnableChild extends BlockSkinnable implements IDebug  {

    public BlockSkinnableChild() {
        super(LibBlockNames.SKINNABLE_CHILD);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnableChild();
    }
    
    @Override
    public void getDebugHoverText(World world, int x, int y, int z, ArrayList<String> textLines) {
        TileEntitySkinnableChild te = (TileEntitySkinnableChild)world.getTileEntity(x, y, z);
        super.getDebugHoverText(world, x, y, z, textLines);
        textLines.add("parent X=" + te.parentX);
        textLines.add("parent Y=" + te.parentY);
        textLines.add("parent Z=" + te.parentZ);
        textLines.add("offset X=" + (-te.parentX + te.xCoord));
        textLines.add("offset Y=" + (te.yCoord - te.parentY));
        textLines.add("offset Z=" + (-(te.parentZ - te.zCoord)));
    }
}
