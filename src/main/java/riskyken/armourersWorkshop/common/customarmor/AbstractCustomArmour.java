package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

import riskyken.armourersWorkshop.common.ArmourerType;

public abstract class AbstractCustomArmour {

    public abstract ArmourerType getArmourType();

    public abstract ArrayList<ArmourBlockData> getArmourData();
}
