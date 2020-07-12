package moe.plushie.armourers_workshop.common.skin.advanced;

import moe.plushie.armourers_workshop.common.skin.data.SkinPart;

public interface IAdvancedPartParent {
    
    public SkinPart getAdvancedPart(int index);
    
    public AdvancedPartNode getAdvancedPartNode(int index);
}
