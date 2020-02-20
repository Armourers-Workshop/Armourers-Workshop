package moe.plushie.armourers_workshop.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.data.serialize.SkinIdentifierSerializer;
import net.minecraft.nbt.NBTTagCompound;

public class SkinDescriptor implements ISkinDescriptor {

    public static final String TAG_SKIN_DATA = "armourersWorkshop";
    
    private ISkinIdentifier identifier;
    public SkinDye skinDye;
    
    public SkinDescriptor() {
        this.skinDye = new SkinDye();
        this.identifier = new SkinIdentifier(0, null, 0, null);
    }
    
    public SkinDescriptor(Skin skin) {
        this(new SkinIdentifier(skin.lightHash(), null, 0, skin.getSkinType()));
    }
    
    public SkinDescriptor(ISkinDescriptor skinPointer) {
        this.skinDye = new SkinDye(skinPointer.getSkinDye());
    }
    
    public SkinDescriptor(ISkinIdentifier identifier) {
        this.identifier = identifier;
        this.skinDye = new SkinDye();
    }
    
    public SkinDescriptor(ISkinIdentifier identifier, ISkinDye skinDye) {
        this.identifier = identifier;
        if (skinDye != null) {
            this.skinDye = new SkinDye(skinDye);
        } else {
            this.skinDye = new SkinDye();
        }
    }
    
    @Override
    public ISkinIdentifier getIdentifier() {
        return identifier;
    }
    
    @Override
    public ISkinDye getSkinDye() {
        return skinDye;
    }
    
    public void readFromCompound(NBTTagCompound compound) {
        readFromCompound(compound, TAG_SKIN_DATA);
    }
    
    public void readFromCompound(NBTTagCompound compound, String tag) {
        NBTTagCompound skinDataCompound = compound.getCompoundTag(tag);
        this.identifier = SkinIdentifierSerializer.readFromCompound(skinDataCompound);
        this.skinDye.readFromCompound(skinDataCompound);
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        writeToCompound(compound, TAG_SKIN_DATA);
    }
    
    public void writeToCompound(NBTTagCompound compound, String tag) {
        NBTTagCompound skinDataCompound = new NBTTagCompound();
        SkinIdentifierSerializer.writeToCompound(identifier, skinDataCompound);
        skinDye.writeToCompound(skinDataCompound);
        compound.setTag(tag, skinDataCompound);
    }
}
