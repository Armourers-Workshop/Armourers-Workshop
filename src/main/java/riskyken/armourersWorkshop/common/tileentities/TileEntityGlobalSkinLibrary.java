package riskyken.armourersWorkshop.common.tileentities;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;

public class TileEntityGlobalSkinLibrary extends TileEntity implements IButtonPress  {
    
    public TileEntityGlobalSkinLibrary() {
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        if (buttonId == 0) {
            
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
    }
}
