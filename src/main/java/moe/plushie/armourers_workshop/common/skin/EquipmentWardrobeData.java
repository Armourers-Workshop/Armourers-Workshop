package moe.plushie.armourers_workshop.common.skin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.SkinHelper;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class EquipmentWardrobeData {
    
    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_HAIR_COLOUR = "hairColour";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    private static final String TAG_HEAD_OVERLAY = "headOverlay";
    private static final String TAG_LIMIT_LIMBS = "limitLimbs";
    private static final String TAG_SLOTS_UNLOCKED = "slotsUnlocked";
    private static final String TAG_SLOT_KEY = "slotKey";
    private static final String TAG_SLOT_VALUE = "slotValue";
    
    private static final Color COLOUR_SKIN_DEFAULT = Color.decode("#F9DFD2");
    private static final Color COLOUR_HAIR_DEFAULT = Color.decode("#804020");
    
    /** Colour of the players skin */
    public int skinColour;
    /** Colour of the players hair */
    public int hairColour;
    /** Bit set of what armour is hidden on the player. */
    public BitSet armourOverride;
    /** Is the hair/hat overlay hidden? */
    public boolean headOverlay;
    /** Should limb movement be limited when the player has a skin on? */
    public boolean limitLimbs;
    /** Number of slots the player has unlocked in the wardrobe */
    public HashMap<String, Integer> slotsUnlocked;
    
    public EquipmentWardrobeData() {
        skinColour = COLOUR_SKIN_DEFAULT.getRGB();
        hairColour = COLOUR_HAIR_DEFAULT.getRGB();
        armourOverride = new BitSet(4);
        headOverlay = false;
        limitLimbs = true;
        slotsUnlocked = new HashMap<String, Integer>();
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            slotsUnlocked.put(skinType.getRegistryName(), getUnlockedSlotsForSkinType(skinType));
        }
    }
    
    public int getUnlockedSlotsForSkinType(ISkinType skinType) {
        if (skinType == SkinTypeRegistry.skinBow) {
            return 1;
        }
        if (skinType == SkinTypeRegistry.skinSword) {
            return 5;
        }
        if (slotsUnlocked.containsKey(skinType.getRegistryName())) {
            return slotsUnlocked.get(skinType.getRegistryName());
        } else {
            return ConfigHandler.startingWardrobeSlots;
        }
    }
    
    public void setUnlockedSlotsForSkinType(ISkinType skinType, int value) {
        slotsUnlocked.put(skinType.getRegistryName(), value);
    }
    
    public EquipmentWardrobeData(EquipmentWardrobeData ewd) {
        skinColour = ewd.skinColour;
        hairColour = ewd.hairColour;
        armourOverride = (BitSet) ewd.armourOverride.clone();
        headOverlay = ewd.headOverlay;
        limitLimbs = ewd.limitLimbs;
        slotsUnlocked = ewd.slotsUnlocked;
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger(TAG_SKIN_COLOUR, this.skinColour);
        compound.setInteger(TAG_HAIR_COLOUR, this.hairColour);
        for (int i = 0; i < 4; i++) {
            compound.setBoolean(TAG_ARMOUR_OVERRIDE + i, this.armourOverride.get(i));
        }
        compound.setBoolean(TAG_HEAD_OVERLAY, this.headOverlay);
        compound.setBoolean(TAG_LIMIT_LIMBS, this.limitLimbs);
        
        NBTTagList slotsList = new NBTTagList();
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            NBTTagCompound slotCount = new NBTTagCompound();
            slotCount.setString(TAG_SLOT_KEY, skinType.getRegistryName());
            slotCount.setInteger(TAG_SLOT_VALUE, getUnlockedSlotsForSkinType(skinType));
            slotsList.appendTag(slotCount);
        }
        compound.setTag(TAG_SLOTS_UNLOCKED, slotsList);
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey(TAG_SKIN_COLOUR)) {
            this.skinColour = compound.getInteger(TAG_SKIN_COLOUR);
        }
        if (compound.hasKey(TAG_HAIR_COLOUR)) {
            this.hairColour = compound.getInteger(TAG_HAIR_COLOUR);
        }
        for (int i = 0; i < 4; i++) {
            this.armourOverride.set(i, compound.getBoolean(TAG_ARMOUR_OVERRIDE + i));
        }
        if (compound.hasKey(TAG_HEAD_OVERLAY)) {
            this.headOverlay = compound.getBoolean(TAG_HEAD_OVERLAY);
        }
        if (compound.hasKey(TAG_LIMIT_LIMBS)) {
            this.limitLimbs = compound.getBoolean(TAG_LIMIT_LIMBS);
        }
        if (compound.hasKey(TAG_SLOTS_UNLOCKED, NBT.TAG_LIST)) {
            NBTTagList slotsList = compound.getTagList(TAG_SLOTS_UNLOCKED, NBT.TAG_COMPOUND);
            this.slotsUnlocked.clear();
            for (int i = 0; i < slotsList.tagCount(); i++) {
                NBTTagCompound slotCount = slotsList.getCompoundTagAt(i);
                if (slotCount.hasKey(TAG_SLOT_KEY, NBT.TAG_STRING) & slotCount.hasKey(TAG_SLOT_VALUE, NBT.TAG_INT)) {
                    String key = slotCount.getString(TAG_SLOT_KEY);
                    int value = slotCount.getInteger(TAG_SLOT_VALUE);
                    slotsUnlocked.put(key, value);
                }
            }
        }
    }
    
    public void fromBytes(ByteBuf buf) {
        this.skinColour = buf.readInt();
        this.hairColour = buf.readInt();
        this.armourOverride = new BitSet(4);
        for (int i = 0; i < 4; i++) {
            this.armourOverride.set(i, buf.readBoolean());
        }
        this.headOverlay = buf.readBoolean();
        this.limitLimbs = buf.readBoolean();
        for (int i = 0; i < ExPropsPlayerSkinData.validSkins.length; i++) {
            String key = ByteBufUtils.readUTF8String(buf);
            int value = buf.readInt();
            slotsUnlocked.put(key, value);
        }
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.skinColour);
        buf.writeInt(this.hairColour);
        for (int i = 0; i < 4; i++) {
            buf.writeBoolean(this.armourOverride.get(i));
        }
        buf.writeBoolean(this.headOverlay);
        buf.writeBoolean(this.limitLimbs);
        
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            ByteBufUtils.writeUTF8String(buf, skinType.getRegistryName());
            buf.writeInt(getUnlockedSlotsForSkinType(skinType));
        }
    }
    
    public int autoColourHair(AbstractClientPlayer player) {
        BufferedImage playerTexture = SkinHelper.getBufferedImageSkin(player);
        if (playerTexture == null) {
            return COLOUR_HAIR_DEFAULT.getRGB();
        }
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerTexture.getRGB(ix + 11, iy + 3));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
    
    public int autoColourSkin(AbstractClientPlayer player) {
        BufferedImage playerTexture = SkinHelper.getBufferedImageSkin(player);
        if (playerTexture == null) {
            return COLOUR_SKIN_DEFAULT.getRGB();
        }
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerTexture.getRGB(ix + 11, iy + 13));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
}
