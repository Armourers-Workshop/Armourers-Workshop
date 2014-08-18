package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

public class CustomArmourData extends AbstractCustomArmour {

    private ArrayList<ArmourBlockData> armourData;
    private ArmourerType type;

    public CustomArmourData(ArrayList armourData, ArmourerType type) {
        this.armourData = armourData;
        this.type = type;
    }

    @Override
    public ArmourerType getArmourType() {
        return this.type;
    }

    @Override
    public ArrayList<ArmourBlockData> getArmourData() {
        return armourData;
    }
}
