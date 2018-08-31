package riskyken.armourers_workshop.common.skin.type.legs;

import java.util.ArrayList;

import riskyken.armourers_workshop.api.common.skin.data.ISkinProperty;
import riskyken.armourers_workshop.api.common.skin.type.ISkinPartType;
import riskyken.armourers_workshop.common.skin.data.SkinProperties;
import riskyken.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinLegs extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinLegs() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinLegsPartLeftLeg(this));
        skinParts.add(new SkinLegsPartRightLeg(this));
        skinParts.add(new SkinLegsPartSkirt(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:legs";
    }
    
    @Override
    public String getName() {
        return "Legs";
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_ARMOUR_OVERRIDE);
        return properties;
    }
}
