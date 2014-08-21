package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

public class CustomArmourData extends AbstractCustomArmour {

    private ArrayList<ArmourBlockData> armourData;
    private ArmourerType type;
    private ArmourPart part;

    public CustomArmourData(ArrayList armourData, ArmourerType type, ArmourPart part) {
        this.armourData = armourData;
        this.type = type;
        this.part = part;
    }

    @Override
    public ArmourerType getArmourType() {
        return this.type;
    }

    @Override
    public ArmourPart getArmourPart() {
        return this.part;
    }
    
    @Override
    public ArrayList<ArmourBlockData> getArmourData() {
        return armourData;
    }
}
