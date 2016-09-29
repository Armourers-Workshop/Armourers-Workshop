package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;

public class TileEntityGlobalSkinLibrary extends TileEntity implements IButtonPress  {
    
    //http://plushie.moe/armourers_workshop/skin-list.php
    //http://plushie.moe/armourers_workshop/skin-upload.php
    
    public TileEntityGlobalSkinLibrary() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        // TODO Auto-generated method stub
    }
}
