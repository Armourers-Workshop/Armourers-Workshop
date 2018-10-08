package moe.plushie.armourers_workshop.common.skin;

import java.awt.Color;
import java.util.BitSet;
import java.util.HashMap;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;

public class PlayerWardrobe {
    
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
    
    public PlayerWardrobe() {
        skinColour = COLOUR_SKIN_DEFAULT.getRGB();
        hairColour = COLOUR_HAIR_DEFAULT.getRGB();
        armourOverride = new BitSet(4);
        headOverlay = false;
        limitLimbs = true;
        slotsUnlocked = new HashMap<String, Integer>();
        /*
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            slotsUnlocked.put(skinType.getRegistryName(), getUnlockedSlotsForSkinType(skinType));
        }
        */
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
    
    public PlayerWardrobe(PlayerWardrobe ewd) {
        skinColour = ewd.skinColour;
        hairColour = ewd.hairColour;
        armourOverride = (BitSet) ewd.armourOverride.clone();
        headOverlay = ewd.headOverlay;
        limitLimbs = ewd.limitLimbs;
        slotsUnlocked = ewd.slotsUnlocked;
    }
}
