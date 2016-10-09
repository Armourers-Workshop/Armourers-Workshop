package riskyken.armourersWorkshop.common.skin.data;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinPointer implements ISkinPointer {

    public static final String TAG_SKIN_DATA = "armourersWorkshop";
    private static final String TAG_SKIN_TYPE = "skinType";
    private static final String TAG_SKIN_ID = "skinId";
    private static final String TAG_SKIN_LOCK = "lock";
    
    public ISkinType skinType;
    public int skinId;
    public boolean lockSkin;
    public SkinDye skinDye;
    
    public SkinPointer() {
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(Skin skin) {
        this(skin.getSkinType(), skin.lightHash());
    }
    
    public SkinPointer(ISkinPointer skinPointer) {
        this.skinType = skinPointer.getSkinType();
        this.skinId = skinPointer.getSkinId();
        this.lockSkin = false;
        this.skinDye = new SkinDye(skinPointer.getSkinDye());
    }
    
    public SkinPointer(ISkinType skinType, int skinId) {
        this.skinType = skinType;
        this.skinId = skinId;
        this.lockSkin = false;
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(ISkinType skinType, int skinId, SkinDye skinDye) {
        this.skinType = skinType;
        this.skinId = skinId;
        this.lockSkin = false;
        this.skinDye = skinDye;
    }
    
    public SkinPointer(ISkinType skinType, int skinId, boolean lockSkin) {
        this.skinType = skinType;
        this.skinId = skinId;
        this.lockSkin = lockSkin;
        this.skinDye = new SkinDye();
    }
    
    public SkinPointer(ISkinType skinType, int skinId, ISkinDye skinDye, boolean lockSkin) {
        this.skinType = skinType;
        this.skinId = skinId;
        this.lockSkin = lockSkin;
        this.skinDye = new SkinDye(skinDye);
    }
    
    @Override
    public int getSkinId() {
        return skinId;
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
        NBTTagCompound skinDataCompound = compound.getCompoundTag(TAG_SKIN_DATA);
        this.skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(skinDataCompound.getString(TAG_SKIN_TYPE));
        this.skinId = skinDataCompound.getInteger(TAG_SKIN_ID);
        this.lockSkin = skinDataCompound.getBoolean(TAG_SKIN_LOCK);
        this.skinDye.readFromCompound(skinDataCompound);
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        NBTTagCompound skinDataCompound = new NBTTagCompound();
        skinDataCompound.setString(TAG_SKIN_TYPE, this.skinType.getRegistryName());
        skinDataCompound.setInteger(TAG_SKIN_ID, this.skinId);
        skinDataCompound.setBoolean(TAG_SKIN_LOCK, this.lockSkin);
        skinDye.writeToCompound(skinDataCompound);
        compound.setTag(TAG_SKIN_DATA, skinDataCompound);
    }
}
