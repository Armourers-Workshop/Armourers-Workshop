package riskyken.armourersWorkshop.common.skin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.config.ConfigHandler;

public class EquipmentWardrobeData {
    
    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_HAIR_COLOUR = "hairColour";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    private static final String TAG_HEAD_OVERLAY = "headOverlay";
    private static final String TAG_LIMIT_LIMBS = "limitLimbs";
    private static final String TAG_SLOTS_UNLOCKED = "slotsUnlocked";
    
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
    public int slotsUnlocked;
    
    public EquipmentWardrobeData() {
        skinColour = Color.decode("#F9DFD2").getRGB();
        hairColour = Color.decode("#804020").getRGB();
        armourOverride = new BitSet(4);
        headOverlay = false;
        limitLimbs = true;
        slotsUnlocked = ConfigHandler.startingWardrobeSlots;
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
        compound.setInteger(TAG_SLOTS_UNLOCKED, slotsUnlocked);
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
        if (compound.hasKey(TAG_SLOTS_UNLOCKED)) {
            this.slotsUnlocked = compound.getInteger(TAG_SLOTS_UNLOCKED);
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
        this.slotsUnlocked = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.skinColour);
        buf.writeInt(this.hairColour);
        for (int i = 0; i < 4; i++) {
            buf.writeBoolean(this.armourOverride.get(i));
        }
        buf.writeBoolean(this.headOverlay);
        buf.writeBoolean(this.limitLimbs);
        buf.writeInt(this.slotsUnlocked);
    }
    
    public int autoColourHair(AbstractClientPlayer player) {
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 3));
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
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 13));
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
