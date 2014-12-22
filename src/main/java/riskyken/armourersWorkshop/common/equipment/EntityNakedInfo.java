package riskyken.armourersWorkshop.common.equipment;

import io.netty.buffer.ByteBuf;

import java.awt.Color;
import java.util.BitSet;

import net.minecraft.nbt.NBTTagCompound;

public class EntityNakedInfo {
    
    private static final String TAG_NAKED = "naked";
    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_PANTS_COLOUR = "pantsColour";
    private static final String TAG_PANT_STRIPE_COLOUR = "pantStripeColour";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    private static final String TAG_HEAD_OVERLAY = "headOverlay";
    private static final String TAG_LIMIT_LIMBS = "limitLimbs";
    
    /** Is the players naked skin active? */
    public boolean isNaked;
    /** Colour use for the players naked skin */
    public int skinColour;
    /** 1st colour use for the players panties when naked! */
    public int pantsColour;
    /** 2nd colour use for the players panties when naked! */
    public int pantStripeColour;
    /** Bit set of what armour is hidden on the player. */
    public BitSet armourOverride;
    /** Is the hair/hat overlay hidden? */
    public boolean headOverlay;
    /** Should limb movement be limited when the player has a skin on? */
    public boolean limitLimbs;
    
    public EntityNakedInfo() {
        isNaked = false;
        skinColour = Color.decode("#F9DFD2").getRGB();
        pantsColour = Color.decode("#FCFCFC").getRGB();
        pantStripeColour = Color.decode("#FCFCFC").getRGB();
        armourOverride = new BitSet(4);
        headOverlay = false;
        limitLimbs = true;
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        compound.setBoolean(TAG_NAKED, this.isNaked);
        compound.setInteger(TAG_SKIN_COLOUR, this.skinColour);
        compound.setInteger(TAG_PANTS_COLOUR, this.pantsColour);
        compound.setInteger(TAG_PANT_STRIPE_COLOUR, this.pantStripeColour);
        for (int i = 0; i < 4; i++) {
            compound.setBoolean(TAG_ARMOUR_OVERRIDE + i, this.armourOverride.get(i));
        }
        compound.setBoolean(TAG_HEAD_OVERLAY, this.headOverlay);
        compound.setBoolean(TAG_LIMIT_LIMBS, this.limitLimbs);
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        this.isNaked = compound.getBoolean(TAG_NAKED);
        if (compound.hasKey(TAG_SKIN_COLOUR)) {
            this.skinColour = compound.getInteger(TAG_SKIN_COLOUR);
        }
        if (compound.hasKey(TAG_PANTS_COLOUR)) {
            this.pantsColour = compound.getInteger(TAG_PANTS_COLOUR);
        }
        if (compound.hasKey(TAG_PANT_STRIPE_COLOUR)) {
            this.pantStripeColour = compound.getInteger(TAG_PANT_STRIPE_COLOUR);
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
    }
    
    public void fromBytes(ByteBuf buf) {
        this.isNaked = buf.readBoolean();
        this.skinColour = buf.readInt();
        this.pantsColour = buf.readInt();
        this.pantStripeColour = buf.readInt();
        this.armourOverride = new BitSet(4);
        for (int i = 0; i < 4; i++) {
            this.armourOverride.set(i, buf.readBoolean());
        }
        this.headOverlay = buf.readBoolean();
        this.limitLimbs = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isNaked);
        buf.writeInt(this.skinColour);
        buf.writeInt(this.pantsColour);
        buf.writeInt(this.pantStripeColour);
        for (int i = 0; i < 4; i++) {
            buf.writeBoolean(this.armourOverride.get(i));
        }
        buf.writeBoolean(this.headOverlay);
        buf.writeBoolean(this.limitLimbs);
    }
}
