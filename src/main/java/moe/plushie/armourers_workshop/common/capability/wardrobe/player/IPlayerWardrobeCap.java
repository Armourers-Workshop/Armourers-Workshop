package moe.plushie.armourers_workshop.common.capability.wardrobe.player;

import java.util.BitSet;

import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;

public interface IPlayerWardrobeCap extends IWardrobeCap {
    
    public BitSet getArmourOverride();
    
    public void setArmourOverride(BitSet armourOverride);
}
