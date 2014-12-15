package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiniArmourer extends TileEntity {

    @SideOnly(Side.CLIENT)
    public int red;
    @SideOnly(Side.CLIENT)
    public int green;
    @SideOnly(Side.CLIENT)
    public int blue;
    
    public TileEntityMiniArmourer() {
    }
}
