package riskyken.armourers_workshop.api.common;

import riskyken.armourers_workshop.api.common.skin.type.ISkinType;

public interface IArmourersSkinnableItem {

    public boolean isSkinValidForItem(ISkinType skinType);
}
