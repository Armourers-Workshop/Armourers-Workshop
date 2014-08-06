package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import riskyken.armourersWorkshop.common.ArmourerType;

public abstract class AbstractCustomArmour {
	
	public abstract ArmourerType getArmourType();
	
	public abstract ArrayList<ArmourBlockData> getArmourData();
}
