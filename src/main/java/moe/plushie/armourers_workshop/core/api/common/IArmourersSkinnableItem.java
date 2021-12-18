package moe.plushie.armourers_workshop.core.api.common;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;

public interface IArmourersSkinnableItem {

    public boolean isSkinValidForItem(ISkinType skinType);
}
