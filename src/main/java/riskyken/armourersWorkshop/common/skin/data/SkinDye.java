package riskyken.armourersWorkshop.common.skin.data;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;

public class SkinDye implements ISkinDye {
    
    public SkinDye() {
        // TODO Auto-generated constructor stub
    }
    
    public SkinDye(ISkinDye skinDye) {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public int getNumberOfDyes() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] getDyeColour(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    public void writeToCompound(NBTTagCompound compound) {
        // TODO Auto-generated method stub
    }

    public void readFromCompound(NBTTagCompound compound) {
        // TODO Auto-generated method stub
    }
}
