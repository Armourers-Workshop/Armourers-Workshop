package moe.plushie.armourers_workshop.common.init.blocks;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.permission.IPermissionHolder;

public class BlockSkinnableGlowing extends BlockSkinnable {
    
    public BlockSkinnableGlowing() {
        super(LibBlockNames.SKINNABLE_GLOWING);
        this.setLightLevel(1.0F);
    }
    
    @Override
    public String getPermissionName() {
        return ((IPermissionHolder)ModBlocks.SKINNABLE).getPermissionName();
    }
}
