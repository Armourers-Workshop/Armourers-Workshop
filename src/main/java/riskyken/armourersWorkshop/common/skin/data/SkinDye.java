package riskyken.armourersWorkshop.common.skin.data;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;

public class SkinDye implements ISkinDye {
    
    private static final String TAG_SKIN_DYE = "dyeData";
    private static final String TAG_DYE_COUNT = "dyeCount";
    private static final String TAG_DYE_ARRAY = "dyeArray";
    
    private ArrayList<byte[]> dyes;
    
    public SkinDye() {
        dyes = new ArrayList<byte[]>();
    }
    
    public SkinDye(ISkinDye skinDye) {
        for (int i = 0; i < skinDye.getNumberOfDyes(); i++) {
            dyes.add(skinDye.getDyeColour(i).clone());
        }
    }
    
    @Override
    public int getNumberOfDyes() {
        return dyes.size();
    }

    @Override
    public byte[] getDyeColour(int index) {
        return dyes.get(index);
    }
    
    @Override
    public void addDye(int index, byte[] rgb) {
        dyes.add(index, rgb);
    }
    
    @Override
    public void removeDye(int index) {
        dyes.remove(index);
    }

    public void writeToCompound(NBTTagCompound compound) {
        NBTTagCompound dyeCompound = new NBTTagCompound();
        dyeCompound.setInteger(TAG_DYE_COUNT, dyes.size());
        for (int i = 0; i < dyes.size(); i++) {
            dyeCompound.setByteArray(TAG_DYE_ARRAY + i, dyes.get(i));
        }
        compound.setTag(TAG_SKIN_DYE, dyeCompound);
    }

    public void readFromCompound(NBTTagCompound compound) {
        NBTTagCompound dyeCompound = compound.getCompoundTag(TAG_SKIN_DYE);
        int count = dyeCompound.getInteger(TAG_DYE_COUNT);
        for (int i = 0; i < count; i++) {
            dyes.add(compound.getByteArray(TAG_DYE_ARRAY + 1));
        }
    }
}
