package riskyken.armourersWorkshop.proxies;

import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;

public class ServerProxy extends CommonProxy {

    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
    }

    @Override
    public void postInit() {
    }

    @Override
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourType type) {
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
    }

    @Override
    public boolean playerHasSkirt(String playerName) {
        return false;
    }
}
