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
    
    public boolean isNaked;
    public int skinColour;
    public int pantsColour;
    public int pantStripeColour;
    public BitSet armourOverride;
    public boolean headOverlay;
    
    public EntityNakedInfo() {
        isNaked = false;
        skinColour = Color.decode("#F9DFD2").getRGB();
        pantsColour = Color.decode("#FCFCFC").getRGB();
        pantStripeColour = Color.decode("#FCFCFC").getRGB();
        armourOverride = new BitSet(4);
        headOverlay = false;
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
        this.headOverlay = compound.getBoolean(TAG_HEAD_OVERLAY);
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
    }
}
