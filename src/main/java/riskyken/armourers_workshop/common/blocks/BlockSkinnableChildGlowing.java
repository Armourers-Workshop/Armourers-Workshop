package riskyken.armourers_workshop.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntitySkinnableChild;

public class BlockSkinnableChildGlowing extends BlockSkinnableChild {

    public BlockSkinnableChildGlowing() {
        super(LibBlockNames.SKINNABLE_CHILD_GLOWING);
        this.setLightLevel(1.0F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnableChild();
    }
}
