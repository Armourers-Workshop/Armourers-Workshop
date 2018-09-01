package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

public interface IArmourersSkinnableItem {

    public boolean isSkinValidForItem(ISkinType skinType);
}
