package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.skin.ISkinType;

public interface IArmourersSkinnableItem {

    public boolean isSkinValidForItem(ISkinType skinType);
}
