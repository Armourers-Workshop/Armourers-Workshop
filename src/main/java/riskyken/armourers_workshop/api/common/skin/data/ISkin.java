package riskyken.armourers_workshop.api.common.skin.data;

import java.util.ArrayList;

import riskyken.armourers_workshop.api.common.skin.type.ISkinType;

public interface ISkin {
    
    public ISkinType getSkinType();
    
    public ArrayList<ISkinPart> getSubParts();
}
