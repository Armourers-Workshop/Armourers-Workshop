package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnableChild;

public class BlockSkinnableChildGlowing extends BlockSkinnable {

    public BlockSkinnableChildGlowing() {
        super(LibBlockNames.SKINNABLE_CHILD_GLOWING);
        this.setLightLevel(1.0F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnableChild();
    }
}
