package riskyken.armourers_workshop.common.blocks;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.items.ItemDebugTool.IDebug;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntitySkinnableChild;

public class BlockSkinnableChild extends BlockSkinnable implements IDebug  {

    public BlockSkinnableChild() {
        super(LibBlockNames.SKINNABLE_CHILD);
    }
    public BlockSkinnableChild(String name) {
        super(name);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnableChild();
    }
    
    @Override
    public void getDebugHoverText(World world, BlockPos pos, ArrayList<String> textLines) {
        TileEntitySkinnableChild te = (TileEntitySkinnableChild)world.getTileEntity(pos);
        super.getDebugHoverText(world, pos, textLines);
        textLines.add("parent=" + te.getParent());
        /*
        textLines.add("offset X=" + (-te.parentX + te.xCoord));
        textLines.add("offset Y=" + (te.yCoord - te.parentY));
        textLines.add("offset Z=" + (-(te.parentZ - te.zCoord)));
        */
    }
}
