package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

public class CustomArmourChestData extends AbstractCustomArmour {

    private ArrayList<ArmourBlockData> armourData;

    public CustomArmourChestData(ArrayList armourData) {
        this.armourData = armourData;
    }

    @Override
    public ArmourerType getArmourType() {
        return ArmourerType.CHEST;
    }

    @Override
    public ArrayList<ArmourBlockData> getArmourData() {
        return armourData;
    }
}
