package riskyken.armourersWorkshop.common.skin.data;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinPointer implements ISkinPointer {

    public static final String TAG_SKIN_DATA = "armourersWorkshop";
    private static final String TAG_SKIN_TYPE = "skinType";
    private static final String TAG_SKIN_LOCK = "lock";
    
    public ISkinType skinType;
    private SkinIdentifier identifier;
    
    public boolean lockSkin;
    public SkinDye skinDye;
    
    public SkinPointer() {
        this.skinDye = new SkinDye();
        this.identifier = new SkinIdentifier(0, null, 0);
    }
    
    public SkinPointer(Skin skin) {
        this(skin.getSkinType(), new SkinIdentifier(skin.lightHash(), null, 0));
    }
    
    public SkinPointer(ISkinPointer skinPointer) {
        this.skinType = skinPointer.getSkinType();
        this.identifier = new SkinIdentifier(skinPointer.getIdentifier());
        this.lockSkin = false;
        this.skinDye = new SkinDye(skinPointer.getSkinDye());
    }
    
    public SkinPointer(ISkinType skinType, SkinIdentifier identifier) {
        this.skinType = skinType;
        this.identifier = identifier;
        this.lockSkin = false;
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(ISkinType skinType, SkinIdentifier identifier, SkinDye skinDye) {
        this.skinType = skinType;
        this.identifier = identifier;
        this.lockSkin = false;
        this.skinDye = skinDye;
    }
    
    public SkinPointer(ISkinType skinType, SkinIdentifier identifier, boolean lockSkin) {
        this.skinType = skinType;
        this.identifier = identifier;
        this.lockSkin = lockSkin;
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(ISkinType skinType, SkinIdentifier identifier, ISkinDye skinDye, boolean lockSkin) {
        this.skinType = skinType;
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
    
    @Override
    public ISkinType getSkinType() {
        return skinType;
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
        this.skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(skinDataCompound.getString(TAG_SKIN_TYPE));
        this.identifier.readFromCompound(skinDataCompound);
        this.lockSkin = skinDataCompound.getBoolean(TAG_SKIN_LOCK);
        this.skinDye.readFromCompound(skinDataCompound);
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        writeToCompound(compound, TAG_SKIN_DATA);
    }
    
    public void writeToCompound(NBTTagCompound compound, String tag) {
        NBTTagCompound skinDataCompound = new NBTTagCompound();
        skinDataCompound.setString(TAG_SKIN_TYPE, this.skinType.getRegistryName());
        this.identifier.writeToCompound(skinDataCompound);
        skinDataCompound.setBoolean(TAG_SKIN_LOCK, this.lockSkin);
        skinDye.writeToCompound(skinDataCompound);
        compound.setTag(tag, skinDataCompound);
    }
}
