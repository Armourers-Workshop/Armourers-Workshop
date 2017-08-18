package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;

public class TileEntityGlobalSkinLibrary extends TileEntity implements IButtonPress  {
    
    public TileEntityGlobalSkinLibrary() {
        // TODO Auto-generated constructor stub
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
}
