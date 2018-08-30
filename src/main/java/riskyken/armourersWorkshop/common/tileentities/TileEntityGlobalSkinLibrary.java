package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;

public class TileEntityGlobalSkinLibrary extends TileEntity implements IButtonPress  {
    
    public TileEntityGlobalSkinLibrary() {
    }

    @Override
    public void buttonPressed(byte buttonId) {
        if (buttonId == 0) {
            
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos());
    }
}
