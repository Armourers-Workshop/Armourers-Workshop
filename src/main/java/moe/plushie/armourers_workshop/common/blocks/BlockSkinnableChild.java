package moe.plushie.armourers_workshop.common.blocks;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnableChild;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSkinnableChild extends BlockSkinnable  {

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
}
