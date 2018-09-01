package moe.plushie.armourers_workshop.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinPointer;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinIdentifierSerializer;
import net.minecraft.nbt.NBTTagCompound;

public class SkinPointer implements ISkinPointer {

    public static final String TAG_SKIN_DATA = "armourersWorkshop";
    private static final String TAG_SKIN_LOCK = "lock";
    
    private SkinIdentifier identifier;
    
    public boolean lockSkin;
    public SkinDye skinDye;
    
    public SkinPointer() {
        this.skinDye = new SkinDye();
        this.identifier = new SkinIdentifier(0, null, 0, null);
    }
    
    public SkinPointer(Skin skin) {
        this(new SkinIdentifier(skin.lightHash(), null, 0, skin.getSkinType()));
    }
    
    public SkinPointer(ISkinPointer skinPointer) {
        this.identifier = new SkinIdentifier(skinPointer.getIdentifier());
        this.lockSkin = false;
        this.skinDye = new SkinDye(skinPointer.getSkinDye());
    }
    
    public SkinPointer(SkinIdentifier identifier) {
        this.identifier = identifier;
        this.lockSkin = false;
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(SkinIdentifier identifier, SkinDye skinDye) {
        this.identifier = identifier;
        this.lockSkin = false;
        this.skinDye = skinDye;
    }
    
    public SkinPointer(SkinIdentifier identifier, boolean lockSkin) {
        this.identifier = identifier;
        this.lockSkin = lockSkin;
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(SkinIdentifier identifier, ISkinDye skinDye, boolean lockSkin) {
        this.identifier = identifier;
        this.lockSkin = lockSkin;
        this.skinDye = new SkinDye(skinDye);
    }
    
    @Override
    public SkinIdentifier getIdentifier() {
        return identifier;
    }
    
    @Deprecated
    @Override
    public int getSkinId() {
        return identifier.getSkinLocalId();
    }
    
    @Deprecated
    @Override
    public ISkinType getSkinType() {
        return identifier.getSkinType();
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
        this.lockSkin = skinDataCompound.getBoolean(TAG_SKIN_LOCK);
        this.skinDye.readFromCompound(skinDataCompound);
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        writeToCompound(compound, TAG_SKIN_DATA);
    }
    
    public void writeToCompound(NBTTagCompound compound, String tag) {
        NBTTagCompound skinDataCompound = new NBTTagCompound();
        SkinIdentifierSerializer.writeToCompound(identifier, skinDataCompound);
        skinDataCompound.setBoolean(TAG_SKIN_LOCK, this.lockSkin);
        skinDye.writeToCompound(skinDataCompound);
        compound.setTag(tag, skinDataCompound);
    }
}
